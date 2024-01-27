package com.example.last_last_cap;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RecipeListActivity extends AppCompatActivity implements RecipeClickListener {

    private RecyclerView recipesRecyclerView;
    private RecipeAdapter adapter;


    private int extractCookTimeInSeconds(String description) {
        // 정규 표현식을 사용하여 텍스트에서 시간 추출
        Pattern pattern = Pattern.compile("(\\d+)\\s*분");
        Matcher matcher = pattern.matcher(description);

        if (matcher.find()) {
            try {
                // 정규 표현식에서 추출한 그룹을 정수로 변환
                int minutes = Integer.parseInt(matcher.group(1));
                return minutes * 60; // 분을 초로 변환
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        return 0; // 추출 실패 시 기본값 0 반환
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);

        recipesRecyclerView = findViewById(R.id.recipesRecyclerView);
        recipesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<RecipeData> recipes = (List<RecipeData>) getIntent().getSerializableExtra("recipes");
        adapter = new RecipeAdapter(recipes, this); // 'this'는 RecipeListActivity 인스턴스
        recipesRecyclerView.setAdapter(adapter);
    }

//    public void onRecipeClicked(String detailUrl) {
//        ExecutorService executor = Executors.newSingleThreadExecutor();
//        Handler handler = new Handler(Looper.getMainLooper());
//
//        executor.execute(() -> {
//            List<RecipeStep> recipeSteps = new ArrayList<>();
//            try {
//                Document document = Jsoup.connect(detailUrl).get();
//
//                // 각 레시피 단계를 크롤링
//                Elements steps = document.select(".view_step_cont");
//                for (Element step : steps) {
//                    String description = step.select(".media-body").text();
//                    String imageUrl = step.select(".media-right img").attr("src");
//
//                    int cookTimeInSeconds = extractCookTimeInSeconds(description);
//
//                    Log.d("RecipeImage", "Image URL: " + imageUrl);
//
//                    recipeSteps.add(new RecipeStep(description, imageUrl, cookTimeInSeconds));
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            handler.post(() -> {
//                // 새로운 Activity에 크롤링한 데이터 전달
//                Intent intent = new Intent(RecipeListActivity.this, RecipeStepsActivity.class);
//                intent.putExtra("recipeSteps", (ArrayList<RecipeStep>) recipeSteps);
//                startActivity(intent);
//            });
//        });
//    }

    public void onRecipeClicked(String detailUrl) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            List<RecipeStep> recipeSteps = new ArrayList<>();
            try {
                Document document = Jsoup.connect(detailUrl).get();
                // 각 레시피 단계를 크롤링
                Elements steps = document.select(".view_step_cont");
                for (int i = 1; i <= 20; i++) { // 최대 stepimg1에서 stepimg20까지를 가정
                    String imageSelector = "#stepimg" + i + " img";
                    Elements stepImages = steps.select(imageSelector);

                    if (!stepImages.isEmpty()) {
                        Element stepImage = stepImages.first();
                        String description = steps.select(".media-body").get(i - 1).text(); // 인덱스 조정

                        if (description.contains(".")) {
                            // 마침표를 기준으로 문장을 나누고 줄바꿈으로 연결
                            String[] sentences = description.split("\\.");
                            StringBuilder formattedDescription = new StringBuilder();
                            for (String sentence : sentences) {
                                formattedDescription.append(sentence.trim()).append(".\n"); // 마침표를 다시 추가
                            }
                            description = formattedDescription.toString().trim(); // 마지막에 추가된 줄바꿈을 제거
                        }

                        // 이미지 URL을 ID를 사용하여 크롤링
                        String imageUrl = stepImage.attr("src");
                        int cookTimeInSeconds = extractCookTimeInSeconds(description);

                        Log.d("RecipeImage", "Image URL: " + imageUrl);
                        recipeSteps.add(new RecipeStep(description, imageUrl, cookTimeInSeconds));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            handler.post(() -> {
                // 새로운 Activity에 크롤링한 데이터 전달
                Intent intent = new Intent(RecipeListActivity.this, RecipeStepsActivity.class);
                intent.putExtra("recipeSteps", (ArrayList<RecipeStep>) recipeSteps);
                startActivity(intent);
            });
        });
    }
}