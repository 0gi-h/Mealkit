package com.example.last_last_cap;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {


    Button login_btn,create_num_btn;//로그인 버튼, 고유번호 생성 버튼
    EditText insert_num;
    TextView show_num;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(this,LoadingActivity.class);
        startActivity(intent);

        login_btn=findViewById(R.id.login_button);
        create_num_btn = findViewById(R.id.create_number_button);
        insert_num = findViewById(R.id.editText);
        show_num=findViewById(R.id.original_number);

        create_num_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String generatedNumber = generateNumber();

                // 생성된 번호를 show_num 텍스트뷰에 표시
                show_num.setText(generatedNumber);
                show_num.setVisibility(View.VISIBLE);
            }
        });

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // insert_num EditText에서 값을 가져옴
                String userInput = insert_num.getText().toString();

                // 유효성 검사: 값이 비어있지 않은지 확인
                if (!userInput.isEmpty()) {
                    // 여기에서 추가적인 유효성 검사를 수행하고 필요한 동작을 수행합니다.
                    // 예를 들어, 입력된 값과 사전에 정의된 올바른 값과 비교하여 로그인 동작을 처리할 수 있습니다.
                    //일단은 지금은 데이터베이스 연결 안했으니까, 그냥 1111이면 실행
                    // 로그인이 성공했을 경우
                    if (userInput.equals("1111")) {
                        // 로그인 성공 시 할 동작을 수행합니다.
                        // 예를 들어, 다음 화면으로 이동하거나 로그인 상태를 유지하는 등의 동작을 수행할 수 있습니다.
                        // 이동할 다음 화면이 있다면, Intent를 사용하여 해당 화면으로 이동할 수 있습니다.
                        // 예시:
                        Intent intent = new Intent(MainActivity.this, MenuActivity.class);
                        intent.putExtra("original_number", userInput); // 인텐트에 입력값 추가
                        startActivity(intent);
                    } else {
                        // 로그인이 실패했을 경우
                        // 실패 처리에 대한 동작을 수행합니다.
                        // 예를 들어, 올바르지 않은 값이 입력되었음을 사용자에게 알릴 수 있습니다.
                        Toast.makeText(MainActivity.this, "올바르지 않은 값입니다.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // 입력된 값이 비어있을 경우
                    // 입력값이 필수인 경우 사용자에게 알릴 수 있습니다.
                    Toast.makeText(MainActivity.this, "값을 입력하세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
    private String generateNumber() {
        long currentTime = System.currentTimeMillis();
        return String.valueOf(currentTime);
    }
}