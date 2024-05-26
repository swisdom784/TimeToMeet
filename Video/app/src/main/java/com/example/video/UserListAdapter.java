package com.example.video;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.auth.data.model.User;

import java.util.ArrayList;

public class UserListAdapter extends BaseAdapter {
    Context mContext;
    LayoutInflater mLayoutInflater;
    ArrayList<UserListElement> Users;
    public UserListAdapter(Context context, ArrayList<UserListElement>data){
        mContext = context;
        Users = data;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return Users.size();
    }

    @Override
    public UserListElement getItem(int position) {
        return Users.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = mLayoutInflater.inflate(R.layout.user_list_element,null);
        ImageView Logo1 = view.findViewById(R.id.logoImage1);
        TextView peopleName = view.findViewById(R.id.peopleName);

        peopleName.setText(Users.get(position).getPeopleName());
        Logo1.setImageResource(R.drawable.logo01);
        return view;
    }
}
