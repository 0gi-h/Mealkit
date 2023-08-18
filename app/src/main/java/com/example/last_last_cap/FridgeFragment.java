package com.example.last_last_cap;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class FridgeFragment extends Fragment {

    private TableLayout buttonContainer;
    private List<Button> buttons;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fridge, container, false);

        buttonContainer = view.findViewById(R.id.buttonContainer);
        buttons = new ArrayList<>();

        Button addButton = view.findViewById(R.id.addButton);

        Button addButton2 = view.findViewById(R.id.addButton2);
        //앞으로 addButton2를 이용하여, 재료추가 기능을 만들 예정. 그로인해 showAddDialog()함수대신 showAddDialog2()함수 사용예정

        addButton.setOnClickListener(v -> showAddDialog());
        addButton2.setOnClickListener(v -> showAddDialog2());
        Button calendarButton = view.findViewById(R.id.calendarButton);
        calendarButton.setOnClickListener(v -> showCalendarDialog());

        return view;
    }

    //showAddDialog에서 db에 입력한 재료를 저장해야 하는 코드 필요
    private void showAddDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("add");

        View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_add_button, null);
        builder.setView(dialogView);

        final EditText nameEditText = dialogView.findViewById(R.id.nameEditText);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String name = nameEditText.getText().toString();
            addButtonWithName(name);
        });

        builder.setNegativeButton("Cancel", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showAddDialog2(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //builder.setTitle("재료 추가하기");
        //제목 추가하면 안이뻐서 주석처리 시켜놓음
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





        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //카테고리 선택 후 어떻게 재료를 추가할지 보여주는 다이얼로그의 textview 에 해당 카테고리영역을 출력해주는 함수
    private void showSelectToAddIngredientDialog(String categoryName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.select_to_add_ingredient, null);
        builder.setView(dialogView);

        TextView categoryTextView = dialogView.findViewById(R.id.category_name);
        categoryTextView.setText(categoryName+" 카테고리");

        Button input_textButton=dialogView.findViewById(R.id.input_text);
        Button using_cameraButton=dialogView.findViewById(R.id.using_camera);
        Button barcodeButton=dialogView.findViewById(R.id.barcode);

        using_cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "아직 기능이 추가되지 않았어요.", Toast.LENGTH_SHORT).show();
            }
        });

        barcodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "아직 기능이 추가되지 않았어요.", Toast.LENGTH_SHORT).show();
            }
        });

        input_textButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddWithTextDialog(categoryName);
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    
    private void  showAddWithTextDialog(String categoryName){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.add_with_text, null);
        builder.setView(dialogView);
    
        TextView checkTextView = dialogView.findViewById(R.id.check);
        checkTextView.setText(categoryName+" 재료명");

        // AutoCompleteTextView에 자동완성 되게 할 list를 추가해두었음. 앞으로 db에서 받아와서 list 생성하는 형태로 이루어져야함
        String[] autoCompleteOptions = new String[] {
                "ab", "abc", "adfasdf","dag","hello","lsdifjsad","sedd" // 원하는 옵션들을 추가하세요
        };

        //AutoCompleteTextView 와 자동완성시킬 list 엮는 코드
        AutoCompleteTextView ingredient_list = dialogView.findViewById(R.id.ingredient_list);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_dropdown_item_1line, autoCompleteOptions);
        ingredient_list.setAdapter(adapter);


        AlertDialog dialog = builder.create();
        dialog.show();
    }
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

        buttons.add(button);
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
}
