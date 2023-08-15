package com.example.last_last_cap;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class FridgeFragment extends Fragment {

    private LinearLayout buttonContainer;
    private List<Button> buttons;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fridge, container, false);

        buttonContainer = view.findViewById(R.id.buttonContainer);
        buttons = new ArrayList<>();

        Button addButton = view.findViewById(R.id.addButton);
        addButton.setOnClickListener(v -> showAddDialog());

        Button calendarButton = view.findViewById(R.id.calendarButton);
        calendarButton.setOnClickListener(v -> showCalendarDialog());

        return view;
    }

    private void showAddDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add Button");

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

    private void addButtonWithName(String name) {
        Button button = new Button(getActivity());
        button.setText(name);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        button.setLayoutParams(layoutParams);
        buttons.add(button);

        buttonContainer.addView(button);
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