package com.example.last_last_cap;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class AllergyDialogFragment extends DialogFragment {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private List<String> selectedAllergies = new ArrayList<>();
    private CheckBox checkBoxPeanuts, checkBoxSoy, checkBoxWheat, checkBoxMilk, checkBoxEggs, checkBoxFish, checkBoxShellfish;
    private OnAllergySelectedListener onAllergySelectedListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.allergy_dialog, container, false);

        checkBoxPeanuts = view.findViewById(R.id.checkbox_peanuts);
        checkBoxSoy = view.findViewById(R.id.checkbox_soy);
        checkBoxWheat = view.findViewById(R.id.checkbox_wheat);
        checkBoxMilk = view.findViewById(R.id.checkbox_milk);
        checkBoxEggs = view.findViewById(R.id.checkbox_eggs);
        checkBoxFish = view.findViewById(R.id.checkbox_fish);
        checkBoxShellfish = view.findViewById(R.id.checkbox_shellfish);

        Button addButton = view.findViewById(R.id.add_allergy_dialog_button2);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addAllergy();

            }
        });

        return view;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if (onAllergySelectedListener != null) {
            onAllergySelectedListener.onAllergySelected(selectedAllergies);
        }
    }

    private void addAllergy() {
        if (checkBoxPeanuts.isChecked()) {
            selectedAllergies.add(checkBoxPeanuts.getText().toString());
        }
        if (checkBoxSoy.isChecked()) {
            selectedAllergies.add(checkBoxSoy.getText().toString());
        }
        if (checkBoxWheat.isChecked()) {
            selectedAllergies.add(checkBoxWheat.getText().toString());
        }
        if (checkBoxMilk.isChecked()) {
            selectedAllergies.add(checkBoxMilk.getText().toString());
        }
        if (checkBoxEggs.isChecked()) {
            selectedAllergies.add(checkBoxEggs.getText().toString());
        }
        if (checkBoxFish.isChecked()) {
            selectedAllergies.add(checkBoxFish.getText().toString());
        }
        if (checkBoxShellfish.isChecked()) {
            selectedAllergies.add(checkBoxShellfish.getText().toString());
        }
        dismiss();
    }

    public void setOnAllergySelectedListener(OnAllergySelectedListener listener) {
        this.onAllergySelectedListener = listener;
    }

    public interface OnAllergySelectedListener {
        void onAllergySelected(List<String> selectedAllergies);
    }
}
