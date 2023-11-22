package com.example.last_last_cap;
import android.util.Log;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;

public class FirestoreDataManager {
    private static final String TAG = "FirestoreDataManager";

    public interface OnDataFetchedListener {
        void onDataFetched(List<IngredientData> data);
        void onError(String errorMessage);
    }
    //파이어스토어 실시간 확인
    public static void addIngredientsListener(String uid, OnDataFetchedListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference usersCollection = db.collection("users");
        CollectionReference ingredientsCollection = usersCollection.document(uid).collection("ingredients");

        ingredientsCollection.addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (e != null) {
                Log.e(TAG, "Listen failed.", e);
                listener.onError("Failed to listen for data updates.");
                return;
            }

            if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                List<IngredientData> ingredientDataList = new ArrayList<>();

                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    String documentId = document.getId(); // 문서의 ID 가져오기
                    String ingredientName = document.getString("name");
                    String expirationDate = document.getString("date");

                    // ID도 함께 저장
                    IngredientData ingredientData = new IngredientData(documentId, ingredientName, expirationDate);
                    ingredientDataList.add(ingredientData);
                }

                listener.onDataFetched(ingredientDataList);
            } else {
                Log.d(TAG, "No ingredients data found.");
                // 데이터가 없을 때 처리할 내용을 추가할 수 있습니다.
            }
        });
    }

    //파이어스토어에서 데이터 가져오는 함수
    public static void fetchIngredientsData(OnDataFetchedListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference usersCollection = db.collection("users");
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        if (user != null) {
            String userUID = user.getUid();
            CollectionReference ingredientsCollection = usersCollection.document(userUID).collection("ingredients");

            ingredientsCollection.get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                List<IngredientData> ingredientDataList = new ArrayList<>();

                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    String documentId = document.getId(); // 문서의 ID 가져오기
                                    String ingredientName = document.getString("name");
                                    String expirationDate = document.getString("date");
                                    IngredientData ingredientData = new IngredientData(documentId,ingredientName, expirationDate);
                                    ingredientDataList.add(ingredientData);
                                }

                                listener.onDataFetched(ingredientDataList);
                            } else {
                                Log.e(TAG, "Error getting documents: ", task.getException());
                                listener.onError("Failed to fetch data from Firestore.");
                            }
                        }
                    });
        } else {
            listener.onError("User is not authenticated.");
        }
    }

}
