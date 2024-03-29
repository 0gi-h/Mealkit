package com.example.last_last_cap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private Button loginButton;
    private Button joinButton;
    private EditText emailEditText;
    private EditText pwEditText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        auth = FirebaseAuth.getInstance();
        Intent intent = new Intent(this,LoadingActivity.class);
        startActivity(intent);

        if(auth!=null){
            //Toast.makeText(this, "auth not null", Toast.LENGTH_SHORT).show();
        }

        loginButton = (Button)findViewById(R.id.login_button);
        joinButton = (Button)findViewById(R.id.join_button);
        emailEditText = (EditText)findViewById(R.id.email);
        pwEditText = (EditText)findViewById(R.id.password);

        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), JoinActivity.class);
                startActivityForResult(intent,2);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailEditText.getText().toString()+"@1.com";
                String pw = pwEditText.getText().toString();

                auth.signInWithEmailAndPassword(email,pw).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser user = auth.getCurrentUser();
                            String userUid = user.getUid();

                            SaveSharedPreferences.setUserName(MainActivity.this, userUid);

                            try {
                                SaveSharedPreferences.makeCryptoUser(MainActivity.this, userUid);
                            } catch (NoSuchPaddingException e) {
                                e.printStackTrace();
                            } catch (InvalidAlgorithmParameterException e) {
                                e.printStackTrace();
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            } catch (IllegalBlockSizeException e) {
                                e.printStackTrace();
                            } catch (BadPaddingException e) {
                                e.printStackTrace();
                            } catch (NoSuchAlgorithmException e) {
                                e.printStackTrace();
                            } catch (InvalidKeyException e) {
                                e.printStackTrace();
                            }

                            String cryptoUid = SaveSharedPreferences.getCryptoUserName(MainActivity.this);

                            try {
                                SaveSharedPreferences.setKeyForDB(MainActivity.this, cryptoUid);
                            } catch (NoSuchPaddingException e) {
                                e.printStackTrace();
                            } catch (InvalidAlgorithmParameterException e) {
                                e.printStackTrace();
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            } catch (IllegalBlockSizeException e) {
                                e.printStackTrace();
                            } catch (BadPaddingException e) {
                                e.printStackTrace();
                            } catch (NoSuchAlgorithmException e) {
                                e.printStackTrace();
                            } catch (InvalidKeyException e) {
                                e.printStackTrace();
                            }

//                            View loginButton = findViewById(R.id.login_activity); // 애니메이션을 적용할 뷰의 ID를 사용
//                            Animation animation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.fridge_open);
//                            loginButton.startAnimation(animation);

                            Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
                            intent.putExtra("name", emailEditText.getText().toString());
                            startActivity(intent);
                            finish();
                        }
                        else{
                            Toast.makeText(view.getContext(), "로그인에 실패했습니다", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}