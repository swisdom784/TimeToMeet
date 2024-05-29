package com.example.video;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrance);
        overridePendingTransition(R.anim.horizontal_enter, R.anim.none);

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
                            Toast.makeText(EntranceActivity.this,"비밀번호가 일치하지 않습니다",Toast.LENGTH_SHORT).show();
                        } else {
                            if(!roomList.contains(flag[0])) roomList.add(flag[0]);
                            FirebaseDatabase.getInstance().getReference("UserAccount").child(id).child("roomList").setValue(roomList);
                            Intent intent = new Intent(EntranceActivity.this, RoomShowActivity.class);
                            intent.putExtra("username",username);
                            intent.putExtra("room_num",flag[0]);
                            startActivity(intent);
                            finish();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }
}