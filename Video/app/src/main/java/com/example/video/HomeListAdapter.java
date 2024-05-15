package com.example.video;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import org.checkerframework.checker.units.qual.C;

import java.util.ArrayList;

public class HomeListAdapter extends BaseAdapter {
    Context mContext;
    LayoutInflater mLayoutInflater;
    ArrayList<HomeListElement> roomSample;

    public HomeListAdapter(Context context, ArrayList<HomeListElement> data){
        mContext = context;
        roomSample = data;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount(){return roomSample.size();}
    @Override
    public long getItemId(int position){return position;}
    @Override
    public HomeListElement getItem(int position){return roomSample.get(position);}
    @Override
    public View getView(int position, View converView, ViewGroup parent){
        //ConstraintLayout root = new ConstraintLayout(mContext);
        View view = mLayoutInflater.inflate(R.layout.home_list_element,null);
        ImageView Logo1 = view.findViewById(R.id.logoImage);
        TextView Number = view.findViewById(R.id.roomNumber);
        ImageView PeopleLogo = view.findViewById(R.id.peopleImage);
        TextView People = view.findViewById(R.id.roomPeople);

        Number.setText(roomSample.get(position).getRoomNumber());
        People.setText(roomSample.get(position).getRoomPeople());
        return view;
    }


}
