package com.example.video;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class HomeListAdapter extends BaseAdapter {
    Context mContext;
    LayoutInflater mLayoutInflater;
    ArrayList<HomeListElement> roomSample;

    Button erase;

    int eraseNum=0;

    public HomeListAdapter(Context context, ArrayList<HomeListElement> data, Button erasebtn){
        mContext = context;
        roomSample = data;
        mLayoutInflater = LayoutInflater.from(mContext);
        erase = erasebtn;
    }

    @Override
    public int getCount(){return roomSample.size();}
    @Override
    public long getItemId(int position){return position;}
    @Override
    public HomeListElement getItem(int position){return roomSample.get(position);}
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.home_list_element, null);
        }

        ImageView Logo1 = convertView.findViewById(R.id.logoImage);
        TextView Number = convertView.findViewById(R.id.roomNumber);
        ImageView PeopleLogo = convertView.findViewById(R.id.peopleImage);
        TextView People = convertView.findViewById(R.id.roomPeople);
        CheckBox checkBox = convertView.findViewById(R.id.checkbox);

        HomeListElement element = roomSample.get(position);

        Number.setText(element.getRoomNumber());
        Number.setSelected(true);
        People.setText(element.getRoomPeople());
        Logo1.setImageResource(R.drawable.logo01);
        PeopleLogo.setImageResource(R.drawable.ic_baseline_person_gray);
        checkBox.setChecked(element.isChecked());

        MainActivity main = new MainActivity();
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked)
                eraseNum+=1;
            else
                eraseNum-=1;
            if(eraseNum > 0)
                erase.setVisibility(View.VISIBLE);
            else
                erase.setVisibility(View.INVISIBLE);
            element.setChecked(isChecked);
            // If needed, you can handle other logic here, like updating a database or showing a Toast
        });

        return convertView;
    }


}