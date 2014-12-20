package com.iceru.classinhand;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by iceru on 14. 12. 18..
 */
public class SeatGridAdapter extends BaseAdapter {
    private ArrayList<Seat> mDataset;

    public SeatGridAdapter(ArrayList<Seat> seats, Context context) {
        this.mDataset = seats;
    }

    @Override
    public int getCount() {
        return mDataset.size();
    }

    @Override
    public Object getItem(int position) {
        return mDataset.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.seat, parent, false);
        }

        Seat seat = mDataset.get(position);
        TextView tv_num = (TextView)convertView.findViewById(R.id.textview_seated_num);
        ImageView iv_gender = (ImageView)convertView.findViewById(R.id.imageview_seated_boygirl);
        TextView tv_name = (TextView)convertView.findViewById(R.id.textview_seated_name);

        Student student = seat.getItsStudent();
        if(student != null) {
            tv_num.setText(String.valueOf(student.getAttendNum()));
            iv_gender.setImageResource(
                    student.isBoy() ? R.drawable.ic_gender_boy : R.drawable.ic_gender_girl);
            tv_name.setText(student.getName());
        }
        else {
            tv_num.setText(null);
            tv_name.setText(null);
        }

        return convertView;
    }
}
