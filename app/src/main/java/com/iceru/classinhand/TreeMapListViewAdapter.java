package com.iceru.classinhand;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collection;
import java.util.TreeMap;

/**
 * Created by iceru on 15. 1. 12..
 */
class TreeMapListViewAdapter extends BaseAdapter {
    private TreeMap<Integer, Student> mDataset;
    private Collection<Student> mDataCollection;
    private LayoutInflater inflater;

    public TreeMapListViewAdapter(Context context, TreeMap<Integer, Student> dataset) {
        this.mDataset = dataset;
        inflater = LayoutInflater.from(context);
        mDataCollection = mDataset.values();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getCount() {
        return mDataset.size();
    }

    @Override
    public Object getItem(int position) {
        return mDataCollection.toArray()[position];
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if(v == null) {
            v = inflater.inflate(R.layout.student_info, parent, false);
        }

        Student s = (Student)getItem(position);

        ImageView iv = (ImageView)v.findViewById(R.id.imageview_gender);
        TextView tv = (TextView)v.findViewById(R.id.textview_name);

        iv.setImageResource(
                s.isBoy() ? R.drawable.ic_gender_boy : R.drawable.ic_gender_girl);
        tv.setText(s.getAttendNum() + ". " + s.getName());

        return v;
    }
}
