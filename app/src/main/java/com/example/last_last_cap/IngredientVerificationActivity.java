package com.example.last_last_cap;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class IngredientVerificationActivity extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference usersCollection = db.collection("users");
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();
    String userUID  = user.getUid();//uid
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ingredient_verification);

        db = FirebaseFirestore.getInstance();
        Button back, delete;

        back = findViewById(R.id.go_back_button);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Intent intent = getIntent();
        String ingredientName = intent.getStringExtra("ingredientName");
        String expirationDate = intent.getStringExtra("expirationDate");

        // 재료 이름을 텍스트뷰에 설정
        TextView ingredientNameTextView = findViewById(R.id.ingredient_name);
        ingredientNameTextView.setText(ingredientName);

        // 유통기한을 텍스트뷰에 설정
        TextView expirationDateTextView = findViewById(R.id.ingredient_expiration_date);
        expirationDateTextView.setText(expirationDate+"까지");
    }

}

