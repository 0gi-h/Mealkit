package com.example.last_last_cap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

//import org.tensorflow.lite.examples.detection.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

//activity_date_picker.xml과 연관
public class datePickerActivity extends AppCompatActivity{

    private FirebaseFirestore db;
    private AlarmManager alarmManager;
    ArrayList<Food> mFoods = new ArrayList<>();
    private TextView textView_Date;
    private DatePickerDialog.OnDateSetListener callbackMethod;
    private ArrayList<String> search;
    private String mStrDate = " ";

    private Button add_button;
    private String timestamp;
    private ArrayList<String> dates;
    private boolean isDetection ;

    public static Context context;

    @Override
    public void onBackPressed() {
        Intent intent=null;
        if(isDetection) {
            intent = new Intent(getApplicationContext(), MenuActivity.class);
            startActivity(intent);
        }
        else
            super.onBackPressed();
        finish();
    }

    public int res;
    public int id;
    public int j;
    public int ld;

    @Override
    public void onResume() {
        super.onResume();
        db.collection("users").document(SaveSharedPreferences.getKeyForDB(datePickerActivity.this))
                .collection("ingredients").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            id=0;
                            ld=0;
                            res=0;
                            for (DocumentSnapshot document : task.getResult()) {
                                res++;
                                if(ld < Integer.parseInt(document.getId()) ) {
                                    ld = Integer.parseInt(document.getId());
                                }

                            }
                        } else {
                        }
                    }
                });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_picker);

        add_button = (Button)findViewById(R.id.button4);

        db = FirebaseFirestore.getInstance();

        context = this;

        Date date = new Date();

        //this.InitializeView();
        //this.InitializeListener();

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar_datepicker);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Intent intent = getIntent() ;

        if(intent.getBooleanExtra("isDetection",false)==true)
            isDetection = true;
        else isDetection = false;

        //TextView editSearch = (TextView) findViewById(R.id.editSearch) ;
        //String search = intent.getStringExtra("contact_phone") ;
        search = (ArrayList<String>) intent.getSerializableExtra("contact_phone");
        if (search != null) {
            //editSearch.setText(search.get(0));

        }

        // 리사이클러뷰에 LinearLayoutManager 객체 지정.
        RecyclerView recyclerView = findViewById(R.id.subrc) ;
        recyclerView.setLayoutManager(new LinearLayoutManager(this)) ;

        // 리사이클러뷰에 SimpleTextAdapter 객체 지정.
        SubAdapter adapter = new SubAdapter(search) ;
        recyclerView.setAdapter(adapter) ;

//        db.collection("items").document("food")
//                .collection("ingredients").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                if (task.isSuccessful()) {
//                    id=0;
//                    ld=0;
//                    res=0;
//                    for (DocumentSnapshot document : task.getResult()) {
//                        res++;
//                        if(ld < Integer.parseInt(document.getId()) ) {
//                            ld = Integer.parseInt(document.getId());
//                        }
//
//                    }
//                } else {
//                }
//            }
//        });

        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dates = adapter.getmDates();
                id = ld +1;

                for(int i=0;i<dates.size();i++){
                    if(dates.get(i).equals("")){
                        onClickShowAlert(view);
                        return;
                    }
                }

                for(int i=0;i<search.size();i++){
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("name", change(search.get(i)));
                    timestamp = dates.get(i);
                    map.put("date", timestamp);


                    db.collection("users").document(SaveSharedPreferences.getKeyForDB(datePickerActivity.this))
                            .collection("ingredients").document(Integer.toString(id)).set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(view.getContext(), "추가에 성공했습니다", Toast.LENGTH_SHORT).show();
                                    mFoods.clear();
                                    db();
                                    finish();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(view.getContext(), "추가에 실패했습니다", Toast.LENGTH_SHORT).show();
                                }
                            });
                    id++;
                }
//                ((SubActivity)SubActivity.context).onResume();
//                System.out.print("알람1");
//                Intent intent = new Intent(getApplicationContext(), SubActivity.class);
                //Intent intent = new Intent(getApplicationContext(), Notification.class);
//                startActivity(intent);
            }
        });

        //updateResult();

    }

    public void onClickShowAlert(View view) {
        AlertDialog.Builder myAlertBuilder =
                new AlertDialog.Builder(datePickerActivity.this);
        // alert의 title과 Messege 세팅
        myAlertBuilder.setTitle("유통기한 설정");
        myAlertBuilder.setMessage("유통기한을 설정하세요.");
        // 버튼 추가 (Ok 버튼과 Cancle 버튼 )
        myAlertBuilder.setPositiveButton("확인",new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog,int which){
                return;
            }
        });
        myAlertBuilder.show();
    }

    public void mOnClick(View v){
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        new DatePickerDialog(this, mDateSetListener, year, month, day).show();
    }

    private DatePickerDialog.OnDateSetListener mDateSetListener= new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            mStrDate = String.format("%d년 %d월 %d일",year, month+1, dayOfMonth);
            //updateResult();
        }
    };

    public String change(String name){
        String res = name;
        String[] eng = getResources().getStringArray(R.array.eng_name);
        String[] kor = getResources().getStringArray(R.array.kor_name);

        for(int i=0;i<eng.length;i++){
            if(name.equals(eng[i])){
                res = kor[i];
            }
        }
        return res;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{ //toolbar의 back키 눌렀을 때 동작
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void db(){
        db.collection("users").document(SaveSharedPreferences.getKeyForDB(datePickerActivity.this)).collection("ingredients")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>(){
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        int i=0;
                        ArrayList<String> dates = new ArrayList<String>();
                        ArrayList<String> names = new ArrayList<String>();

                        if(task.isSuccessful()){
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                String str = (String) document.get("name");

                                HashMap<String, Object> map = (HashMap<String, Object>) document.getData();

                                String name = (String) map.get("name");
                                String date = (String) map.get("date");


                                String d[] = date.split("-");
                                int yy = Integer.parseInt(d[0]);
                                int mm = Integer.parseInt(d[1]);
                                int dd = Integer.parseInt(d[2]);
                                Calendar day = Calendar.getInstance();
                                day.add(Calendar.DATE, -1);
                                String getTime = new java.text.SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(day.getTime());

                                String current[] = getTime.split("-");
                                int cyy = Integer.parseInt(current[0]);
                                int cmm = Integer.parseInt(current[1]);
                                String cr[] = current[2].split(" ");
                                int cdd = Integer.parseInt(cr[0]);

                                String cur[] = cr[1].split(":");
                                int chh = Integer.parseInt(cur[0]);
                                int cmmm = Integer.parseInt(cur[1]);
                                int css = Integer.parseInt(cur[2]);

                                i++;
                                names.add(name);
                                dates.add(date);

                                Food f = new Food(str, getResources().getIdentifier(str, "drawable", getPackageName()), false, document.getId());
                                f.setTime(date);
                                mFoods.add(f);

                            }
                            alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);


                            Intent receiverIntent = new Intent(datePickerActivity.this, AlarmReceiver.class);
                            receiverIntent.putExtra("notiName", names);
                            receiverIntent.putExtra("notiDate", dates);
                            receiverIntent.putExtra("notiCount", Integer.toString(i));


                            PendingIntent pendingIntent = PendingIntent.getBroadcast(datePickerActivity.this, 0, receiverIntent, PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_IMMUTABLE);

                            if (pendingIntent == null) {
                                // TODO: 이미 설정된 알람이 없는 경우


                            } else {
                                System.out.println("알람2");
                                PendingIntent sender = PendingIntent.getBroadcast(datePickerActivity.this, 0, receiverIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                                alarmManager.cancel(sender);
                                sender.cancel();
                            }

                            Calendar calendar = Calendar.getInstance();
                            PendingIntent penIntent = PendingIntent.getBroadcast(datePickerActivity.this, 0, receiverIntent, PendingIntent.FLAG_IMMUTABLE);

                            calendar.setTimeInMillis(System.currentTimeMillis());
                            calendar.set(Calendar.HOUR_OF_DAY, 10);
                            calendar.set(Calendar.MINUTE,0);
                            calendar.set(Calendar.SECOND, 0);
                            calendar.set(Calendar.MILLISECOND, 00);

                            //테스트용 코드
//                            String from = "2023-10-23 03:28:00"; //임의로 날짜와 시간을 지정
//                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                            Date datetime = null;
//                            try {
//                                datetime = dateFormat.parse(from);
//                            } catch (ParseException e) {
//                                e.printStackTrace();
//                            }
//                            Calendar calendar = Calendar.getInstance();
//                            calendar.setTime(datetime);
//
//                            System.out.println(datetime);
                            //


                            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 24 * 60 * 60 * 1000, penIntent);
                            //adapter.notifyDataSetChanged();

                        }
                        else {

                        }
                    }
                });
    }
}