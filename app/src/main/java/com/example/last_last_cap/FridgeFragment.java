package com.example.last_last_cap;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class FridgeFragment extends Fragment {
    private AlertDialog currentDialog;
    private TableLayout buttonContainer;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference usersCollection = db.collection("users");
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();
    String userUID  = user.getUid();//uid


    Activity activity;
    private List<String> list;

    @Override public void onAttach(Context context) { super.onAttach(context); if (context instanceof Activity) activity = (Activity) context; }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fridge, container, false);
        buttonContainer = view.findViewById(R.id.buttonContainer);

        FirestoreDataManager.addIngredientsListener(userUID, new FirestoreDataManager.OnDataFetchedListener() {
            @Override
            public void onDataFetched(List<IngredientData> data) {

                buttonContainer.removeAllViews();

                // 데이터를 사용하는 로직을 여기에 구현
                // 데이터가 변경될 때마다 이 부분이 호출됩니다.
                // 변경된 데이터를 사용하여 addButtonWithName 또는 UI 업데이트 로직을 호출합니다.
                for (IngredientData ingredientData : data) {
                    addButtonWithName(ingredientData.getName(),ingredientData.getDate());
                }
            }

            @Override
            public void onError(String errorMessage) {
                // 오류 처리 로직을 여기에 구현
                Log.e(TAG, errorMessage);
            }
        });


        Button addButton2 = view.findViewById(R.id.addButton2);
        //앞으로 addButton2를 이용하여, 재료추가 기능을 만들 예정. 그로인해 showAddDialog()함수대신 showAddDialog2()함수 사용예정

        addButton2.setOnClickListener(v -> showAddDialog2());
        Button calendarButton = view.findViewById(R.id.calendarButton);
        calendarButton.setOnClickListener(v -> showCalendarDialog());

        return view;
    }



    private void showAddDialog2(){
        if (currentDialog != null && currentDialog.isShowing()) {
            currentDialog.dismiss();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.ingredient_classification_dialog, null);
        builder.setView(dialogView);

        //각 재료의 카테고리별 버튼 연결

        Button graiButton = dialogView.findViewById(R.id.grain);
        Button vegButton = dialogView.findViewById(R.id.veg);
        Button fruitButton = dialogView.findViewById(R.id.fruit);
        Button porkButton = dialogView.findViewById(R.id.pork);
        Button chickenButton = dialogView.findViewById(R.id.chicken);
        Button beafButton = dialogView.findViewById(R.id.beaf);
        Button milkButton = dialogView.findViewById(R.id.milk);
        Button seasoningButton = dialogView.findViewById(R.id.seasoning);
        Button marinButton = dialogView.findViewById(R.id.marin);
        Button nutButton = dialogView.findViewById(R.id.nut);
        Button eggButton = dialogView.findViewById(R.id.egg);
        Button etcButton = dialogView.findViewById(R.id.etc);


        //각 버튼에 해당하는 카테고리 추가로 넘어감. 넘어갈때 카테고리 이름 가지고 넘어가도록 설정해두었음.
        graiButton.setOnClickListener(v -> showSelectToAddIngredientDialog("곡류"));
        vegButton.setOnClickListener(v -> showSelectToAddIngredientDialog("채소"));
        fruitButton.setOnClickListener(v ->showSelectToAddIngredientDialog("과일"));
        porkButton.setOnClickListener(v ->showSelectToAddIngredientDialog("돼지고기"));
        chickenButton.setOnClickListener(v ->showSelectToAddIngredientDialog("닭고기"));
        beafButton.setOnClickListener(v ->showSelectToAddIngredientDialog("소고기"));
        milkButton.setOnClickListener(v ->showSelectToAddIngredientDialog("유제품"));
        seasoningButton.setOnClickListener(v ->showSelectToAddIngredientDialog("조미료"));
        marinButton.setOnClickListener(v ->showSelectToAddIngredientDialog("수산물"));
        nutButton.setOnClickListener(v ->showSelectToAddIngredientDialog("견과류"));
        eggButton.setOnClickListener(v ->showSelectToAddIngredientDialog("달걀"));
        etcButton.setOnClickListener(v ->showSelectToAddIngredientDialog("기타"));




        //기본적인 다이얼로그 보여주는 코드
        AlertDialog dialog = builder.create();
        dialog.show();

        currentDialog = dialog;
    }

    //카테고리 선택 후 어떻게 재료를 추가할지 보여주는 다이얼로그의 textview 에 해당 카테고리영역을 출력해주는 함수
    private void showSelectToAddIngredientDialog(String categoryName) {
        list = new ArrayList<String>();
        settingList(categoryName);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if (currentDialog != null && currentDialog.isShowing()) {
            currentDialog.dismiss();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.select_to_add_ingredient, null);
        builder.setView(dialogView);


        //현재 추가하려는 재료의 카테고리가 어느 카테고리인지 출력. 앞선 showAddDialog2함수에서 가져온 카테고리 이름을 출력
        TextView categoryTextView = dialogView.findViewById(R.id.category_name);
        categoryTextView.setText(categoryName+" 카테고리");

        Button input_textButton=dialogView.findViewById(R.id.input_text);
        Button using_cameraButton=dialogView.findViewById(R.id.using_camera);
        Button barcodeButton=dialogView.findViewById(R.id.barcode);


        //사진 기능(추후 업데이트 예정)
        using_cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "아직 기능이 추가되지 않았어요.", Toast.LENGTH_SHORT).show();
            }
        });

        //바코드 기능(추후 업데이트 예정)
        barcodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "아직 기능이 추가되지 않았어요.", Toast.LENGTH_SHORT).show();
            }
        });

        input_textButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentDialog.dismiss();

//                showAddWithTextDialog(categoryName);
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                intent.putExtra("list", (Serializable) list);
                startActivity(intent);

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
        currentDialog = dialog;

    }
    // 기존 버튼들을 삭제하는 함수

    // 재료를 버튼으로 추가하는 함수
    private void addButtonWithName(String name) {
        Button button = new Button(getActivity());
        button.setText(name);

        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT
        );
        layoutParams.weight = 1; // Equally distribute buttons within TableRow

        button.setLayoutParams(layoutParams);

        TableLayout buttonContainer = getView().findViewById(R.id.buttonContainer);
        TableRow lastTableRow = null;

        if (buttonContainer.getChildCount() > 0) {
            lastTableRow = (TableRow) buttonContainer.getChildAt(buttonContainer.getChildCount() - 1);
        }

        if (lastTableRow == null || lastTableRow.getChildCount() >= 3) {
            // Add a new TableRow for a new row
            lastTableRow = new TableRow(getActivity());
            buttonContainer.addView(lastTableRow);
        }

        lastTableRow.addView(button);

        // 버튼 클릭 이벤트 설정
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showIngredientVerificationDialog(name);
            }
        });
    }
    private void addButtonWithName(String name, String expirationDate) {
        Button button = new Button(getActivity());
        button.setText(name);

        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT
        );
        layoutParams.weight = 1; // Equally distribute buttons within TableRow

        button.setLayoutParams(layoutParams);

        TableLayout buttonContainer = getView().findViewById(R.id.buttonContainer);
        TableRow lastTableRow = null;

        if (buttonContainer.getChildCount() > 0) {
            lastTableRow = (TableRow) buttonContainer.getChildAt(buttonContainer.getChildCount() - 1);
        }

        if (lastTableRow == null || lastTableRow.getChildCount() >= 3) {
            // Add a new TableRow for a new row
            lastTableRow = new TableRow(getActivity());
            buttonContainer.addView(lastTableRow);
        }

        lastTableRow.addView(button);

        // 버튼 클릭 이벤트 설정
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showIngredientVerificationDialog(name, expirationDate);
            }
        });
    }

    private void getExpirationDate(String ingredientName) {
        // 파이어베이스에서 해당 재료의 유통기한을 가져옵니다.

        DocumentReference docRef = db.collection("users").document(userUID)
                .collection("ingredients").document(ingredientName);

        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String expirationDate = documentSnapshot.getString("date");
                // name과 expirationDate를 함께 전달합니다.
                addButtonWithName(ingredientName, expirationDate);
            }
        }).addOnFailureListener(e -> {
            // 오류 처리
        });
    }

    private void showIngredientVerificationDialog(String ingredientName, String expirationDate) {
        if (currentDialog != null && currentDialog.isShowing()) {
            currentDialog.dismiss();
        }

        // IngredientVerificationActivity를 시작하고 재료 이름과 유통기한을 전달합니다.
        Intent intent = new Intent(getActivity(), IngredientVerificationActivity.class);
        intent.putExtra("ingredientName", ingredientName);
        intent.putExtra("expirationDate", expirationDate);
        startActivity(intent);
    }

    // showAddDialog2 메서드의 addButtonWithName 호출 부분 수정

    private void showIngredientVerificationDialog(String ingredientName) {
        if (currentDialog != null && currentDialog.isShowing()) {
            currentDialog.dismiss();
        }

        // IngredientVerificationActivity를 시작하고 재료 이름을 전달합니다.
        Intent intent = new Intent(getActivity(), IngredientVerificationActivity.class);
        intent.putExtra("ingredientName", ingredientName);
        startActivity(intent);
    }

    private void showCalendarDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                (view, year1, monthOfYear, dayOfMonth) -> {
                    String selectedDate = year1 + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                    Toast.makeText(getContext(), selectedDate, Toast.LENGTH_SHORT).show();
                }, year, month, day) {
            @Override
            public void onDateChanged(DatePicker view, int year, int month, int dayOfMonth) {
                super.onDateChanged(view, year, month, dayOfMonth);
                // Mark selected date with a red dot
                DatePicker picker = getDatePicker();
                long selectedDateInMillis = getDatePickerDateInMillis(picker);
                picker.setMinDate(selectedDateInMillis);
                picker.setMaxDate(selectedDateInMillis);
            }
        };

        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
        datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());

        datePickerDialog.show();
    }

    private long getDatePickerDateInMillis(DatePicker picker) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, picker.getYear());
        calendar.set(Calendar.MONTH, picker.getMonth());
        calendar.set(Calendar.DAY_OF_MONTH, picker.getDayOfMonth());
        return calendar.getTimeInMillis();
    }

    private void settingList(String categoryName) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collectionRef = db.collection("food");
        System.out.println("시작");
        collectionRef.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        for (DocumentSnapshot document : queryDocumentSnapshots) {
//                            String ingredient = (String) document.get("INGREDIENT");
                            if (document.get("CATEGORY").equals(categoryName)) {
                                list.add((String) document.get("INGREDIENT"));
                                //System.out.println(list);
                            }
                        }
                        System.out.println("끝");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // 오류 처리
                    }
                });
    }
}