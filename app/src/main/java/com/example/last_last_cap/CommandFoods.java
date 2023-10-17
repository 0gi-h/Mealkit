package com.example.last_last_cap;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class CommandFoods extends Activity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private TextView tvMatchedFoods;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_fridge);

        Button btnCheckMatchedFoods = findViewById(R.id.btnCheckMatchedFoods);
        tvMatchedFoods = findViewById(R.id.tvMatchedFoods);

        btnCheckMatchedFoods.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkMatchedFoods();
            }
        });
    }

    private void checkMatchedFoods() {
        List<String> userIngredients = new ArrayList<>();
        StringBuilder matchedFoodsStringBuilder = new StringBuilder();

        db.collection("/users/jnPlwp5EJvQJGS604F2bR59N0892/ingredients")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String ingredientName = document.getString("name");
                            userIngredients.add(ingredientName);
                        }

                        db.collection("/foods")
                                .get()
                                .addOnCompleteListener(foodTask -> {
                                    if (foodTask.isSuccessful()) {
                                        for (QueryDocumentSnapshot foodDocument : foodTask.getResult()) {
                                            int matchCount = 0;

                                            List<String> foodIngredients = (List<String>) foodDocument.get("ingredients");
                                            for (String ingredient : foodIngredients) {
                                                if (userIngredients.contains(ingredient)) {
                                                    matchCount++;
                                                }
                                            }

                                            if (matchCount >= 3) {
                                                matchedFoodsStringBuilder.append(foodDocument.getId()).append("\n");
                                            }
                                        }
                                        tvMatchedFoods.setText(matchedFoodsStringBuilder.toString());  // 일치하는 음식 이름 텍스트 뷰에 표시
                                    }
                                });
                    }
                });
    }
}
