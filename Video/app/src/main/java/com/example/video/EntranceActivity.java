package com.example.video;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class EntranceActivity extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    Button entbtn;
    EditText password;
    List<Integer> roomList = new ArrayList<>();
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.none, R.anim.horizontal_exit);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrance);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

        String username = getIntent().getStringExtra("username");
        String id = getIntent().getStringExtra("userid");
        entbtn = findViewById(R.id.entbtn);
        databaseReference = FirebaseDatabase.getInstance().getReference("UserAccount").child(id);
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

        entbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                password = findViewById(R.id.password);
                String p = password.getText().toString();
                if(p.isEmpty()){
                    showCustomToast("입장 코드를 입력하세요\n입장 코드는 16자리입니다");
                    return;
                }

                final int[] flag = {0};
                databaseReference = FirebaseDatabase.getInstance().getReference("room");
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for(DataSnapshot child : snapshot.getChildren()){
                                    if(p.equals(child.child("password").getValue())){
                                        flag[0] = Integer.valueOf(child.getKey());
                                        System.out.println(flag[0]);
                                        break;
                                    }
                                }
                                if(flag[0] == 0){
                                    showCustomToast("입장 코드가 일치하지 않습니다");
                        } else {
                            if(!roomList.contains(flag[0])) roomList.add(flag[0]);
                            FirebaseDatabase.getInstance().getReference("UserAccount").child(id).child("roomList").setValue(roomList);
                            Intent intent = new Intent(EntranceActivity.this, RoomNavigationActivity.class);
                            intent.putExtra("username",username);
                            intent.putExtra("room_num",flag[0]);
                            startActivity(intent);
                            overridePendingTransition(R.anim.none, R.anim.fade_out);
                            finish();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
        ImageView iv_home = findViewById(R.id.back_to_home);
        iv_home.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                onBackPressed();
            }
        });

    }
    private void showCustomToast(String message) {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast, null);

        TextView text = layout.findViewById(R.id.toast_text);
        text.setText(message);

        ImageView image = layout.findViewById(R.id.toast_image);
        image.setImageResource(R.drawable.logo01); // 원하는 아이콘 리소스 설정

        Toast toast = new Toast(getApplicationContext());
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }
}