package com.example.last_last_cap;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class AllergyFragment extends Fragment implements AllergyDialogFragment.OnAllergySelectedListener, AllergyAdapter.OnItemClickListener {

    private List<String> allergies = new ArrayList<>();
    private RecyclerView recyclerView;
    private AllergyAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_allergy, container, false);

        Button addButton = view.findViewById(R.id.add_allergy_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAllergyDialog();
            }
        });

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new AllergyAdapter(allergies, this);
        recyclerView.setAdapter(adapter);

        return view;
    }

    private void showAllergyDialog() {
        AllergyDialogFragment dialogFragment = new AllergyDialogFragment();
        dialogFragment.setOnAllergySelectedListener(this);
        dialogFragment.show(getChildFragmentManager(), "AllergyDialogFragment");
    }

    @Override
    public void onAllergySelected(List<String> selectedAllergies) {
        for (String allergy : selectedAllergies) {
            if (!allergies.contains(allergy)) {
                allergies.add(allergy);
            }
        }
        adapter.notifyDataSetChanged();
        Toast.makeText(getContext(), "알러지가 추가되었습니다.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemClick(int position) {
        deleteAllergy(position);
    }

    public void deleteAllergy(int position) {
        allergies.remove(position);
        adapter.notifyItemRemoved(position);
        Toast.makeText(getActivity(), "알러지가 삭제되었습니다.", Toast.LENGTH_SHORT).show();
    }
}
