package com.iceru.classinhand;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by iceru on 14. 12. 18..
 */
public class SeatGridAdapter extends BaseAdapter {
    private ArrayList<Seat> mDataset;
    private Context         mContext;

    public SeatGridAdapter(ArrayList<Seat> seats, Context context) {
        this.mDataset = seats;
        this.mContext = context;
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
        RelativeLayout rlayout = (RelativeLayout)convertView.findViewById(R.id.relativelayout_seat_background);
        TextView tv_num = (TextView)convertView.findViewById(R.id.textview_seated_num);
        ImageView iv_gender = (ImageView)convertView.findViewById(R.id.imageview_seated_boygirl);
        TextView tv_name = (TextView)convertView.findViewById(R.id.textview_seated_name);
        ImageView iv_seated_left = (ImageView)convertView.findViewById(R.id.imageview_recently_seated_left);
        ImageView iv_seated_right = (ImageView)convertView.findViewById(R.id.imageview_recently_seated_right);

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
            iv_gender.setImageDrawable(null);
        }

        switch(seat.getSelected()) {
            case ClassInHandApplication.SEATED_LEFT:
                rlayout.setBackgroundColor(mContext.getResources().getColor(R.color.pink_200));
                break;
            case ClassInHandApplication.SEATED_RIGHT:
                rlayout.setBackgroundColor(mContext.getResources().getColor(R.color.light_blue_200));
                break;
            default:
                rlayout.setBackgroundColor(mContext.getResources().getColor(R.color.grey_300));
                break;
        }

        switch(seat.getRecentSeatedLev()) {
            case ClassInHandApplication.SEATED_LEFT:
                iv_seated_left.setImageResource(R.drawable.ic_error_pink_500_24px);
                iv_seated_left.setVisibility(View.VISIBLE);
                iv_seated_right.setVisibility(View.GONE);
                break;
            case ClassInHandApplication.SEATED_RIGHT:
                iv_seated_right.setImageResource(R.drawable.ic_error_custom_24px);
                iv_seated_right.setVisibility(View.VISIBLE);
                iv_seated_left.setVisibility(View.GONE);
                break;
            case ClassInHandApplication.SEATED_BOTH:
                iv_seated_left.setImageResource(R.drawable.ic_error_pink_500_24px);
                iv_seated_left.setVisibility(View.VISIBLE);
                iv_seated_right.setImageResource(R.drawable.ic_error_custom_24px);
                iv_seated_right.setVisibility(View.VISIBLE);
                break;
            default:
                iv_seated_left.setVisibility(View.GONE);
                iv_seated_right.setVisibility(View.GONE);
                break;
        }

        return convertView;
    }
}
