package com.example.last_last_cap;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Collections;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JoinActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore firestore; // Firestore 인스턴스 추가
    private Button joinButton;
    private EditText emailEditText;
    private EditText pwEditText;
    private EditText rePwEditText;

    private String email;
    private String password;
    private String repassword;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.join_activity);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance(); // Firestore 인스턴스 초기화

        joinButton = (Button)findViewById(R.id.join_confirm_button);
        emailEditText = (EditText)findViewById(R.id.join_email_edittext);
        pwEditText = (EditText)findViewById(R.id.join_password_edittext);
        rePwEditText = (EditText)findViewById(R.id.join_repassword_edittext);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = emailEditText.getText().toString()+"@1.com";
                password = pwEditText.getText().toString();
                repassword = rePwEditText.getText().toString();

                if(isEmail(email)){
                    if(password.length() >= 6) {
                        if(password.equals(repassword)){
                            auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        // 회원가입 성공 후 Firestore에 사용자 컬렉션 생성
                                        createCollectionForUser(task.getResult().getUser().getUid());

                                        Toast.makeText(view.getContext(), "회원가입에 성공하였습니다.", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent();
                                        setResult(RESULT_OK, intent);
                                        finish();
                                    }else{
                                        Toast.makeText(view.getContext(), "이미 존재하는 아이디입니다.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }else{
                            Toast.makeText(view.getContext(), "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                            pwEditText.setText("");
                            rePwEditText.setText("");
                        }
                    } else {
                        Toast.makeText(view.getContext(), "비밀번호는 6자리 이상이어야 합니다.", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(view.getContext(), "이메일 주소가 맞지않는 형식입니다.", Toast.LENGTH_SHORT).show();
                    emailEditText.setText("");
                }
            }
        });
    }

    public boolean isEmail(String email){
        boolean returnValue = false;
        String regex = "^[_a-zA-Z0-9-\\.]+@[\\.a-zA-Z0-9-]+\\.[a-zA-Z]+$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(email);
        if(m.matches()){
            returnValue = true;
        }
        return returnValue;
    }

    private void createCollectionForUser(String userId) {
        String username = email.replace("@1.com", "");

        // 'users' 컬렉션 내에 사용자 UID를 문서 ID로 사용하고, 그 안에 'username' 필드에 사용자 아이디를 저장
        firestore.collection("users").document(userId).set(Collections.singletonMap("username", username));
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
}
