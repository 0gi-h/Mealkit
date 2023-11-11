package com.example.last_last_cap;

import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
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
import java.util.Collections;
import java.util.List;

public class RecipeFragment extends Fragment {

    private SearchView searchView;
    private ListView listView;
    private IngredientAdapter adapter;
    private List<String> data;

    public RecipeFragment() {
        // 필요한 공개 생성자
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe, container, false);


        searchView = view.findViewById(R.id.searchView);
        listView = view.findViewById(R.id.listView);
        adapter = new IngredientAdapter(requireContext(), R.layout.ingredient_item, new ArrayList<>());
        listView.setAdapter(adapter);

        // Firebase Firestore 인스턴스를 가져옵니다.
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // 현재 로그인한 사용자의 UID를 가져옵니다.
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userUID = currentUser.getUid();

            // 사용자의 재료 목록을 조회합니다.
            db.collection("users").document(userUID).collection("ingredients").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        List<String> userIngredients = new ArrayList<>();
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            // 각 문서에서 'name' 필드를 가져옵니다.
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
        // adapter 초기화
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
                // 해당 재료를 사용하는 레시피 검색 등의 추가 기능 구현
            }
        });

        return view;
    }

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
                                    String[] recipesArray = recipesString.split(","); // 쉼표로 구분하여 분리
                                    Collections.addAll(recipeNames, recipesArray); // 배열을 리스트에 추가
                                }
                            }
                            // 모든 재료에 대한 쿼리가 완료되었을 때 다이얼로그를 표시
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




    private void showRecipeDialog(List<String> recipes) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("조회된 요리 목록");

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, recipes);
        builder.setAdapter(arrayAdapter, null);

        builder.setNegativeButton("닫기", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }



    private class FetchRecipeTask extends AsyncTask<String, Void, List<String>> {
        @Override
        protected List<String> doInBackground(String... strings) {
            List<String> results = new ArrayList<>();
            String searchQuery = strings[0];
            String searchUrl = "https://www.10000recipe.com/recipe/list.html?q=" + searchQuery;
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
                            results.add("Step" + stepCount + ": " + content); // 여기에서 "Step" + stepCount 로 스텝 번호를 부여합니다.
                            stepCount++;
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return results;
        }

        @Override
        protected void onPostExecute(List<String> results) {
            super.onPostExecute(results);

            LinearLayout resultLayout = getView().findViewById(R.id.resultLayout);
            resultLayout.removeAllViews(); // 이전 결과를 삭제합니다.

            for (String result : results) {
                TextView textView = new TextView(getContext());
                textView.setText(result);
                textView.setPadding(0, 8, 0, 8);
                resultLayout.addView(textView);
            }
        }
    }
}
