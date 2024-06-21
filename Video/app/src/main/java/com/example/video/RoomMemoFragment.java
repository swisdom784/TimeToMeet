package com.example.video;

import static java.lang.String.valueOf;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RoomMemoFragment extends Fragment {
    private static final String ARG_STRING = "user_name";
    private static final String ARG_INT = "room_num";
    private String user_name;
    private int room_num;
    DatabaseReference databaseReference;
    ScrollView scrollView;
    ListView memoListView;
    EditText memoEdit;
    ImageButton MakeMemoBtn;
    List<HashMap<String,String>> messageList;
    room room =  new room();
    LinearLayout fixedMemoWrapper;
    TextView fixedMemo, fixedMemoWriter;
    boolean isFixed = false;
    HashMap<String,String> currentFixedMemo;

    ArrayList<NoticeBoardListElement> elementList = new ArrayList<>();
    public RoomMemoFragment() {
        // Required empty public constructor
    }

    @NonNull
    public static RoomMemoFragment newInstance(String name, int num){
        RoomMemoFragment fragment = new RoomMemoFragment();
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
        View view = inflater.inflate(R.layout.fragment_room_memo, container, false);
        scrollView = view.findViewById(R.id.mainScrollView);
        memoListView = view.findViewById(R.id.memoListView);
        memoEdit = view.findViewById(R.id.memoEdit);
        MakeMemoBtn = view.findViewById(R.id.MakeMemoBtn);
        fixedMemoWrapper  = view.findViewById(R.id.fixedMemoWrapper);
        fixedMemo = view.findViewById(R.id.fixedMemo);
        fixedMemoWriter = view.findViewById(R.id.fixedMemoWriter);

        setMakeMemoBtn();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("room").child(String.valueOf(room_num));
        loadDataFromDatabase();

        return view;
    }
    private void loadDataFromDatabase(){
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                room r = snapshot.getValue(room.class);
                if(r!= null){
                    updateUI(r);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void updateUI(room r){
        room = r;
        messageList = r.getMessageList();
        elementList = new ArrayList<>();
        for(HashMap<String,String> msg : messageList){
            NoticeBoardListElement nble = new NoticeBoardListElement(msg.get("comment"), msg.get("writer"));
            elementList.add(nble);
        }//room 의 메세지를 elementlist로 옮김
        NoticeBoardListAdapter NBLAdapter = new NoticeBoardListAdapter(getContext(),elementList);
        memoListView.setAdapter(NBLAdapter);
        setListViewHeightBasedOnChildren(memoListView);

        if (r.getFixedMemo().get("comment") != null){
            String comment = r.getFixedMemo().get("comment");
            String writer = r.getFixedMemo().get("writer");
            setFixedMessage(r.getFixedMemo(), comment, writer);
        }else {
            clearFixedMessage();
        }
        //길게 눌렀을 때 메시지 고정
        memoListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showConfirmationDialog(messageList.get(position));
                return true;
            }
        });
        fixedMemoWrapper.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View v){
                showDeleteFixedMessageDialog();
                return true;
            }
        });

        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }
    private void setMakeMemoBtn(){
        MakeMemoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = memoEdit.getText().toString();
                if(s.length() == 0) return;
                memoEdit.setText("");
                HashMap<String, String> chat =new HashMap<>();
                chat.put("comment",s);
                chat.put("writer",user_name);
                messageList.add(chat);
                databaseReference.child("messageList").setValue(messageList)
                        .addOnCompleteListener(task -> loadDataFromDatabase());
            }
        });


    }
    public static void setListViewHeightBasedOnChildren (ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }
    private void showConfirmationDialog(HashMap<String,String> msg){
        androidx.appcompat.app.AlertDialog.Builder builder
                = new androidx.appcompat.app.AlertDialog.Builder(getContext());
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.delete_dialog,null);
        builder.setView(dialogView);
        Dialog dialog = builder.create();
        dialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT
                ,WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView tv_num = dialogView.findViewById(R.id.num_of_room);
        tv_num.setVisibility(View.GONE);
        TextView tv_delete =  dialogView.findViewById(R.id.dialog_message);
        tv_delete.setText("메시지를 상단에 고정할까요?");

        TextView btnYes = dialogView.findViewById(R.id.btn_yes);
        TextView btnNo = dialogView.findViewById(R.id.btn_no);

        btnYes.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                setFixedMessage(msg,msg.get("comment"),msg.get("writer"));
                dialog.dismiss();
            }
        });
        btnNo.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                dialog.dismiss();
            }
        });
        dialog.show();

    }
    private void setFixedMessage(HashMap<String,String> msg,String comment, String writer) {
        isFixed = true;
        fixedMemo.setText(comment);
        fixedMemoWriter.setText(writer);
        fixedMemoWrapper.setVisibility(View.VISIBLE);

        databaseReference.child("fixedMemo").setValue(msg);
        databaseReference.child("isFixed").setValue(true);
    }

    private void showDeleteFixedMessageDialog() {
        androidx.appcompat.app.AlertDialog.Builder builder
                = new androidx.appcompat.app.AlertDialog.Builder(getContext());
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.delete_dialog,null);
        builder.setView(dialogView);
        Dialog dialog = builder.create();
        dialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT
                ,WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView tv_num = dialogView.findViewById(R.id.num_of_room);
        tv_num.setVisibility(View.GONE);
        TextView tv_delete =  dialogView.findViewById(R.id.dialog_message);
        tv_delete.setText("고정 메시지를 삭제할까요?");

        TextView btnYes = dialogView.findViewById(R.id.btn_yes);
        TextView btnNo = dialogView.findViewById(R.id.btn_no);

        btnYes.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                clearFixedMessage();
                dialog.dismiss();
            }
        });
        btnNo.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void clearFixedMessage() {
        isFixed = false;
        fixedMemo.setText("");
        fixedMemoWriter.setText("");
        fixedMemoWrapper.setVisibility(View.GONE);
        databaseReference.child("fixedMemo").removeValue();
        databaseReference.child("isFixed").setValue(false);
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