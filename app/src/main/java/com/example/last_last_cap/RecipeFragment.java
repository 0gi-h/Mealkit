package com.example.last_last_cap;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RecipeFragment extends Fragment {

    private SearchView searchView;
    private ListView listView;
    private IngredientAdapter adapter;
    private List<String> data;

    public RecipeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe, container, false);


        searchView = view.findViewById(R.id.searchView);
        listView = view.findViewById(R.id.listView);
        adapter = new IngredientAdapter(requireContext(), R.layout.ingredient_item, new ArrayList<>());
        listView.setAdapter(adapter);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // 현재 로그인한 사용자의 UID로 목록 가져옴
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userUID = currentUser.getUid();

            // 냉장고 재료 가져오는 부분
            db.collection("users").document(userUID).collection("ingredients").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        List<String> userIngredients = new ArrayList<>();
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            String ingredientName = documentSnapshot.getString("name");
                            if (ingredientName != null) {
                                userIngredients.add(ingredientName);
                            }
                        }
                        adapter.clear();
                        adapter.addAll(userIngredients);
                        adapter.notifyDataSetChanged();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // 오류 처리
                }
            });
        }

        adapter = new IngredientAdapter(requireContext(), R.layout.ingredient_item, new ArrayList<>());
        listView.setAdapter(adapter);

        Button searchButton = view.findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> selectedIngredients = new ArrayList<>();
                for (int i = 0; i < adapter.getCount(); i++) {
                    if (adapter.isChecked(i)) {
                        selectedIngredients.add(adapter.getItem(i));
                    }
                }
                fetchRecipesWithIngredients(selectedIngredients);
            }
        });

        searchView.setIconifiedByDefault(false);
        searchView.setQueryHint("검색어를 입력하시오");

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = adapter.getItem(position);
            }
        });

        return view;
    }

    //firestore 구조가 좀 바껴야할듯 현재 테스트로 김치찌개 제육볶음만 설정해놓음
    //주재료를 먼저 알아야하고 그 주재료에 대한 요리들을 저장시켜놔야함
    private void fetchRecipesWithIngredients(List<String> ingredients) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        List<String> recipeNames = new ArrayList<>();

        for (String ingredient : ingredients) {
            db.collection("mainFood").document(ingredient)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                String recipesString = documentSnapshot.getString("recipes");
                                if (recipesString != null && !recipesString.isEmpty()) {
                                    String[] recipesArray = recipesString.split(","); // recipes필드에 쉼표로 구분해서 배열에 저장
                                    Collections.addAll(recipeNames, recipesArray);
                                }
                            }
                            if (ingredient.equals(ingredients.get(ingredients.size() - 1))) {
                                showRecipeDialog(recipeNames);
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        // 오류 처리
                    });
        }
    }


    //선택한 주재료에 대한 가능한 요리들 보여주는 다이얼로그, 요리 선택하면 재료 비교함
    private void showRecipeDialog(List<String> recipes) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("조회된 요리 목록");

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, recipes);

        builder.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String selectedRecipe = arrayAdapter.getItem(which);
                fetchRecipeIngredientsAndCompare(selectedRecipe);
            }
        });

        builder.setNegativeButton("닫기", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // firestore에서 선택된 요리의 재료 목록을 가져옴
    //ex) 김치찌개 선택했으면 김치찌개의 재료들을 불러오는거임
    private void fetchRecipeIngredientsAndCompare(String recipe) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("foods").document(recipe)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String ingredientsString = documentSnapshot.getString("ingredients");
                            if (ingredientsString != null && !ingredientsString.isEmpty()) {
                                List<String> recipeIngredients = Arrays.asList(ingredientsString.split(","));
                                compareIngredients(recipeIngredients, recipe);
                            }
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    // 오류 처리
                });
    }

    // firestore에서 가져온 재료와 사용자 재료를 비교
    //
    private void compareIngredients(List<String> recipeIngredients, String selectedRecipe) {
        List<String> userIngredients = adapter.getItems(); // 사용자 재료
        List<String> availableIngredients = new ArrayList<>();
        List<String> missingIngredients = new ArrayList<>();

        for (String ingredient : recipeIngredients) {
            if (userIngredients.contains(ingredient)) { //contains로 확인하고
                availableIngredients.add(ingredient); //있으면 availableIngredients에 추가
            } else {
                missingIngredients.add(ingredient); //없으면 missingIngredients에 추가
            }
        }

        showComparisonResult(availableIngredients, missingIngredients, selectedRecipe);
    }

    // 재료 비교한 결과 보여주는 다이얼로그
    private void showComparisonResult(List<String> available, List<String> missing, String selectedRecipe) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("재료 확인");

        String availableIngredients = "가지고 있는 재료:\n" + TextUtils.join(", ", available);
        String missingIngredients = "없는 재료:\n" + TextUtils.join(", ", missing);

        builder.setMessage(availableIngredients + "\n\n" + missingIngredients);

        builder.setPositiveButton("만개의 레시피 보기", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                fetchRecipeData(selectedRecipe);
            }
        });

        builder.setNegativeButton("닫기", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }




    //만갱의 레시피 크롤링
    private void fetchRecipeData(String selectedRecipe) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            List<String> result = new ArrayList<>();
            String searchUrl = "https://www.10000recipe.com/recipe/list.html?q=" + selectedRecipe;
            try {
                Document document = Jsoup.connect(searchUrl).get();
                Element firstRecipe = document.select(".common_sp_list_ul li").first();
                if (firstRecipe != null) {
                    Element recipeLinkElement = firstRecipe.select("a[href]").first();
                    if (recipeLinkElement != null) {
                        String recipeLink = "https://www.10000recipe.com" + recipeLinkElement.attr("href");
                        Document recipeDocument = Jsoup.connect(recipeLink).get();
                        Elements stepDivs = recipeDocument.select("[id^=stepDiv]");
                        int stepCount = 1;
                        for (Element stepDiv : stepDivs) {
                            String content = stepDiv.select(".media-body").text();
                            result.add("Step " + stepCount + ": " + content);
                            stepCount++;
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            handler.post(() -> {
                Intent intent = new Intent(getContext(), RecipeActivity.class);
                intent.putStringArrayListExtra("recipeSteps", new ArrayList<>(result));
                getContext().startActivity(intent);
            });
        });
    }


}
