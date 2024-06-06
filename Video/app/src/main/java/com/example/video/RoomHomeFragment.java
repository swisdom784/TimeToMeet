package com.example.video;

import android.content.res.Resources;
import android.graphics.Color;
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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RoomHomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RoomHomeFragment extends Fragment {
    private static final String ARG_STRING = "user_name";
    private static final String ARG_INT = "room_num";
    private String user_name;
    private int room_num;
    LinearLayout my_time, day_list;
    DatabaseReference databaseReference;
    final int[] meet = new int[10];
    int startDay = 1, endDay = 4, startTime = 7, endTime = 11;
    List<Integer> sum = new ArrayList<>();

    public RoomHomeFragment() {
        // Required empty public constructor
    }

    public static RoomHomeFragment newInstance(String name, int num) {
        RoomHomeFragment fragment = new RoomHomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_STRING, name);
        args.putInt(ARG_INT, num);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user_name = getArguments().getString(ARG_STRING);
            room_num = getArguments().getInt(ARG_INT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_room_home, container, false);
        my_time = view.findViewById(R.id.my_time);
        day_list = view.findViewById(R.id.day_list);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        loadDataFromDatabase();

        return view;
    }

    private void loadDataFromDatabase() {
        databaseReference.child("room").child(String.valueOf(room_num)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                room r = snapshot.getValue(room.class);
                if (r != null) {
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
                // 데이터 읽기 실패 처리
            }
        });
    }

    public void updateUI(room r) {
        sum = r.getSum();
        startDay = meet[1];
        endDay = meet[3];
        startTime = meet[4];
        endTime = meet[5];

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

    public TextView makeTimeText(int i, LinearLayout.LayoutParams timeParams) {
        TextView newTime = new TextView(getContext());
        newTime.setText(new StringBuilder().append(i).append("시"));
        newTime.setGravity(Gravity.RIGHT);
        newTime.setTextSize(22);
        newTime.setTypeface(ResourcesCompat.getFont(getContext(), R.font.bagel_fat_one_regular));
        newTime.setTextColor(ContextCompat.getColor(getContext(), R.color.colorYlnMnBlue));
        int padding = 5;
        int paddingpx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, padding, getResources().getDisplayMetrics());
        newTime.setPadding(15, paddingpx, 0, paddingpx);
        return newTime;
    }

    int a;
    public TextView makeBtn(int i, LinearLayout.LayoutParams btnParams) {
        //TODO: set a button
        TextView newBtn = new TextView(getContext());
        newBtn.setLayoutParams(btnParams);
        Resources res = getResources();
        int max_val;
        max_val = Collections.max(sum);
        int btn_val = sum.get(a);
        double btn_color = (double) btn_val * (1 / (double) max_val);
        int red_diff = 0xFF - 0x2E;
        int green_diff = 0xFF - 0x50;
        int blue_diff = 0xFF - 0x90;
        int red = (int) (red_diff * btn_color), green = (int) (green_diff * btn_color), blue = (int) (blue_diff * btn_color);
        newBtn.setBackgroundColor(Color.rgb(0xFF - red, 0xFF - green, 0xFF - blue));

        newBtn.setId(a);
        newBtn.setText(String.valueOf(a++));
        newBtn.setGravity(Gravity.CENTER);
        newBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //시간을 클릭했을 때 나오는 메세지
            }
        });
        return newBtn;
    }
}
