package com.example.video;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.ui.auth.data.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

public class MakeRoomActivity extends AppCompatActivity {
//TODO : 시간 똑바로 선택해야지만 방 만들 수 있게 하기
    room r = new room();
    EditText room,password;
    Button makebtn;
    DatabaseReference mdatabase = FirebaseDatabase.getInstance().getReference();
    Map<String,Integer> time = new HashMap<>();
    Map<String,Integer> days = new HashMap<>();
    List<Integer> roomList = new ArrayList<>();
    ActivityResultLauncher<Intent> time1ActivityResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == 0) {
                        int hour = result.getData().getIntExtra("hour",0);
                        int min = result.getData().getIntExtra("min",0);
                        TextView returnText = findViewById(R.id.timetext_btn1);
                        returnText.setText(new StringBuilder().append(hour).append(" : ").append(min));
                        time.put("starthour",hour);
                        time.put("startmin",min);
                    }
                }
            });
    ActivityResultLauncher<Intent> time2ActivityResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == 0) {
                        int hour = result.getData().getIntExtra("hour",0);
                        int min = result.getData().getIntExtra("min",0);
                        TextView returnText = findViewById(R.id.timetext_btn2);
                        returnText.setText(new StringBuilder().append(hour).append(" : ").append(min));
                        time.put("endhour",hour);
                        time.put("endmin",min);
                    }
                }
            });
    ActivityResultLauncher<Intent> date1ActivityResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == 1) {
                        int year = result.getData().getIntExtra("year",0);
                        int month = result.getData().getIntExtra("month",0);
                        int day = result.getData().getIntExtra("day",0);
                        TextView returnText = findViewById(R.id.datetext_btn1);
                        returnText.setText(new StringBuilder().append(year).append("년 ").append(month).append("월 ").append(day).append("일"));
                        days.put("startyear",year);
                        days.put("startmonth",month);
                        days.put("startday",day);
                    }
                }
            });
    ActivityResultLauncher<Intent> date2ActivityResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == 1) {
                        int year = result.getData().getIntExtra("year",0);
                        int month = result.getData().getIntExtra("month",0);
                        int day = result.getData().getIntExtra("day",0);
                        TextView returnText = findViewById(R.id.datetext_btn2);
                        returnText.setText(new StringBuilder().append(year).append("년 ").append(month).append("월 ").append(day).append("일"));
                        days.put("endyear",year);
                        days.put("endmonth",month);
                        days.put("endday",day);
                    }
                }
            });
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_room);
        overridePendingTransition(R.anim.horizontal_enter, R.anim.none);

        room = findViewById(R.id.room);
        makebtn = findViewById(R.id.makebtn);
        Intent follow = getIntent();
        String username = follow.getStringExtra("username");
        final int[] room_num = new int[10];
        mdatabase = mdatabase.child("room_num");
        String id = follow.getStringExtra("userid");
        mdatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Double num = snapshot.getValue(Double.class);
                room_num[0] = num.intValue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("UserAccount").child(id);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserAccount u = snapshot.getValue(UserAccount.class);
                if(u.getRoomList() != null)
                    roomList = u.getRoomList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        makebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Integer> weight = new ArrayList<>();
                List<Integer> sum = new ArrayList<>();
                for(int i=0;i<(days.get("endday")-days.get("startday")+1)*(time.get("endhour")-time.get("starthour")+1);i++) {
                    weight.add(0);
                    sum.add(0);
                }
                roomList.add(room_num[0]+1);
                Map<String,Object> s = new HashMap<>();
                s.put(username,weight);

                r.setname(room.getText().toString());
                r.setPassword(HashKeyGenerator.generateRandomHashKey());
                r.setOwner(username);
                r.setGuest(s);
                r.setDay(days);
                r.setTime(time);
                r.setSum(sum);
                mdatabase.setValue(room_num[0]+1);
                mdatabase = FirebaseDatabase.getInstance().getReference("room");
                mdatabase.child(String.valueOf(room_num[0]+1)).setValue(r);
                mdatabase = FirebaseDatabase.getInstance().getReference("UserAccount").child(id).child("roomList");
                mdatabase.setValue(roomList);
                finish();
            }
        });
        TextView tv_home = findViewById(R.id.back_to_home1);
        tv_home.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                finish();
            }
        });
    }//onCreate
    public void clickstartdateBtn(View v){
        Intent datepickIntent = new Intent(getApplicationContext(), date_picker.class);
        date1ActivityResult.launch(datepickIntent);
    }
    public void clickenddateBtn(View v){
        Intent datepickIntent = new Intent(getApplicationContext(), date_picker.class);
        date2ActivityResult.launch(datepickIntent);
    }

    public void clicktimeBtn1(View v){
        Intent timepickIntent = new Intent(getApplicationContext(), time_picker.class);
        time1ActivityResult.launch(timepickIntent);
    }
    public void clicktimeBtn2(View v){
        Intent timepickIntent = new Intent(getApplicationContext(), time_picker.class);
        time2ActivityResult.launch(timepickIntent);
    }

    public boolean is_timePicked(){//TODO : 수정하기
        if(days.get("startyear") == null || days.get("startmonth") == null || days.get("startday") == null) return false;
        if(days.get("endyear") == null || days.get("endmonth") == null || days.get("endday") == null) return false;
        if(days.get("starthour") == null || days.get("startmin")==null) return false;
        if(days.get("endhour") == null || days.get("endmin")==null) return false;

        if(days.get("starthour")<=days.get("endhour")
                && days.get("startday")<=days.get("endday")
                && days.get("startmonth")<=days.get("endmonth")
                && days.get("startyear")<=days.get("endyear")) return true;
        return true;
    }

}