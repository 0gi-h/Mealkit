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

public class RecipeListActivity extends AppCompatActivity implements RecipeClickListener {

    private RecyclerView recipesRecyclerView;
    private RecipeAdapter adapter;


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

    public void onRecipeClicked(String detailUrl) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            List<RecipeStep> recipeSteps = new ArrayList<>();
            try {
                Document document = Jsoup.connect(detailUrl).get();

                // 각 레시피 단계를 크롤링
                Elements steps = document.select(".view_step_cont");
                for (Element step : steps) {
                    String description = step.select(".media-body").text();
                    String imageUrl = step.select(".media-right img").attr("src");

                    Log.d("RecipeImage", "Image URL: " + imageUrl);

                    recipeSteps.add(new RecipeStep(description, imageUrl));
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

