package com.example.video;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoomGuestActivity extends AppCompatActivity {
    int startDay = 4;
    int endDay = 20;
    int startTime = 7;
    int endTime = 22;
    room room =  new room();
    String user;
    Button okbtn,sumbtn;
    TextView guestName;

    Map<String,Object> map = new HashMap<>();
    List<Integer> sum = new ArrayList<>();
    List<Long> weight = new ArrayList<>();
    final int[] b = new int[10];
    ValueEventListener valueEventListener;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.none, R.anim.horizontal_exit);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_guest);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        Intent i = getIntent();
        String username = i.getStringExtra("username");
        user = username;
        int room_num = i.getIntExtra("room_num",0);
        final int[] meet = new int[10];
        guestName = findViewById(R.id.name);
        guestName.setText(user);
        databaseReference.child("room").child(String.valueOf(room_num)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                room r = snapshot.getValue(room.class);
                if(!r.getGuest().containsKey(username)){
                    List<Long> sum = new ArrayList<Long>();
                    for(int i=0;i<(r.getDay().get("endday")-r.getDay().get("startday")+1)*(r.getTime().get("endhour")-r.getTime().get("starthour")+1);i++)
                        sum.add(0L);
                    Map<String,Object> map = r.getGuest();
                    map.put(username,sum);
                    r.setGuest(map);
                }
                room = r;
                map = r.getGuest();
                sum = r.getSum();
                weight = (List<Long>) map.get(username);
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
                        185,
                        112);

                for(int ii = startTime;ii<=endTime;ii++){
                    TextView tv_time = makeTimeText(ii,btnParams);
                    my_time.addView(tv_time);
                }//for
                for(int j = startDay; j<=endDay; j++){
                    LinearLayout my_time_text = new LinearLayout(getApplicationContext());

                    my_time_text.setLayoutParams(dayparams);
                    my_time_text.setOrientation(LinearLayout.VERTICAL);
                    my_time_text.setTag(j);

                    TextView day_text = new TextView(getApplicationContext());
                    String day = String.valueOf(new StringBuilder().append(j).append("일"));
                    day_text.setText(day);
                    day_text.setTextSize(22);
                    day_text.setGravity(Gravity.CENTER);
                    day_text.setTypeface(ResourcesCompat.getFont(getApplicationContext(),R.font.bagel_fat_one_regular));
                    day_text.setTextColor(getResources().getColor(R.color.colorYlnMnBlue));
                    int padding = 3;
                    int paddingpx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,padding,getResources().getDisplayMetrics());
                    day_text.setPadding(0,0,0,paddingpx);

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

    }//onCreate
    public TextView makeTimeText(int i, LinearLayout.LayoutParams timeParams){
        TextView newTime = new TextView(this);
        newTime.setText(new StringBuilder().append(i).append("시"));
        newTime.setGravity(Gravity.RIGHT);
        newTime.setTextSize(22);
        newTime.setTypeface(ResourcesCompat.getFont(getApplicationContext(),R.font.bagel_fat_one_regular));
        newTime.setTextColor(getResources().getColor(R.color.colorYlnMnBlue));
        int padding = 4;
        int paddingpx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,padding,getResources().getDisplayMetrics());
        newTime.setPadding(15,paddingpx,0,paddingpx);
        return newTime;
    }
    int a;
    //    Map<String,Object> map = room.getGuest();
//    List<Integer> sum = room.getSum();
    public TextView makeBtn(int i,LinearLayout.LayoutParams btnParams){
        //TODO: set a button
        TextView newBtn = new TextView(this);
        newBtn.setLayoutParams(btnParams);
        newBtn.setTag(weight.get(a));
//        if(weight.get(a) == 0)
//            newBtn.setBackgroundColor(Color.WHITE);
//        else
//            newBtn.setBackgroundColor(Color.BLUE);
        // 테두리 모양 생성
        GradientDrawable border = new GradientDrawable();
        if(weight.get(a) == 0)
            border.setColor(getResources().getColor(R.color.white_90));
        else
            border.setColor(getResources().getColor(R.color.colorPowderBlue));
//        border.setColor(Color.BLUE); // 배경색 투명
        border.setStroke(2, getResources().getColor(R.color.white_30));// 테두리 두께와 색상
        border.setCornerRadius(0); // 모서리 곡률

        // 테두리를 Button에 설정
        newBtn.setBackground(border);
        newBtn.setId(a++);
//        newBtn.setText(String.valueOf(a));
        //newBtn.setBackgroundResource(R.drawable.textview_margin);
        newBtn.setGravity(Gravity.CENTER);
        return newBtn;
    }
}