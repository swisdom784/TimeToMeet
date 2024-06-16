package com.example.video;

import static java.security.AccessController.getContext;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RoomNavigationActivity extends AppCompatActivity {
    TextView tv_name,tv_password;
    ImageView iv_back;
    DatabaseReference databaseReference;
    String user_name;
    int room_num = 0;
    private boolean isTransactionInProgress = false;

    @Override
    public void onBackPressed() {
        // 백스택에 있는 모든 프래그먼트를 제거하고 액티비티를 종료
        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        super.onBackPressed();
        overridePendingTransition(R.anim.none, R.anim.horizontal_exit);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_navigation);
        overridePendingTransition(R.anim.fade_in, R.anim.none);
        Intent i = getIntent();
        user_name = i.getStringExtra("username");
        room_num = i.getIntExtra("room_num",0);
        tv_name = findViewById(R.id.name);
        tv_password = findViewById(R.id.password);
        databaseReference = FirebaseDatabase.getInstance().getReference();

        //DB에서 데이터 불러옴
        loadDataFromDatabase();

        //하단 네비게이션 바 설정
        setNavigationBar();

        // 첫 화면 설정 -> loadFromDatabase 안에
        iv_back = findViewById(R.id.back_to_home);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    private void setNavigationBar(){
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (isTransactionInProgress) {
                    return false; // 트랜잭션이 이미 진행 중이면 클릭 무시
                }
                Fragment selectedFragment = null;

                int itemId = item.getItemId();
                if (itemId == R.id.nav_home) {
                    selectedFragment = RoomHomeFragment.newInstance(user_name, room_num);
                } else if (itemId == R.id.nav_people) {
                    selectedFragment = RoomPeopleFragment.newInstance(user_name, room_num);
                } else if (itemId == R.id.nav_choice) {
                    selectedFragment = RoomChoiceFragment.newInstance(user_name, room_num);
                } else if (itemId == R.id.nav_memo) {
                    selectedFragment = RoomMemoFragment.newInstance(user_name, room_num);
                }

                if (selectedFragment != null) {
                    isTransactionInProgress = true; // 트랜잭션이 시작됨을 표시
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, selectedFragment)
                            .addToBackStack(null)
                            .commit();
                    return true;
                }
                return false;
            }
        });
    }
    // Fragment 트랜잭션이 완료된 후 호출되는 콜백
    @Override
    public void onAttachFragment(@NonNull Fragment fragment) {
//        super.onAttachFragment(fragment);
        isTransactionInProgress = false; // 트랜잭션이 완료되면 표시 해제
    }

    private void loadDataFromDatabase() {
        databaseReference.child("room").child(String.valueOf(room_num)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                room r = snapshot.getValue(room.class);
                if (r != null) {
                    updateUI(r);
                    replaceFragment();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // 데이터 읽기 실패 처리
                Log.e("RoomNavigationActivity", "Failed to read data", error.toException());
            }
        });
    }

    private void updateUI(room r){
        String ps = r.getPassword();
        tv_password.setTextSize(20);
        tv_password.setText(new StringBuilder().append("입장 코드 : ").append(ps));
        tv_password.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.mainfont01));
        tv_password.setTextColor(getResources().getColor(R.color.colorCharcoal));

        // 방 이름 텍스트뷰 업데이트
        tv_name.setText(r.getname());
    }
    private void replaceFragment() {
        Fragment selectedFragment = RoomHomeFragment.newInstance(user_name, room_num);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, selectedFragment)
                .addToBackStack(null)
                .commit();
    }

}