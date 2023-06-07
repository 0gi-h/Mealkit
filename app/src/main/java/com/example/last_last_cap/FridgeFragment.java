package com.example.last_last_cap;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class FridgeFragment extends Fragment {

    private static final String TAG_CALENDAR_DIALOG = "calendar_dialog";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fridge, container, false);

        Button calendarButton = view.findViewById(R.id.calander_button);
        calendarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCalendarDialog();
            }
        });

        return view;
    }

    private void showCalendarDialog() {
        FragmentManager fragmentManager = getChildFragmentManager();
        CalendarDialogFragment dialogFragment = new CalendarDialogFragment();
        dialogFragment.show(fragmentManager, TAG_CALENDAR_DIALOG);
    }
}
