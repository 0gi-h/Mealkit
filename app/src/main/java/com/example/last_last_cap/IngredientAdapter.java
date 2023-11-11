package com.example.last_last_cap;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class IngredientAdapter extends ArrayAdapter<String> {
    private Context context;
    private List<String> ingredients;
    private SparseBooleanArray checkedItems = new SparseBooleanArray();

    public boolean isChecked(int position) {
        return checkedItems.get(position, false);
    }

    public void setChecked(int position, boolean isChecked) {
        checkedItems.put(position, isChecked);
    }

    public IngredientAdapter(@NonNull Context context, int resource, @NonNull List<String> objects) {
        super(context, resource, objects);
        this.context = context;
        this.ingredients = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.ingredient_item, parent, false);
        }

        String ingredient = ingredients.get(position);
        TextView textView = convertView.findViewById(R.id.ingredientText);
        CheckBox checkBox = convertView.findViewById(R.id.ingredientCheckBox);

        // 기존의 체크 변경 리스너를 제거합니다.
        checkBox.setOnCheckedChangeListener(null);

        // 체크박스의 체크 상태를 설정합니다.
        checkBox.setChecked(isChecked(position));

        // 새로운 체크 변경 리스너를 설정합니다.
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            setChecked(position, isChecked);
        });

        textView.setText(ingredient);

        return convertView;
    }

}

