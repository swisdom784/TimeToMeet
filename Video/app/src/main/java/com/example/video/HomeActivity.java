package com.example.video;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
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
    int a = 0,b = 0;
    final String name[] = new String[10];
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    Button makebtn,entbtn;
    List<Integer> roomList = new ArrayList<>();
    List<room> roomInfo = new ArrayList<>();
    ArrayList<HomeListElement> elementList = new ArrayList<>();
    ListView roomListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("UserAccount");
        String id = user.getUid();
        mDatabase.child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserAccount u = snapshot.getValue(UserAccount.class);
                name[0] = u.getUsername();
                if(u.getRoomList() != null){
                    roomList = u.getRoomList();
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        makebtn = findViewById(R.id.makebtn);
        makebtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public  void onClick(View v){
                Intent intent = new Intent(HomeActivity.this, MakeRoomActivity.class);
                intent.putExtra("username",name[0]);
                intent.putExtra("userid",id);
                startActivity(intent);
                overridePendingTransition(R.anim.horizontal_enter, R.anim.none);
            }
        });
        entbtn = findViewById(R.id.entbtn);
        entbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, EntranceActivity.class);
                intent.putExtra("username",name[0]);
                intent.putExtra("userid",id);
                startActivity(intent);
                overridePendingTransition(R.anim.horizontal_enter, R.anim.none);
            }
        });

    }

    @Override
    protected void onStart(){
        super.onStart();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("room");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getValue() == null) return;
                int s = roomInfo.size();
                for(DataSnapshot child : snapshot.getChildren())
                {
                    room r = child.getValue(room.class);
                    if(b++ < s){
                        room r2 = roomInfo.get(b-1);
                        if(r.getGuest().size() != r2.getGuest().size())
                        {
                            roomInfo.set(b-1,r);
                        }
                        continue;
                    }
                    roomInfo.add(r);
                }
                b = 0;

                for(int i = a; i<roomList.size();i++){
                    a++;
                    if(roomList.size() < a) { a--; break;}
                    int num = roomList.get(i);
                    room r2 = roomInfo.get(num-1);
                    int Pnum = r2.getGuest().size();
                    String roomName = r2.getname();
                    HomeListElement tempElement = new HomeListElement(roomName,String.valueOf(Pnum),num);
                    elementList.add(tempElement);
                }
                roomListView = findViewById(R.id.roomListOrigin);
                for(int i= 0;i<roomList.size();i++)
                {
                    HomeListElement check = elementList.get(i);
                    room r = roomInfo.get(roomList.get(i)-1);
                    if(Integer.valueOf(check.getRoomPeople()) != r.getGuest().size()){
                        check.setRoomPeople(String.valueOf(r.getGuest().size()));
                        elementList.set(i,check);
                    }
                }
                HomeListAdapter hlAdapter = new HomeListAdapter(getApplicationContext(),elementList);
                roomListView.setAdapter(hlAdapter);
                roomListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                    @Override
                    public void onItemClick(AdapterView parent, View v, int position, long id){
                        //TODO : intent 로 방으로 연결되게 하기
                        Intent intent = new Intent(HomeActivity.this, RoomShowActivity.class);
                        intent.putExtra("username",name[0]);
                        intent.putExtra("room_num",hlAdapter.getItem(position).getRoomNum());
                        startActivity(intent);
                        overridePendingTransition(R.anim.horizontal_enter, R.anim.none);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        Log.d("onStart_activated","test_log");
    }//onStart

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (isFinishing()) {
            // back 버튼으로 화면 종료가 야기되면 동작한다.
            overridePendingTransition(R.anim.none, R.anim.horizontal_exit);
        }

    }
}