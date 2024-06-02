package com.example.video;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NoticeBoardActivity extends AppCompatActivity {
    //memolist 구현용 변수
    room room =  new room();
    String user;
    int roomNum;
    ListView memoListView;
    ArrayList<NoticeBoardListElement> elementList = new ArrayList<>();

    EditText memoEdit;

    //DB 구현용 변수
    ValueEventListener valueEventListener;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    Button MakeMemoBtn;

    List<HashMap<String,String>> messageList;

    //???? 변수


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_board);
        Intent i = getIntent();
        String username = i.getStringExtra("username");
        user = username;
        int room_num = i.getIntExtra("room_num",0);
        roomNum = room_num;
        memoListView = findViewById(R.id.memoListView);
        memoEdit = findViewById(R.id.memoEdit);
        MakeMemoBtn = findViewById(R.id.MakeMemoBtn);

        MakeMemoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = memoEdit.getText().toString();
                if(s.length() == 0) return;
                memoEdit.setText("");
                HashMap<String, String> chat =new HashMap<>();
                chat.put("comment",s);
                chat.put("writer",user);
                messageList.add(chat);
                databaseReference.child("room").child(String.valueOf(room_num)).child("messageList").setValue(messageList);
            }
        });
        databaseReference.child("room").child(String.valueOf(room_num)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //TODO : 현재는 최초 생성시만 메모 들어감, 메모를 편집해서 업로드할 시 바로 반영되게 할 것
                room r = snapshot.getValue(room.class);
                room = r;
                messageList = r.getMessageList();
                elementList = new ArrayList<>();
                //TODO : FOR DEBUG
//                message temp_msg = new message("sample comment","writer");
//                HashMap<String,String> s = new HashMap<>();
//                s.put("comment","sample comment");
//                s.put("writer","sample writer");
//                messageList.add(s);
//                messageList.add(s);
                //TODO : FOR DEBUG

                for(HashMap<String,String> msg : messageList){
                    NoticeBoardListElement nble = new NoticeBoardListElement(msg.get("comment"), msg.get("writer"));
                    elementList.add(nble);
                }//room 의 메세지를 elementlist로 옮김
                NoticeBoardListAdapter NBLAdapter = new NoticeBoardListAdapter(getApplicationContext(),elementList);
                memoListView.setAdapter(NBLAdapter);
                //어댑터 장착
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        memoListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            public void onItemClick(AdapterView parent, View v, int position, long id){
                //TODO : 각 메모 클릭 시 이벤트
            }
        });

    }//onCreate
}