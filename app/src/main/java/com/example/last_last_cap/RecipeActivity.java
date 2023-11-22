package com.example.last_last_cap;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
public class RecipeActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecipeStepsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        // RecipeStep 객체 리스트를 받아옵니다.
        List<RecipeStep> recipeSteps = (List<RecipeStep>) getIntent().getSerializableExtra("recipeSteps");

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecipeStepsAdapter(recipeSteps);
        recyclerView.setAdapter(adapter);
    }
}


