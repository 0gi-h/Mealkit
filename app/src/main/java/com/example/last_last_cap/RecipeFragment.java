package com.example.last_last_cap;

import android.os.AsyncTask;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RecipeFragment extends Fragment {

    private SearchView searchView;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> data;

    public RecipeFragment() {
        // 필요한 공개 생성자
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe, container, false);

        searchView = view.findViewById(R.id.searchView);
        listView = view.findViewById(R.id.listView);
        data = new ArrayList<>();
        data.add("백종원 김치찌개");
        data.add("맛있는 김치찌개");
        data.add("매콤한 김치찌개");
        adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, data);
        listView.setAdapter(adapter);

        searchView.setIconifiedByDefault(false);
        searchView.setQueryHint("검색어를 입력하시오");

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = adapter.getItem(position);
                new FetchRecipeTask().execute(selectedItem);
            }
        });

        return view;
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
