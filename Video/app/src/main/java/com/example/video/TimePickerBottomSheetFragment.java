package com.example.video;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;


public class TimePickerBottomSheetFragment extends BottomSheetDialogFragment {
    int flag = 1;
    private TimePickerListener listener;
    public void setListener(TimePickerListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_time_picker_bottom_sheet, container, false);
        Bundle bundle = getArguments();
        assert bundle != null;
        flag = bundle.getInt("flag");
        // 날짜 선택 후 확인 버튼
        Button confirmButton = view.findViewById(R.id.btn_confirm_time);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePicker timePicker = view.findViewById(R.id.time_picker);
                int hour = timePicker.getHour();
                int min = timePicker.getMinute();
                if (listener != null) {
                    listener.onTimeSelected(hour,min,flag);
                }
                dismiss();
            }
        });

        return view;
    }

}