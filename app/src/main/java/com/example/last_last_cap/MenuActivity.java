package com.example.last_last_cap;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

public class MenuActivity extends AppCompatActivity {

    TextView show;
    ViewPager viewPager;
    TabLayout tabLayout;
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        show=findViewById(R.id.show_original_num);
        Intent intent = getIntent();
        String original_number = getIntent().getStringExtra("original_number");
        show.setText(original_number+"님의 냉장고입니다.");
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);



    }
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new FridgeFragment(), "냉장고");
        adapter.addFragment(new RecipeFragment(), "레시피");
        adapter.addFragment(new AllergyFragment(), "알러지");
        adapter.addFragment(new BookmarkFragment(), "북마크");
        viewPager.setAdapter(adapter);
    }

}
