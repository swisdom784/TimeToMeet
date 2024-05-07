package com.example.video;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.data.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.protobuf.StringValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class HomeActivity extends AppCompatActivity {

    TextView nameText,password;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseDatabase mDatabase;
    Button makebtn,entbtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        nameText = findViewById(R.id.name);
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("UserAccount");
        String id = user.getUid();
        final String name[] = new String[10];
        mDatabase.child(id).child("username").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                name[0] = snapshot.getValue(String.class);
                nameText.setText(name[0]);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        }).toString();
        makebtn = findViewById(R.id.makebtn);
        makebtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public  void onClick(View v){
                Intent intent = new Intent(HomeActivity.this, MakeRoomActivity.class);
                intent.putExtra("username",name[0]);
                startActivity(intent);
            }
        });
        entbtn = findViewById(R.id.entbtn);
        entbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                password = findViewById(R.id.password);
                String p = password.getText().toString();
                final int[] flag = {0};
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("room");
                databaseReference.addValueEventListener(new ValueEventListener() {
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
                            Toast.makeText(HomeActivity.this,"비밀번호가 일치하지 않습니다",Toast.LENGTH_SHORT).show();
                        } else {
                            Intent intent = new Intent(HomeActivity.this, RoomActivity.class);
                            intent.putExtra("username",name[0]);
                            intent.putExtra("room_num",flag[0]);
                            startActivity(intent);
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