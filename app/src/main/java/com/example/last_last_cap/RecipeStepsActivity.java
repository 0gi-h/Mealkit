package com.example.last_last_cap;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.List;
public class RecipeStepsActivity extends AppCompatActivity {

    private ViewPager2 recipeStepsViewPager;
    private RecipeStepsAdapter adapter;
    private TabLayout tabLayout;
    private Button leftArrowButton, rightArrowButton; // 화살표 버튼 추가

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_steps);

        recipeStepsViewPager = findViewById(R.id.recipeStepsViewPager);
        leftArrowButton = findViewById(R.id.leftArrowButton); // 화살표 버튼 초기화
        rightArrowButton = findViewById(R.id.rightArrowButton);

        List<RecipeStep> recipeSteps = (List<RecipeStep>) getIntent().getSerializableExtra("recipeSteps");
        tabLayout = findViewById(R.id.tabLayout);
        adapter = new RecipeStepsAdapter(recipeSteps);
        recipeStepsViewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, recipeStepsViewPager, (tab, position) -> {
            tab.setText("" + (position + 1));
        }).attach();

        // 왼쪽 화살표 버튼 클릭 리스너
        leftArrowButton.setOnClickListener(v -> {
            int currentItem = recipeStepsViewPager.getCurrentItem();
            if (currentItem > 0) {
                recipeStepsViewPager.setCurrentItem(currentItem - 1);
            }
        });

        // 오른쪽 화살표 버튼 클릭 리스너
        rightArrowButton.setOnClickListener(v -> {
            int currentItem = recipeStepsViewPager.getCurrentItem();
            if (currentItem < adapter.getItemCount() - 1) {
                recipeStepsViewPager.setCurrentItem(currentItem + 1);
            }
        });
    }
}





