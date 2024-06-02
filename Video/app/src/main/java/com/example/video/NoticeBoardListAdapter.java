package com.example.video;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class NoticeBoardListAdapter extends BaseAdapter {
    Context mContext;
    LayoutInflater mLayoutInflater;
    ArrayList<NoticeBoardListElement> Memos;
    public NoticeBoardListAdapter(Context context, ArrayList<NoticeBoardListElement> data){
        mContext = context;
        Memos = data;
        mLayoutInflater = LayoutInflater.from(mContext);
    }
    @Override
    public int getCount() {
        return Memos.size();
    }

    @Override
    public NoticeBoardListElement getItem(int position) {
        return Memos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.noticeboard_list_element, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.content = convertView.findViewById(R.id.content);
            viewHolder.writer = convertView.findViewById(R.id.writer);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // 내용 설정
        viewHolder.content.setText(Memos.get(position).getMemo());
        // 작성자 설정
        viewHolder.writer.setText(Memos.get(position).getWriter());

        return convertView;
    }
    private static class ViewHolder {
        TextView content;
        TextView writer;
    }

}
