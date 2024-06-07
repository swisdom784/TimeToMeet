package com.example.video;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RoomChoiceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RoomChoiceFragment extends Fragment {
    private static final String ARG_STRING = "user_name";
    private static final String ARG_INT = "room_num";
    private String user_name;
    private int room_num;
    LinearLayout my_time, day_list;
    Button okbtn;
    DatabaseReference databaseReference;
    final int[] meet = new int[10];
    int startDay = 1, endDay = 4, startTime = 7, endTime = 11;
    Map<String,Object> map = new HashMap<>();
    List<Integer> sum = new ArrayList<>();
    List<Long> weight = new ArrayList<>();

    public RoomChoiceFragment(){

    }
    public static RoomChoiceFragment newInstance(String name, int num){
        RoomChoiceFragment fragment = new RoomChoiceFragment();
        Bundle args = new Bundle();
        args.putString(ARG_STRING, name);
        args.putInt(ARG_INT,num);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user_name = getArguments().getString(ARG_STRING);
            room_num= getArguments().getInt(ARG_INT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_room_choice, container, false);
        my_time = view.findViewById(R.id.my_time);
        day_list = view.findViewById(R.id.day_list);
        okbtn = view.findViewById(R.id.okbtn);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        loadDataFromDatabase();

        return view;
    }
    private void loadDataFromDatabase(){
        databaseReference.child("room").child(String.valueOf(room_num)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                room r = snapshot.getValue(room.class);
                if(r!= null){
                    meet[0] = snapshot.child("day").child("startmonth").getValue(Integer.class);
                    meet[1] = snapshot.child("day").child("startday").getValue(Integer.class);
                    meet[2] = snapshot.child("day").child("endmonth").getValue(Integer.class);
                    meet[3] = snapshot.child("day").child("endday").getValue(Integer.class);
                    meet[4] = snapshot.child("time").child("starthour").getValue(Integer.class);
                    meet[5] = snapshot.child("time").child("endhour").getValue(Integer.class);
                    updateUI(r);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void updateUI(room r){
        sum = r.getSum();
        startDay = meet[1];
        endDay = meet[3];
        startTime = meet[4];
        endTime = meet[5];
        map = r.getGuest();
        weight = (List<Long>) map.get(user_name);

        okbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                r.setSum(sum);
                map.replace(user_name,weight);
                r.setGuest(map);
                databaseReference.child("room").child(String.valueOf(room_num)).setValue(r)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(getContext(),"저장 성공",Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getContext(),"저장 실패",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        LinearLayout.LayoutParams dayparams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
                185,
                106);

        for (int ii = startTime; ii <= endTime; ii++) {
            TextView tv_time = makeTimeText(ii, btnParams);
            my_time.addView(tv_time);
        }
        for (int j = startDay; j <= endDay; j++) {
            LinearLayout my_time_text = new LinearLayout(getContext());

            my_time_text.setLayoutParams(dayparams);
            my_time_text.setOrientation(LinearLayout.VERTICAL);
            my_time_text.setTag(j);

            TextView day_text = new TextView(getContext());
            String day = String.valueOf(new StringBuilder().append(j).append("일"));
            day_text.setText(day);
            day_text.setTextSize(22);
            day_text.setGravity(Gravity.CENTER);
            day_text.setTypeface(ResourcesCompat.getFont(getContext(), R.font.bagel_fat_one_regular));
            day_text.setTextColor(ContextCompat.getColor(getContext(), R.color.colorYlnMnBlue));
            int padding = 3;
            int paddingpx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, padding, getResources().getDisplayMetrics());
            day_text.setPadding(0, 0, 0, paddingpx);

            my_time_text.addView(day_text);
            for (int ii = startTime; ii <= endTime; ii++) {
                TextView tv_btn = makeBtn(ii, btnParams);
                my_time_text.addView(tv_btn); // 선택 버튼, 복제
            }
            day_list.addView(my_time_text);
        }


    }
    public TextView makeTimeText(int i, LinearLayout.LayoutParams timeParams){
        TextView newTime = new TextView(getContext());
        newTime.setText(new StringBuilder().append(i).append("시"));
        newTime.setGravity(Gravity.RIGHT);
        newTime.setTextSize(22);
        newTime.setTypeface(ResourcesCompat.getFont(getContext(),R.font.bagel_fat_one_regular));
        newTime.setTextColor(getResources().getColor(R.color.colorYlnMnBlue));
        int padding = 5;
        int paddingpx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,padding,getResources().getDisplayMetrics());
        newTime.setPadding(15,paddingpx,0,paddingpx);
        return newTime;
    }
    int a;
    public TextView makeBtn(int i,LinearLayout.LayoutParams btnParams){
        TextView newBtn = new TextView(getContext());
        newBtn.setLayoutParams(btnParams);
        if(weight.get(a) == 0)
            newBtn.setBackgroundColor(getResources().getColor(R.color.white_90));
        else
            newBtn.setBackgroundColor(getResources().getColor(R.color.colorPowderBlue));
        newBtn.setId(a);
        newBtn.setText(String.valueOf(a++));
        //newBtn.setBackgroundResource(R.drawable.textview_margin);
        newBtn.setGravity(Gravity.CENTER);
        newBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                long now_tag = (long)newBtn.getTag();
                int id = newBtn.getId();
                if(weight.get(id) == 0){
                    newBtn.setBackgroundColor(getResources().getColor(R.color.colorPowderBlue));
//                    newBtn.setTag(1L);
                    weight.set(id, 1L);
                    sum.set(id,sum.get(id)+1);
                }else if(weight.get(id) == 1){
                    newBtn.setBackgroundColor(getResources().getColor(R.color.white_90));
//                    newBtn.setTag(0L);
                    weight.set(id,0L);
                    sum.set(id,sum.get(id)-1);

                }
            }
        });
        return newBtn;
    }
    private void setOkBtn(){

    }

}