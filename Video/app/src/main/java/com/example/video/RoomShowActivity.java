package com.example.video;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoomShowActivity extends AppCompatActivity {
    //TODO : DATABASE 에서 ST/END time 가져와서 변수에다 대입하기,뒤가 앞보다 작으면 검사하기
    //TODO : 선택한 시간 데이터베이스에 업로드하기
    //TODO : 선택 - 30분 단위로 확장하기
    int startDay = 1;
    int endDay = 2;
    int startTime = 7;
    int endTime = 22;
    room room;
    Map<String,Object> map = new HashMap<>();
    List<Integer> sum = new ArrayList<>();
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    Button select;
    TextView password,roomName;
    int max_val;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_show);
        Intent i = getIntent();
        String username = i.getStringExtra("username");
        int room_num = i.getIntExtra("room_num",0);
        final int[] meet = new int[10];
        databaseReference.child("room").child(String.valueOf(room_num)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                room r = snapshot.getValue(room.class);
                map = r.getGuest();
                sum = r.getSum();
                String ps = r.getPassword();
                password = findViewById(R.id.password);
                password.setText(ps);
                roomName = findViewById(R.id.name);
                roomName.setText(r.getname());
                meet[0] = snapshot.child("day").child("startmonth").getValue(Integer.class);
                meet[1] = snapshot.child("day").child("startday").getValue(Integer.class);
                meet[2] = snapshot.child("day").child("endmonth").getValue(Integer.class);
                meet[3] = snapshot.child("day").child("endday").getValue(Integer.class);
                meet[4] = snapshot.child("time").child("starthour").getValue(Integer.class);
                meet[5] = snapshot.child("time").child("endhour").getValue(Integer.class);
                startDay = meet[1];
                endDay = meet[3];
                startTime = meet[4];
                endTime = meet[5];

                LinearLayout my_time = (LinearLayout)findViewById(R.id.my_time);
                LinearLayout.LayoutParams dayparams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);

                LinearLayout day_list = (LinearLayout)findViewById(R.id.day_list);

                LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
                        200,
                        100);

                for(int ii = startTime;ii<=endTime;ii++){
                    TextView tv_time = makeTimeText(ii,btnParams);
                    my_time.addView(tv_time);
                }//for
                max_val = Collections.max(sum);
                for(int j = startDay; j<=endDay; j++){
                    LinearLayout my_time_text = new LinearLayout(getApplicationContext());

                    my_time_text.setLayoutParams(dayparams);
                    my_time_text.setOrientation(LinearLayout.VERTICAL);
                    my_time_text.setTag(j);

                    TextView day_text = new TextView(getApplicationContext());
                    String day = String.valueOf(new StringBuilder().append(j).append("일"));
                    day_text.setText(day);
                    day_text.setTextSize(26);
                    day_text.setGravity(Gravity.CENTER);

                    my_time_text.addView(day_text);
                    for(int ii = startTime;ii<=endTime;ii++){
                        TextView tv_btn = makeBtn(ii,btnParams);
                        my_time_text.addView(tv_btn); //선택 버튼, 복제
                    }//for
                    day_list.addView(my_time_text);
                }//for
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        select = findViewById(R.id.select);
        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RoomShowActivity.this,RoomActivity.class);
                intent.putExtra("username",username);
                intent.putExtra("room_num",room_num);
                startActivity(intent);
                finish();
            }
        });

    }//onCreate

    public TextView makeTimeText(int i, LinearLayout.LayoutParams timeParams){
        TextView newTime = new TextView(this);
        newTime.setText(new StringBuilder().append(i).append("시"));
        newTime.setGravity(Gravity.RIGHT);
        newTime.setTextSize(26);
        return newTime;
    }


    int a;
    public TextView makeBtn(int i,LinearLayout.LayoutParams btnParams){
        //TODO: set a button
        TextView newBtn = new TextView(this);
        newBtn.setLayoutParams(btnParams);
        Resources res = getResources();
        int s = sum.get(a);
        int resourceId = res.getIdentifier("step"+s, "color", getPackageName());
        int step1Color = ContextCompat.getColor(this, resourceId);
        newBtn.setBackgroundColor(step1Color);
//        else{
//            int btn_val = sum.get(a);
//            //max_val = Collections.max(sum);
//            int btn_color = btn_val * (1/max_val);
//            int red_diff = 0xAD - 0x00;
//            int green_diff = 0xFF - 0x80;
//            int blue_diff = 0x2F-0x00;
//            newBtn.setBackgroundColor(Color.rgb(btn_color * red_diff,btn_color * green_diff,btn_color*blue_diff));
//        }
//        else if(sum.get(a) == 2)
//            newBtn.setBackgroundColor(Color.BLUE);
//        else if(sum.get(a) == 3)
//            newBtn.setBackgroundColor(Color.MAGENTA);
        newBtn.setId(a);
        newBtn.setText(String.valueOf(a++));
        //newBtn.setBackgroundResource(R.drawable.textview_margin);
        newBtn.setGravity(Gravity.CENTER);
        newBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return newBtn;
    }

}