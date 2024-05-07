package com.example.video;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class date_picker extends AppCompatActivity{
    private DatePicker datePicker;
    private TextView date;
    private Button setDate;
    int year;
    int month;
    int day;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_picker);
        initDate();
        setDatePicker();
    }
    @Override
    public void onBackPressed(){
        Intent intent = new Intent(date_picker.this,MainActivity.class);
        intent.putExtra("year",year);
        intent.putExtra("month",month+1);
        intent.putExtra("day",day);
        setResult(1,intent);
        super.onBackPressed();
    }

    private void initDate(){
        datePicker = findViewById(R.id.date_picker);
        date = findViewById(R.id.tv_date);
    }
    private void setDatePicker(){
        setDate = findViewById(R.id.btn_setting);
        setDate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                year = datePicker.getYear();
                month = datePicker.getMonth();
                day = datePicker.getDayOfMonth();

                date.setText(new StringBuilder().append(year).append(".")
                        .append(month+1).append(".").append(day).append(" "));
            }
        });
    }
}
