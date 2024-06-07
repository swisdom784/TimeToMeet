package com.example.video;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class DatePickerBottomSheetFragment extends BottomSheetDialogFragment {
    int flag = 1;
    private DatePickerListener listener;

    public void setListener(DatePickerListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_date_picker_bottom_sheet, container, false);
        Bundle bundle = getArguments();
        assert bundle != null;
        flag = bundle.getInt("flag");
        // 날짜 선택 후 확인 버튼
        Button confirmButton = view.findViewById(R.id.btn_confirm);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePicker datePicker = view.findViewById(R.id.date_picker);
                int year = datePicker.getYear();
                int month = datePicker.getMonth();
                int day = datePicker.getDayOfMonth();

                if (listener != null) {
                    listener.onDateSelected(year, month, day,flag);
                }
                dismiss();
            }
        });

        return view;
    }

}