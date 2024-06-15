package com.example.video;

import android.graphics.drawable.GradientDrawable;
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
import android.widget.ImageView;
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
        if(map.get(user_name) != null) {
            weight = (List<Long>)map.get(user_name);
        }
        else{
            for(int i=0;i<(meet[5]-meet[4]+1)*(meet[3]-meet[1]+1);i++) weight.add(Long.valueOf(0));
        }

        okbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                r.setSum(sum);
                if(map.get(user_name) != null)
                    map.replace(user_name,weight);
                else
                    map.put(user_name,weight);
                r.setGuest(map);
                databaseReference.child("room").child(String.valueOf(room_num)).setValue(r)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    showCustomToast("저장에 성공했어요");
                                } else {
                                    showCustomToast("저장에 실패했어요");
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
                117);

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

        GradientDrawable border = new GradientDrawable();
        if(weight.get(a) == 0)
            border.setColor(getResources().getColor(R.color.white_90));
        else
            border.setColor(getResources().getColor(R.color.colorPowderBlue));
//        border.setColor(Color.BLUE); // 배경색 투명
        border.setStroke(2, getResources().getColor(R.color.colorCadetGray));// 테두리 두께와 색상
        border.setCornerRadius(0); // 모서리 곡률
        // 테두리를 Button에 설정
        newBtn.setBackground(border);
//        if(weight.get(a) == 0)
//            newBtn.setBackgroundColor(getResources().getColor(R.color.white_90));
//        else
//            newBtn.setBackgroundColor(getResources().getColor(R.color.colorPowderBlue));
        newBtn.setId(a++);
        //newBtn.setBackgroundResource(R.drawable.textview_margin);
        newBtn.setGravity(Gravity.CENTER);
        newBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                long now_tag = (long)newBtn.getTag();
                int id = newBtn.getId();
                if(weight.get(id) == 0){
                    border.setColor(getResources().getColor(R.color.colorPowderBlue));
//                    newBtn.setTag(1L);
                    weight.set(id, 1L);
                    sum.set(id,sum.get(id)+1);
                }else if(weight.get(id) == 1){
                    border.setColor(getResources().getColor(R.color.white_90));
//                    newBtn.setTag(0L);
                    weight.set(id,0L);
                    sum.set(id,sum.get(id)-1);

                }
            }
        });
        return newBtn;
    }
    private void showCustomToast(String message) {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast, null);

        TextView text = layout.findViewById(R.id.toast_text);
        text.setText(message);

        ImageView image = layout.findViewById(R.id.toast_image);
        image.setImageResource(R.drawable.logo01); // 원하는 아이콘 리소스 설정

        Toast toast = new Toast(getContext());
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }
}