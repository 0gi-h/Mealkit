package com.example.last_last_cap;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CalendarView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class CalendarDialogFragment extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        // 다이얼로그 레이아웃을 inflate
        View dialogView = inflater.inflate(R.layout.dialog_calendar, null);
        builder.setView(dialogView)
                .setTitle("캘린더 선택")
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 캘린더에서 선택된 날짜 처리
                        CalendarView calendarView = dialogView.findViewById(R.id.calendar_view);
                        long selectedDate = calendarView.getDate();
                        // 선택된 날짜에 대한 처리 작업 수행
                        // 예시로 선택된 날짜를 Toast 메시지로 표시해보겠습니다.
                        Toast.makeText(getActivity(), "선택된 날짜: " + formatDate(selectedDate), Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 다이얼로그 취소 버튼 클릭 시 동작
                        dialog.dismiss();
                    }
                });

        return builder.create();
    }

    private String formatDate(long dateInMillis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(dateInMillis);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return year + "년 " + month + "월 " + day + "일";
    }
}
