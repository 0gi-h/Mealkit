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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class AllergyDialogFragment extends DialogFragment {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private List<String> selectedAllergies = new ArrayList<>();
    private OnAllergySelectedListener onAllergySelectedListener;

    private Button btnPeanuts, btnSoy, btnWheat, btnMilk, btnEggs, btnPeach, btnShellfish;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.allergy_dialog_new, container, false);

        btnPeanuts = view.findViewById(R.id.allergy_p);
        btnSoy = view.findViewById(R.id.allergy_bean);
        btnWheat = view.findViewById(R.id.allergy_meal);
        btnMilk = view.findViewById(R.id.allergy_milk);
        btnEggs = view.findViewById(R.id.allergy_egg);
        btnPeach = view.findViewById(R.id.allergy_peach);
        btnShellfish = view.findViewById(R.id.allergy_sea);
        String userUid = SaveSharedPreferences.getKeyForDB(getActivity());
        CollectionReference allergyCollection = db.collection("users").document(userUid).collection("ALLERGY");


        btnPeanuts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addAllergyDocument(allergyCollection, btnPeanuts.getText().toString());
                dismiss();
            }


        });

        btnSoy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addAllergyDocument(allergyCollection, btnSoy.getText().toString());
                dismiss();
            }
        });
        btnWheat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addAllergyDocument(allergyCollection, btnWheat.getText().toString());
                dismiss();
            }
        });
        btnEggs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addAllergyDocument(allergyCollection, btnEggs.getText().toString());
                dismiss();
            }
        });
        btnPeach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addAllergyDocument(allergyCollection, btnPeach.getText().toString());
                dismiss();
            }
        });
        btnMilk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addAllergyDocument(allergyCollection, btnMilk.getText().toString());
                dismiss();
            }
        });
        btnShellfish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addAllergyDocument(allergyCollection, btnShellfish.getText().toString());
                dismiss();
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

    private void addAllergyDocument(CollectionReference allergyCollection, String allergyName) {
        // 알레르기 문서 추가
        allergyCollection.document(allergyName).set(new AllergyData())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // 성공적으로 추가된 경우
                        selectedAllergies.add(allergyName);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // 추가 실패한 경우
                    }
                });
    }
    public void setOnAllergySelectedListener(OnAllergySelectedListener listener) {
        this.onAllergySelectedListener = listener;
    }

    public interface OnAllergySelectedListener {
        void onAllergySelected(List<String> selectedAllergies);
    }
}
