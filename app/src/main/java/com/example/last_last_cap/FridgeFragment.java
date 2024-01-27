package com.example.last_last_cap;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
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

import org.tensorflow.lite.examples.detection.DetectorActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class FridgeFragment extends Fragment {
    private AlertDialog currentDialog;
    private TableLayout buttonContainer;
    String[] fish_allergy = {"대게","꽃게","새우","대하","꼬막","가리비","굴","맛조개","바지락","전복","재첩","키조개","홍합"};
    String[] milk_allergy = {"우유","크림","버터","치즈"};
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference usersCollection = db.collection("users");
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();
    String userUID  = user.getUid();//uid

    private FirebaseAuth.AuthStateListener authStateListener;
    private String currentUserUID;

    Activity activity;
    private List<String> list;
    private List<String> allergy_list;

    @Override public void onAttach(Context context) { super.onAttach(context); if (context instanceof Activity) activity = (Activity) context; }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fridge, container, false);
        buttonContainer = view.findViewById(R.id.buttonContainer);

        FirestoreDataManager.addIngredientsListener(SaveSharedPreferences.getKeyForDB(getContext()), new FirestoreDataManager.OnDataFetchedListener() {
            @Override
            public void onDataFetched(List<IngredientData> data) {

                buttonContainer.removeAllViews();

                // 데이터를 사용하는 로직을 여기에 구현
                // 데이터가 변경될 때마다 이 부분이 호출됩니다.
                // 변경된 데이터를 사용하여 addButtonWithName 또는 UI 업데이트 로직을 호출합니다.
                for (IngredientData ingredientData : data) {
                    addButtonWithName(ingredientData.getid(),ingredientData.getName(),ingredientData.getDate());
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
        Button addbutton_camera = view.findViewById(R.id.addButton_camera);
        addbutton_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), DetectorActivity.class);
                startActivity(intent);
            }
        });

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
        fruitButton.setOnClickListener(v ->showSelectToAddIngredientDialog("과일류"));
        porkButton.setOnClickListener(v ->showSelectToAddIngredientDialog("돼지고기"));
        chickenButton.setOnClickListener(v ->showSelectToAddIngredientDialog("닭고기"));
        beafButton.setOnClickListener(v ->showSelectToAddIngredientDialog("소고기"));
        milkButton.setOnClickListener(v ->showSelectToAddIngredientDialog("유제품"));
        seasoningButton.setOnClickListener(v ->showSelectToAddIngredientDialog("조미료"));
        marinButton.setOnClickListener(v ->showSelectToAddIngredientDialog("수산물"));
        nutButton.setOnClickListener(v ->showSelectToAddIngredientDialog("견과류"));
        eggButton.setOnClickListener(v ->showSelectToAddIngredientDialog("계란"));
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
        Button barcodeButton=dialogView.findViewById(R.id.barcode);


        //사진 기능(추후 업데이트 예정)


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
    private void addButtonWithName(String id, String name, String expirationDate) {
        Button button = new Button(getActivity());
        button.setText(name);
        //버튼에 사진넣기 (열지마세요)
        switch(name){
            case "오징어":   case "문어":case "낙지":
                button.setCompoundDrawablesWithIntrinsicBounds( 0,R.drawable.c, 0, 0);

                break;
            case "된장":
                button.setCompoundDrawablesWithIntrinsicBounds( 0,R.drawable.a, 0, 0);
                break;
            case "고추장":
                button.setCompoundDrawablesWithIntrinsicBounds( 0,R.drawable.b, 0, 0);
                break;
            case "소금":
                button.setCompoundDrawablesWithIntrinsicBounds( 0,R.drawable.d, 0, 0);
                break;
            case "설탕":
                button.setCompoundDrawablesWithIntrinsicBounds(0,  R.drawable.e,0, 0);
                break;
            case "크림": case "버터": case "치즈":
                button.setCompoundDrawablesWithIntrinsicBounds(0,  R.drawable.f,0, 0);
                break;
            case "우유":
                button.setCompoundDrawablesWithIntrinsicBounds(0,  R.drawable.g,0, 0);
                break;
            case "꼬막": case "가리비":case "굴":case "맛조개":case "바지락":case "전복":case "재첩":case "키조개":case "홍합":
                button.setCompoundDrawablesWithIntrinsicBounds(0,  R.drawable.h,0, 0);
                break;
            case "새우": case "대하":
                button.setCompoundDrawablesWithIntrinsicBounds(0,  R.drawable.i,0, 0);
                break;
            case "꽃게": case "대게":
                button.setCompoundDrawablesWithIntrinsicBounds(0,  R.drawable.j,0, 0);
                break;
            case "가자미": case "갈치":case "고등어":case "광어":case "멸치":case "장어": case "연어":
                button.setCompoundDrawablesWithIntrinsicBounds(0,  R.drawable.k,0, 0);
                break;
            case "수박":
                button.setCompoundDrawablesWithIntrinsicBounds(0,  R.drawable.l,0, 0);
                break;
            case "귤":
                button.setCompoundDrawablesWithIntrinsicBounds(0,  R.drawable.m,0, 0);
                break;
            case "메추리알":
                button.setCompoundDrawablesWithIntrinsicBounds(0,  R.drawable.n,0, 0);
                break;
            case "사과":
                button.setCompoundDrawablesWithIntrinsicBounds(0,  R.drawable.o,0, 0);
                break;
            case "포도":
                button.setCompoundDrawablesWithIntrinsicBounds(0,  R.drawable.p,0, 0);
                break;
            case "배":
                button.setCompoundDrawablesWithIntrinsicBounds(0,  R.drawable.q,0, 0);
                break;
            case "블루베리":
                button.setCompoundDrawablesWithIntrinsicBounds(0,  R.drawable.r,0, 0);
                break;
            case "복숭아":
                button.setCompoundDrawablesWithIntrinsicBounds(0,  R.drawable.s,0, 0);
                break;
            case "매실":
                button.setCompoundDrawablesWithIntrinsicBounds(0,  R.drawable.t,0, 0);
                break;
            case "닭봉":case "닭날개":case "닭다리":case "닭발":case "닭가슴살":
                button.setCompoundDrawablesWithIntrinsicBounds(0,  R.drawable.u,0, 0);
                break;
            case "새우살":case "부채살": case "안심살":case "아롱사태": case"차돌박이": case"소고기":
                button.setCompoundDrawablesWithIntrinsicBounds(0,  R.drawable.v,0, 0);
                break;
            case "항정살":case "가브리살":case "목살":case "등심":case "뒷다리살": case"삼겹살": case"돼지고기":
                button.setCompoundDrawablesWithIntrinsicBounds(0,  R.drawable.w,0, 0);
                break;
            case "계란":
                button.setCompoundDrawablesWithIntrinsicBounds(0,  R.drawable.x,0, 0);
                break;
            case "파프리카":
                button.setCompoundDrawablesWithIntrinsicBounds(0,  R.drawable.y,0, 0);
                break;
            case "대파":
                button.setCompoundDrawablesWithIntrinsicBounds(0,  R.drawable.z,0, 0);
                break;
            case "청경채":
                button.setCompoundDrawablesWithIntrinsicBounds(0,  R.drawable.aa,0, 0);
                break;
            case "오이":
                button.setCompoundDrawablesWithIntrinsicBounds(0,  R.drawable.ab,0, 0);
                break;
            case "양파":
                button.setCompoundDrawablesWithIntrinsicBounds(0,  R.drawable.ac,0, 0);
                break;
            case "시금치":
                button.setCompoundDrawablesWithIntrinsicBounds(0,  R.drawable.ad,0, 0);
                break;
            case "숙주나물":
                button.setCompoundDrawablesWithIntrinsicBounds(0,  R.drawable.ae,0, 0);
                break;
            case "생강":
                button.setCompoundDrawablesWithIntrinsicBounds(0,  R.drawable.af,0, 0);
                break;
            case "상추":
                button.setCompoundDrawablesWithIntrinsicBounds(0,  R.drawable.ag,0, 0);
                break;
            case "버섯":
                button.setCompoundDrawablesWithIntrinsicBounds(0,  R.drawable.ah,0, 0);
                break;
            case "양배추":
                button.setCompoundDrawablesWithIntrinsicBounds(0,  R.drawable.ai,0, 0);
                break;
            case "무":
                button.setCompoundDrawablesWithIntrinsicBounds(0,  R.drawable.aj,0, 0);
                break;
            case "멜론":
                button.setCompoundDrawablesWithIntrinsicBounds(0,  R.drawable.ak,0, 0);
                break;
            case "마늘":
                button.setCompoundDrawablesWithIntrinsicBounds(0,  R.drawable.al,0, 0);
                break;
            case "딸기":
                button.setCompoundDrawablesWithIntrinsicBounds(0,  R.drawable.am,0, 0);
                break;
            case "당근":
                button.setCompoundDrawablesWithIntrinsicBounds(0,  R.drawable.an,0, 0);
                break;
            case "고추":
                button.setCompoundDrawablesWithIntrinsicBounds(0,  R.drawable.ao,0, 0);
                break;
            case "고사리":
                button.setCompoundDrawablesWithIntrinsicBounds(0,  R.drawable.ap,0, 0);
                break;
            case "고구마":
                button.setCompoundDrawablesWithIntrinsicBounds(0,  R.drawable.aq,0, 0);
                break;
            case "감자":
                button.setCompoundDrawablesWithIntrinsicBounds(0,  R.drawable.ar,0, 0);
                break;
            case "아몬드":        case "은행":        case "헤이즐넛":        case "땅콩":        case "호두":
                button.setCompoundDrawablesWithIntrinsicBounds(0,  R.drawable.as,0, 0);
                break;
            case "깻잎":
                button.setCompoundDrawablesWithIntrinsicBounds(0,  R.drawable.at,0, 0);
                break;
            case "바나나":
                button.setCompoundDrawablesWithIntrinsicBounds(0,  R.drawable.au,0, 0);
                break;
            case "배추":
                button.setCompoundDrawablesWithIntrinsicBounds(0,  R.drawable.av,0, 0);
                break;
            case "브로콜리":
                button.setCompoundDrawablesWithIntrinsicBounds(0,  R.drawable.aw,0, 0);
                break;
            case "베이컨":
                button.setCompoundDrawablesWithIntrinsicBounds(0,  R.drawable.ax,0, 0);
                break;
            case "율무":
                button.setCompoundDrawablesWithIntrinsicBounds(0,  R.drawable.ay,0, 0);
                break;
            case "대두":
                button.setCompoundDrawablesWithIntrinsicBounds(0,  R.drawable.az,0, 0);
                break;
            case "호밀":
                button.setCompoundDrawablesWithIntrinsicBounds(0,  R.drawable.ba,0, 0);
                break;
            case "호박":
                button.setCompoundDrawablesWithIntrinsicBounds(0,  R.drawable.bb,0, 0);
                break;
            case "보리":
                button.setCompoundDrawablesWithIntrinsicBounds(0,  R.drawable.bc,0, 0);
                break;
            case "옥수수":
                button.setCompoundDrawablesWithIntrinsicBounds(0,  R.drawable.bd,0, 0);
                break;
            case "밀":
                button.setCompoundDrawablesWithIntrinsicBounds(0,  R.drawable.be,0, 0);
                break;
            case "밀가루":
                button.setCompoundDrawablesWithIntrinsicBounds(0,  R.drawable.bf,0, 0);
                break;
            case "김":
                button.setCompoundDrawablesWithIntrinsicBounds(0,  R.drawable.bg,0, 0);
                break;
            case "팽이버섯":
                button.setCompoundDrawablesWithIntrinsicBounds(0,  R.drawable.spin,0, 0);
                break;
            case "키위":
                button.setCompoundDrawablesWithIntrinsicBounds(0,  R.drawable.kiwi,0, 0);
                break;
            case "소세지":
                button.setCompoundDrawablesWithIntrinsicBounds(0,  R.drawable.sausage,0, 0);
                break;
            case "토마토":
                button.setCompoundDrawablesWithIntrinsicBounds(0,  R.drawable.tomato,0, 0);
                break;
        }
        button.setBackgroundResource(R.drawable.round_button_background);
        CollectionReference allergyCollection = usersCollection.document(userUID).collection("ALLERGY");
// 알러지 정보 가져오기
        allergyCollection.document("어패류").get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            // "어패류" 알러지가 있는 경우
                            List<String> fishAllergies = Arrays.asList(fish_allergy);

                            // 현재 알러지가 fish_allergy 리스트에 포함되어 있다면 배경색을 붉은색으로 설정
                            if (fishAllergies.contains(name)) {
                                button.setTextColor(Color.RED);

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
        allergyCollection.document("유제품").get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            // "유제품" 알러지가 있는 경우
                            List<String> milkallegries = Arrays.asList(milk_allergy);

                            // 현재 알러지가 fish_allergy 리스트에 포함되어 있다면 배경색을 붉은색으로 설정
                            if (milkallegries.contains(name)) {
                                button.setTextColor(Color.RED);

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

        allergyCollection.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot document : queryDocumentSnapshots) {
                            String allergy = document.getId();

                            // "땅콩"이 알러지에 포함되어 있다면 해당 버튼의 배경색을 붉은색으로 설정
                            if ("땅콩".equals(allergy) && "땅콩".equals(name)) {
                                button.setTextColor(Color.RED);

                                break;
                            }
                            if ("복숭아".equals(allergy) && "복숭아".equals(name)) {
                                button.setTextColor(Color.RED);

                                break;

                            }
                            if ("대두".equals(allergy) && "대두".equals(name)) {
                                button.setTextColor(Color.RED);

                                break;

                            }
                            if ("밀".equals(allergy) && "밀".equals(name)) {
                                button.setTextColor(Color.RED);

                                break;

                            }
                            if ("계란".equals(allergy) && "계란".equals(name)) {
                                button.setTextColor(Color.RED);

                                break;

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

        button.setCompoundDrawablePadding(10);

        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(24, 0, 24, 0); // left, top, right, bottom margins


        button.setLayoutParams(layoutParams);
        button.setTypeface(null, Typeface.BOLD);

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
                showIngredientVerificationDialog(id,name, expirationDate);
            }
        });
    }


   private void showIngredientVerificationDialog(String id, String ingredientName, String expirationDate) {
       if (currentDialog != null && currentDialog.isShowing()) {
           currentDialog.dismiss();
       }

       // IngredientVerificationActivity를 다이얼로그로 표시
       IngredientVerificationActivity dialog = new IngredientVerificationActivity(getActivity(), id, ingredientName, expirationDate);
       dialog.show();
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