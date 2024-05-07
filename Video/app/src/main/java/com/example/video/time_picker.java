package com.example.video;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;


public class time_picker extends AppCompatActivity {
    private TimePicker timePicker;
    private TextView time;
    private Button setTime;
    private Button backBtn;
    private int hour2;
    private int min2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_picker);
        initTime();
        setTimePicker();
    }
    @Override
    public void onBackPressed(){
        Intent intent = new Intent(time_picker.this,MainActivity.class);

        intent.putExtra("hour",hour2);
        intent.putExtra("min",min2);
        setResult(0,intent);
        super.onBackPressed();
    }

    private void initTime() {
        timePicker = findViewById(R.id.time_picker);
        time = findViewById(R.id.tv_time);
    }

    private void setTimePicker() {
        setTime = findViewById(R.id.btn_setting);
        setTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int hour = timePicker.getCurrentHour();
                int min = timePicker.getCurrentMinute();
                showTime(hour, min);


            }
        });
    }
    private void showTime(int hour, int min){
        hour2 = hour;
        min2 = min;
        time.setText(new StringBuilder().append(hour).append(" : ").append(min).append(" "));
    }
}