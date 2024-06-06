package com.example.video;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class RoomPeopleFragment extends Fragment {
    private static final String ARG_STRING = "user_name";
    private static final String ARG_INT = "room_num";
    private String user_name;
    private int room_num;
    ListView peopleListView;
    Map<String,Object> map = new HashMap<>();
    ArrayList<String> nameList = new ArrayList<>();
    ArrayList<UserListElement> elementList = new ArrayList<>();
    DatabaseReference databaseReference;

    public RoomPeopleFragment() {
        // Required empty public constructor
    }
    public static RoomPeopleFragment newInstance(String name, int num){
        RoomPeopleFragment fragment = new RoomPeopleFragment();
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
        View view =  inflater.inflate(R.layout.fragment_room_people, container, false);
        peopleListView = view.findViewById(R.id.personList);

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
        map = r.getGuest();
        nameList.addAll(map.keySet());
        for(String name : nameList){
            UserListElement ule = new UserListElement(name);
            elementList.add(ule);
        }
        UserListAdapter ulAdapter = new UserListAdapter(getContext(),elementList);
        peopleListView.setAdapter(ulAdapter);
        peopleListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            public void onItemClick(AdapterView parent, View v, int position, long id){
                Intent intent = new Intent(getActivity(),RoomGuestActivity.class);
                intent.putExtra("username",elementList.get(position).getPeopleName());
                intent.putExtra("room_num",room_num);
                startActivity(intent);
            }
        });

    }//updateUI
}