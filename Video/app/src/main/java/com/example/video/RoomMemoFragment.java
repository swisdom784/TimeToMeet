package com.example.video;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;

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
    ListView memoListView;
    EditText memoEdit;
    ImageButton MakeMemoBtn;
    List<HashMap<String,String>> messageList;
    room room =  new room();

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
        memoListView = view.findViewById(R.id.memoListView);
        memoEdit = view.findViewById(R.id.memoEdit);
        MakeMemoBtn = view.findViewById(R.id.MakeMemoBtn);

        setMakeMemoBtn();

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
                databaseReference.child("room").child(String.valueOf(room_num)).child("messageList").setValue(messageList);
                loadDataFromDatabase();
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

}