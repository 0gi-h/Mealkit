package com.example.last_last_cap;

import static android.app.Activity.RESULT_OK;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.firestore.FirebaseFirestore;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class SettingFragment extends Fragment {

    private FirebaseFirestore db;
    private Button logout;
    private TextView encodeTextView;
    private TextView decodeTextView;
    private String encodeStr="";
    private String decodeStr="";
    private Button copyButton;
    private Button editButton;
    private EditText encodeEdit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.setting_activity, container, false);

        db = FirebaseFirestore.getInstance();

        logout = rootView.findViewById(R.id.logout_button);
        encodeTextView = rootView.findViewById(R.id.encode_text);
        copyButton = rootView.findViewById(R.id.copy_button);
        editButton = rootView.findViewById(R.id.setting_edit_button);

        String user = SaveSharedPreferences.getUserName(requireContext());

        encodeTextView.setText(SaveSharedPreferences.getCryptoUserName(requireContext()));

//        Toolbar mToolbar = rootView.findViewById(R.id.toolbar3);
//        setSupportActionBar(mToolbar);

        copyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("label", SaveSharedPreferences.getCryptoUserName(requireContext()));
                clipboard.setPrimaryClip(clip);
                Toast.makeText(view.getContext(), "클립보드에 복사되었습니다.", Toast.LENGTH_SHORT).show();
            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(requireContext(), PopupActivity.class);
                intent.putExtra("data", SaveSharedPreferences.getCryptoUserName(requireContext()));
                startActivityForResult(intent, 1);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SaveSharedPreferences.clearUserName(requireContext());
                Toast.makeText(view.getContext(), "로그아웃 완료", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(requireContext(), MainActivity.class);
                startActivity(intent);
                requireActivity().finish();
            }
        });

        return rootView;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{ //toolbar의 back키 눌렀을 때 동작
                requireActivity().finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.w("SettingActivity", "넘어왔어요~");
        //Log.w("SettingActivity","resultCode="+resultCode+" requestC")
/*
        Toast.makeText(this, "넘어왔어요.?!?!?", Toast.LENGTH_SHORT).show();
        encodeTextView.setText("넘어왔어요~");


        String result = data.getStringExtra("changeText");
        if (result != null) {
            encodeTextView.setText(result);
        }*/

        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                String result = data.getStringExtra("changeText");
                Log.w("SettingFragment","Conditions All Ok");

                if (result != null) {

                    String decode = null;

                    try {
                        decode = AES256Crypto.AES_Decode(result);
                    } catch (InvalidAlgorithmParameterException | NoSuchPaddingException |
                             UnsupportedEncodingException | IllegalBlockSizeException |
                             BadPaddingException | NoSuchAlgorithmException | InvalidKeyException e) {
                        e.printStackTrace();
                    }

                    if (decode == null){
                        Toast.makeText(getContext(), "유효하지 않은 냉장고 고유키입니다.", Toast.LENGTH_SHORT).show();
                        return;
                    }else {
                        try {
                            if (db.collection("items").document(decode.trim()) == null) {
                                Toast.makeText(getContext(), "유효하지 않은 냉장고 고유키입니다.", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        } catch (NullPointerException e) {
                            Toast.makeText(getContext(), "유효하지 않은 냉장고 고유키입니다.", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                            return;
                        } catch (IllegalArgumentException e){
                            Toast.makeText(getContext(), "유효하지 않은 냉장고 고유키입니다.", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                            return;
                        }

                    }
                    SaveSharedPreferences.setCryptoUser(getContext(), result);

                    Log.w("SettingActivity","result is not null "+result);

                    try {
                        SaveSharedPreferences.setKeyForDB(getContext(), SaveSharedPreferences.getCryptoUserName(getContext()));
                    } catch (InvalidAlgorithmParameterException | NoSuchPaddingException | UnsupportedEncodingException | IllegalBlockSizeException | BadPaddingException | NoSuchAlgorithmException | InvalidKeyException e) {
                        Toast.makeText(getContext(), "냉장고 고유키가 변경에 실패했습니다.", Toast.LENGTH_SHORT).show();
                    }
                    encodeTextView.setText(SaveSharedPreferences.getCryptoUserName(getContext()));
                    Toast.makeText(getContext(), "냉장고 고유키가 변경되었습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    Log.w("SettingActivity","result is null");
                }
            }
        }
    }
}