package com.example.last_last_cap;

import android.os.Bundle;
import android.util.Log;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class AllergyFragment extends Fragment implements AllergyDialogFragment.OnAllergySelectedListener, AllergyAdapter.OnItemClickListener , AllergyAdapter.OnItemLongClickListener {

    private List<String> allergies = new ArrayList<>();
    private RecyclerView recyclerView;
    private AllergyAdapter adapter;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String userUid;


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
        adapter = new AllergyAdapter(allergies, this, this);
        recyclerView.setAdapter(adapter);
        userUid = SaveSharedPreferences.getKeyForDB(getActivity());

        // Firestore에서 알러지 데이터 가져오기
        loadAllergiesFromFirestore();
            return view;
        }

        private void showAllergyDialog() {
            AllergyDialogFragment dialogFragment = new AllergyDialogFragment();
            dialogFragment.setOnAllergySelectedListener(this);
            dialogFragment.show(getChildFragmentManager(), "AllergyDialogFragment");
        }
    private void loadAllergiesFromFirestore() {
        // 사용자의 알러지 컬렉션 참조
        CollectionReference allergyCollection = db.collection("users").document(userUid).collection("ALLERGY");

        // SnapshotListener를 통해 실시간 업데이트 처리
        allergyCollection.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    // 에러 처리
                    Log.e("AllergyFragment", "Listen failed.", e);
                    return;
                }

                // 성공적으로 데이터를 가져왔을 때
                allergies.clear();
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    // 각 알러지 데이터를 리스트에 추가
                    allergies.add(document.getId());
                }
                adapter.notifyDataSetChanged();
            }
        });
    }
        @Override
        public void onAllergySelected(List<String> selectedAllergies) {
            for (String allergy : selectedAllergies) {
                if (!allergies.contains(allergy)) {
                    allergies.add(allergy);
                }
            }
            adapter.notifyDataSetChanged();
        }

    @Override
    public void onItemClick(int position) {
        // 클릭 이벤트에서는 아무 동작을 하지 않도록 구현
        // 꾹 누르는 이벤트에서 이미 처리했기 때문에 클릭 이벤트는 무시
    }

    @Override
    public void onItemLongClick(int position) {
        // 꾹 눌렀을 때 동작할 내용을 여기에 추가
        String allergyToRemove = allergies.get(position);
        deleteAllergyFromFirestore(allergyToRemove);
    }

    private void deleteAllergyFromFirestore(String allergyName) {
        // 사용자의 알러지 컬렉션 참조
        CollectionReference allergyCollection = db.collection("users").document(userUid).collection("ALLERGY");

        // 해당 알러지 문서 삭제
        allergyCollection.document(allergyName)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // 성공적으로 삭제된 경우
                        Toast.makeText(getActivity(), "알러지가 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // 삭제 실패한 경우
                        Toast.makeText(getActivity(), "알러지 삭제 실패", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    }