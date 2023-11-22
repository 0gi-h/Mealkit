package com.example.last_last_cap;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class IngredientVerificationActivity extends Dialog {
    private final Context context;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference usersCollection = db.collection("users");
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();
    String userUID  = user.getUid();//uid
    private List<String> userAllergies = new ArrayList<>();
    private final String id;
    private final String ingredientName;
    private final String expirationDate;


    public IngredientVerificationActivity(@NonNull Context context, String id, String ingredientName, String expirationDate) {
        super(context);
        this.context = context;
        this.id = id;
        this.ingredientName = ingredientName;
        this.expirationDate = expirationDate;
    }

    String[] fish_allergy = {"가자미","갈치","고등어","광어","멸치","장어","연어","오징어","문어","낙지","대게","꽃게","새우","대하","꼬막",
    "가리비","굴","맛조개","바지락","전복","재첩","키조개","홍합"};
    String[] milk_allergy = {"우유","크림","버터","치즈"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ingredient_verification);

        db = FirebaseFirestore.getInstance();
        Button back, delete;
        delete = findViewById(R.id.delete_button);
        back = findViewById(R.id.go_back_button);
        loadUserAllergies();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        // 재료 이름을 텍스트뷰에 설정
        TextView ingredientNameTextView = findViewById(R.id.ingredient_name);
        ingredientNameTextView.setText(ingredientName);

        // 유통기한을 텍스트뷰에 설정
        TextView expirationDateTextView = findViewById(R.id.ingredient_expiration_date);
        expirationDateTextView.setText(expirationDate+"까지");

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // id가 문서 ID인 경우에만 삭제 수행
                if (id != null && !id.isEmpty()) {
                    // users/{userUID}/ingredients/{id} 경로의 문서 참조
                    CollectionReference ingredientsCollection = usersCollection.document(userUID).collection("ingredients");
                    ingredientsCollection.document(id).delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // 삭제 성공
                                    // 필요에 따라 추가적인 처리 수행
                                    dismiss();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // 삭제 실패
                                    // 필요에 따라 추가적인 처리 수행
                                }
                            });
                } else {
                    // id가 없거나 비어있는 경우에는 삭제할 문서가 없으므로 실패로 처리
                    // 필요에 따라 추가적인 처리 수행
                }
            }
        });

    }
    private void loadUserAllergies() {
        // 사용자의 알러지 컬렉션 참조
        CollectionReference allergyCollection = usersCollection.document(userUID).collection("ALLERGY");

        // 알러지 컬렉션의 모든 문서 가져오기
        allergyCollection.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        // 성공적으로 데이터를 가져왔을 때
                        userAllergies.clear();
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            // 각 알러지 데이터를 리스트에 추가
                            userAllergies.add(document.getId());
                        }

                        // 사용자의 알러지 목록을 가져왔으므로 이후의 처리 수행
                        checkAllergies();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // 데이터 가져오기 실패 시 처리
                    }
                });
    }
    private void checkAllergies() {


        // 땅콩, 대두, 밀, 달걀, 복숭아 알러지 체크
        checkCommonAllergies(ingredientName,"복숭아");
        checkCommonAllergies(ingredientName,"대두");
        checkCommonAllergies(ingredientName,"밀");
        checkCommonAllergies(ingredientName,"달걀");
        checkCommonAllergies(ingredientName,"땅콩");


        // 어패류 알러지 체크
        checkFishAllergy(ingredientName);

        // 유제품 알러지 체크
        checkMilkAllergy(ingredientName);
    }

    private void checkCommonAllergies(String ingredientName,String allergy_name) {
        // 사용자의 알러지 컬렉션 참조
        CollectionReference allergyCollection = usersCollection.document(userUID).collection("ALLERGY");

        // 공통 알러지가 있는지 확인
        allergyCollection.document(allergy_name).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            // 사용자가 공통 알러지가 있는 경우
                            if (allergy_name.equals(ingredientName)) {
                                // 공통 알러지인 경우 텍스트뷰에 표시
                                TextView ingredientAllergyTextView = findViewById(R.id.ingredient_alergy);
                                showAllergy(ingredientName + " 알러지");


                                // 글씨체를 굵게 설정

                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // 알러지 데이터 가져오기 실패 시 처리
                    }
                });
    }

    private void checkFishAllergy(String ingredientName) {
        // 사용자의 알러지 컬렉션 참조
        CollectionReference allergyCollection = usersCollection.document(userUID).collection("ALLERGY");

        // "어패류" 알러지가 있는지 확인
        allergyCollection.document("어패류").get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            // "어패류" 알러지가 있는 경우
                            if (Arrays.asList(fish_allergy).contains(ingredientName)) {
                                // 어패류 알러지인 경우 텍스트뷰에 표시
                                showAllergy("어패류 알러지");
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // 알러지 데이터 가져오기 실패 시 처리
                    }
                });
    }

    private void checkMilkAllergy(String ingredientName) {
        // 사용자의 알러지 컬렉션 참조
        CollectionReference allergyCollection = usersCollection.document(userUID).collection("ALLERGY");

        // "유제품" 알러지가 있는지 확인
        allergyCollection.document("유제품").get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            // "유제품" 알러지가 있는 경우
                            if (Arrays.asList(milk_allergy).contains(ingredientName)) {
                                // 유제품 알러지인 경우 텍스트뷰에 표시
                                showAllergy("유제품 알러지");
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // 알러지 데이터 가져오기 실패 시 처리
                    }
                });
    }

    private void showAllergy(String allergyText) {
        TextView ingredientAllergyTextView = findViewById(R.id.ingredient_alergy);
        ingredientAllergyTextView.setText(allergyText);

        // 글씨체를 굵게 설정
        ingredientAllergyTextView.setTypeface(null, Typeface.BOLD);

        ingredientAllergyTextView.setTextColor(context.getResources().getColor(android.R.color.holo_red_light));
    }



}

