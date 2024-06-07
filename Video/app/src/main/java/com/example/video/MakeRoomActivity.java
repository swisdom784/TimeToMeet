package com.example.video;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Locale;
import java.util.Map;

public class MakeRoomActivity extends AppCompatActivity implements DatePickerListener, TimePickerListener {
    room r = new room();
    EditText room, password;

    DatabaseReference mdatabase = FirebaseDatabase.getInstance().getReference();
    Map<String, Integer> time = new HashMap<>();
    Map<String, Integer> days = new HashMap<>();
    List<Integer> roomList = new ArrayList<>();
    private static final String TAG_BOTTOM_SHEET = "bottom_sheet_date_picker";
    Button makebtn, start_date_set, end_date_set, start_time_set, end_time_set;
    TextView start_date, end_date, start_time,end_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_room);
        overridePendingTransition(R.anim.horizontal_enter, R.anim.none);

        setDateNTime(); //날짜, 시간 설정(아래쪽에 있음)

        MAKE_A_ROOM(); //데이터베이스 설정 및 makebtn

        TextView tv_home = findViewById(R.id.back_to_home1);
        tv_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }//onCreate

    public void setDateNTime(){
        start_date = findViewById(R.id.datetext_btn1); //tvs
        end_date = findViewById(R.id.datetext_btn2);
        start_time = findViewById(R.id.timetext_btn1);
        end_time = findViewById(R.id.timetext_btn2);
        start_date_set = findViewById(R.id.start_date_btn); //btns
        end_date_set = findViewById(R.id.end_date_btn);
        start_time_set = findViewById(R.id.start_time_btn);
        end_time_set = findViewById(R.id.end_time_btn);

        start_date_set.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Bundle bundle = new Bundle();
                bundle.putInt("flag",1);
                DatePickerBottomSheetFragment bottomSheetFragment = new DatePickerBottomSheetFragment();
                bottomSheetFragment.setArguments(bundle);
                bottomSheetFragment.setListener(MakeRoomActivity.this);
                bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
            }
        });

        end_date_set.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Bundle bundle = new Bundle();
                bundle.putInt("flag",2);
                DatePickerBottomSheetFragment bottomSheetFragment = new DatePickerBottomSheetFragment();
                bottomSheetFragment.setArguments(bundle);
                bottomSheetFragment.setListener(MakeRoomActivity.this);
                bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
            }
        });

        start_time_set.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Bundle bundle = new Bundle();
                bundle.putInt("flag",1);
                TimePickerBottomSheetFragment bottomSheetFragment = new TimePickerBottomSheetFragment();
                bottomSheetFragment.setArguments(bundle);
                bottomSheetFragment.setListener(MakeRoomActivity.this);
                bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
            }
        });
        end_time_set.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Bundle bundle = new Bundle();
                bundle.putInt("flag",2);
                TimePickerBottomSheetFragment bottomSheetFragment = new TimePickerBottomSheetFragment();
                bottomSheetFragment.setArguments(bundle);
                bottomSheetFragment.setListener(MakeRoomActivity.this);
                bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
            }
        });
    }

    @Override
    public void onDateSelected(int year,int month,int day,int flag){
        String selectedDate = String.format(Locale.getDefault(), "%d년 %d월 %d일", year, month + 1, day);
        if(flag == 1){//start
            start_date.setText(selectedDate);
            days.put("startyear", year);
            days.put("startmonth", month);
            days.put("startday", day);
        }
        else if(flag == 2){//end
            end_date.setText(selectedDate);
            days.put("endyear", year);
            days.put("endmonth", month);
            days.put("endday", day);
        }
    }
    public void onTimeSelected(int hour, int min,int flag){
        String selectedTime = String.format(Locale.getDefault(), "%d시", hour);
        if(flag == 1){//start
            start_time.setText(selectedTime);
            time.put("starthour", hour);
            time.put("startmin", min);
        }
        else if(flag == 2){//end
            end_time.setText(selectedTime);
            time.put("endhour", hour);
            time.put("endmin", min);
        }

    }

    public boolean is_timePicked() {
        if (days.get("startyear") == null || days.get("startmonth") == null || days.get("startday") == null)
            return false;
        if (days.get("endyear") == null || days.get("endmonth") == null || days.get("endday") == null)
            return false;
        if (time.get("starthour") == null || time.get("startmin") == null) return false;
        if (time.get("endhour") == null || time.get("endmin") == null) return false;

        if (time.get("starthour") <= time.get("endhour")
                && days.get("startday") <= days.get("endday")
                && days.get("startmonth") <= days.get("endmonth")
                && days.get("startyear") <= days.get("endyear")){
            if(days.get("endday")-days.get("startday") >= 21) return false;
            return true;
        }
        return false;
    }

    public void MAKE_A_ROOM(){
        room = findViewById(R.id.room);
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
            public void onCancelled(@NonNull DatabaseError error) {}
        });
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("UserAccount").child(id);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserAccount u = snapshot.getValue(UserAccount.class);
                if (u.getRoomList() != null)
                    roomList = u.getRoomList();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
        makebtn = findViewById(R.id.makebtn);
        makebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!is_timePicked()) {
                    if(days.get("endday")-days.get("startday") >= 21){
                        Toast.makeText(MakeRoomActivity.this, "모임 날짜를 20일 안으로 선택하세요", Toast.LENGTH_SHORT).show();
                    }
                    Toast.makeText(MakeRoomActivity.this, "시간 입력 오류\n시간이 제대로 입력되지 않았습니다", Toast.LENGTH_SHORT).show();
                    return;
                }
                List<Integer> weight = new ArrayList<>();
                List<Integer> sum = new ArrayList<>();
                for (int i = 0; i < (days.get("endday") - days.get("startday") + 1) * (time.get("endhour") - time.get("starthour") + 1); i++) {
                    weight.add(0);
                    sum.add(0);
                }
                roomList.add(room_num[0] + 1);
                Map<String, Object> s = new HashMap<>();
                s.put(username, weight);

                r.setname(room.getText().toString());
                r.setPassword(HashKeyGenerator.generateRandomHashKey());
                r.setOwner(username);
                r.setGuest(s);
                r.setDay(days);
                r.setTime(time);
                r.setSum(sum);
                mdatabase.setValue(room_num[0] + 1);
                mdatabase = FirebaseDatabase.getInstance().getReference("room");
                mdatabase.child(String.valueOf(room_num[0] + 1)).setValue(r);
                mdatabase = FirebaseDatabase.getInstance().getReference("UserAccount").child(id).child("roomList");
                mdatabase.setValue(roomList);
                finish();
            }
        });
    }

}