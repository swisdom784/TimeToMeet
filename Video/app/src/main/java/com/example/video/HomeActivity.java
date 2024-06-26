package com.example.video;

import static java.lang.String.valueOf;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class HomeActivity extends AppCompatActivity {
    int a = 0,b = 0;
    final String name[] = new String[10];
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    Button makebtn,entbtn;
    TextView erasebtn,userText;
    List<Integer> roomList = new ArrayList<>();
    List<room> roomInfo = new ArrayList<>();
    ArrayList<HomeListElement> elementList = new ArrayList<>();
    ListView roomListView;
    ImageView backBtn;
    String id;

    HomeListAdapter hlAdapter;
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (isFinishing()) {
            overridePendingTransition(R.anim.none, R.anim.horizontal_exit);
        }

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        backBtn = findViewById(R.id.back_to_home2);
        backBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                intent.putExtra("isLogout",1);
                overridePendingTransition(R.anim.fade_in, R.anim.horizontal_exit);
                startActivity(intent);
                finish();
            }
        });


        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("UserAccount");
        id = user.getUid();
        userText = findViewById(R.id.userText);
        mDatabase.child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserAccount u = snapshot.getValue(UserAccount.class);
                name[0] = u.getUsername();
                if(u.getRoomList() != null){
                    roomList = u.getRoomList();
                }
                userText.setText(name[0]);

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
                overridePendingTransition(R.anim.vertical_enter, R.anim.none);
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
                overridePendingTransition(R.anim.vertical_enter, R.anim.none);
            }
        });

        erasebtn = findViewById(R.id.erasebtn);
        erasebtn.setVisibility(View.INVISIBLE);
        erasebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showYesNoDialog();
            }
        });
    }

    @Override
    protected void onStart(){
        super.onStart();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("room");
        databaseReference.addValueEventListener(new ValueEventListener() {
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
                    HomeListElement tempElement = new HomeListElement(roomName, valueOf(Pnum),num);
                    elementList.add(tempElement);
                }
                roomListView = findViewById(R.id.roomListOrigin);
                for(int i= 0;i<roomList.size();i++)
                {
                    HomeListElement check = elementList.get(i);
                    room r = roomInfo.get(roomList.get(i)-1);
                    if(Integer.valueOf(check.getRoomPeople()) != r.getGuest().size()){
                        check.setRoomPeople(valueOf(r.getGuest().size()));
                        elementList.set(i,check);
                    }
                }
                hlAdapter = new HomeListAdapter(getApplicationContext(),elementList,erasebtn);
                roomListView.setAdapter(hlAdapter);
                roomListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                    @Override
                    public void onItemClick(AdapterView parent, View v, int position, long id){
                        //TODO : intent 로 방으로 연결되게 하기
                        Intent intent = new Intent(HomeActivity.this, RoomNavigationActivity.class);
                        intent.putExtra("username",name[0]);
                        intent.putExtra("room_num",hlAdapter.getItem(position).getRoomNum());
                        startActivity(intent);
                        overridePendingTransition(R.anim.fade_in, R.anim.none);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        Log.d("onStart_activated","test_log");
    }//onStart
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
    int eraseNum = 0;
    List<Integer> newRoom = new ArrayList<>();
   private void showYesNoDialog(){
       eraseNum = 0;
       newRoom.clear();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.delete_dialog,null);
        builder.setView(dialogView);
        Dialog dialog = builder.create();
        dialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT
                ,WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
       TextView tv_num = dialogView.findViewById(R.id.num_of_room);

       for(int i=0;i<roomList.size();i++) {
           if (hlAdapter.getItem(i).isChecked()) {
               eraseNum++;
           }
           else {
               newRoom.add(roomList.get(i));
           }
       }
       if(eraseNum == 0){
           showCustomToast("삭제할 방을 선택해주세요");
           return;
       }
       tv_num.setText(valueOf(eraseNum));
        TextView btnYes = dialogView.findViewById(R.id.btn_yes);
        TextView btnNo = dialogView.findViewById(R.id.btn_no);

        btnYes.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                handleDialogResult(true);
                dialog.dismiss();
                erasebtn.setVisibility(View.INVISIBLE);
            }
        });
        btnNo.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                handleDialogResult(false);
                dialog.dismiss();
            }
        });
       dialog.show();

    }
    private void handleDialogResult(boolean isYes) {
        if (isYes) {
            if(eraseNum != 0) {
                roomList.clear();
                roomList.addAll(newRoom);
                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("UserAccount").child(id).child("roomList");
                mDatabase.setValue(roomList);
                elementList.clear();
                a = 0;
                onStart();
            }
        }
        return;
    }
}