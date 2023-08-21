package com.example.last_last_cap;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import static java.lang.Thread.sleep;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private ArrayList<String> list;          // 데이터를 넣은 리스트변수
    private ListView listView;          // 검색을 보여줄 리스트변수
    private EditText editSearch;        // 검색어를 입력할 Input 창
    private ArrayList<String> arraylist;
    private SearchAdapter adapter;
    private ArrayList<String> food;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);

        Intent intent = getIntent();
        list = (ArrayList<String>) intent.getSerializableExtra("list");

//        TextView textView7 = (TextView) findViewById(R.id.textView7);
//        textView7.setText(category+" 검색하기");

        editSearch = (EditText) findViewById(R.id.editSearch);
        listView = (ListView) findViewById(R.id.listView);

        // 리스트를 생성한다.
        //list = new ArrayList<String>();
        food = new ArrayList<String>();

        //ck= new int[27];

        // 검색에 사용할 데이터을 미리 저장한다.
//        settingList();

        System.out.println(list);
        System.out.println("확인");
        // 리스트의 모든 데이터를 arraylist에 복사한다.// list 복사본을 만든다.
        arraylist = new ArrayList<String>();
        arraylist.addAll(list);

        // 리스트에 연동될 아답터를 생성한다.
        adapter = new SearchAdapter(list, this);

        // 리스트뷰에 아답터를 연결한다.
        listView.setAdapter(adapter);

        // input창에 검색어를 입력시 "addTextChangedListener" 이벤트 리스너를 정의한다.
        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                // input창에 문자를 입력할때마다 호출된다.
                // search 메소드를 호출한다.
                String text = editSearch.getText().toString();
                search(text);
            }
        });

        Button button4 = (Button) findViewById(R.id.button4);
        button4.setText("추가하기");

        button4.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

//                Intent intent = new Intent(getApplicationContext(), datePickerActivity.class);
//
//
//                intent.putExtra("contact_phone", food) ;
//                intent.putExtra("isDetection",false);
//
//
//                startActivity(intent);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            int[] ck = new int[arraylist.size()];

            int count = 0;
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){

                for(int i=0;i<arraylist.size();i++){
                    ck[i] += 0;


                }
                TextView t = (TextView)view.findViewById(R.id.label);
                String str = t.getText().toString();

                int c = arraylist.indexOf(str);

                if (ck[c] == 0){
                    view.setBackgroundColor(Color.GRAY);

                    ck[c]=1;
                    food.add(arraylist.get(c));
                    count++;


                }
                else if(ck[c]==1){
                    view.setBackgroundColor(Color.WHITE);
                    ck[c]=0;
                    food.remove(arraylist.get(c));
                    count --;

                }

                String stu = "";
                stu += count;

                button4.setText("추가하기("+stu+")"  );

            }

        });
    }


    public void search(String charText) {

        // 문자 입력시마다 리스트를 지우고 새로 뿌려준다.
        list.clear();

        // 문자 입력이 없을때는 모든 데이터를 보여준다.
        if (charText.length() == 0) {
            list.addAll(arraylist);
        }

        // 문자 입력을 할때..
        else
        {
            // 리스트의 모든 데이터를 검색한다.
            for(int i = 0;i < arraylist.size(); i++)
            {
                // arraylist의 모든 데이터에 입력받은 단어(charText)가 포함되어 있으면 true를 반환한다.
                if (arraylist.get(i).toLowerCase().contains(charText))
                {
                    list.add(arraylist.get(i));

                }
                // 검색된 데이터를 리스트에 추가한다.
            }
        }

        // 리스트 데이터가 변경되었으므로 아답터를 갱신하여 검색된 데이터를 화면에 보여준다.
        adapter.notifyDataSetChanged();
    }

//    private void settingList() {
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        CollectionReference collectionRef = db.collection("food");
//        collectionRef.get()
//                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                    @Override
//                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                        System.out.println("확인요망");
//                        for (DocumentSnapshot document : queryDocumentSnapshots) {
//                            String ingredient = (String) document.get("INGREDIENT");
//                            if (ingredient != null) {
//                                list.add(ingredient);
//                                //System.out.println(list);
//                            }
//                        }
//
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        // 오류 처리
//                    }
//                });
//    }
}
