package com.iceru.classinhand;

import android.app.ActionBar;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by iceru on 14. 12. 18..
 */
public class SeatGridAdapter extends BaseAdapter {
    private ArrayList<Seat> mDataset;
    private Context         mContext;
    private int             mColumns;

    public SeatGridAdapter(ArrayList<Seat> seats, Context context, int columns) {
        this.mDataset = seats;
        this.mContext = context;
        this.mColumns = columns;
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
        //RelativeLayout rlayout = (RelativeLayout)convertView.findViewById(R.id.relativelayout_seat_background);
        final FontFitTextView tv_num = (FontFitTextView)convertView.findViewById(R.id.textview_seated_num);
        final ImageView iv_gender = (ImageView)convertView.findViewById(R.id.imageview_seated_boygirl);
        FontFitTextView tv_name = (FontFitTextView) convertView.findViewById(R.id.textview_seated_name);
        final ImageView iv_seated_left = (ImageView)convertView.findViewById(R.id.imageview_recently_seated_left);
        final ImageView iv_seated_right = (ImageView)convertView.findViewById(R.id.imageview_recently_seated_right);

        Student student = seat.getItsStudent();
        if(student != null) {
            tv_num.setText(String.valueOf(student.getAttendNum()));
            iv_gender.setImageResource(
                    student.isBoy() ? R.drawable.ic_gender_boy : R.drawable.ic_gender_girl);
            tv_name.setText(student.getName());
            iv_gender.post(new Runnable() {
                @Override
                public void run() {
                    int height = tv_num.getHeight();
                    iv_gender.getLayoutParams().height = height;
                    iv_gender.getLayoutParams().width = height - 8;
                    iv_seated_left.getLayoutParams().width = iv_seated_left.getLayoutParams().height = height - 8;
                    iv_seated_right.getLayoutParams().width = iv_seated_right.getLayoutParams().height = height - 8;
                }
            });
        }
        else {
            tv_num.setText(null);
            tv_name.setText(null);
            iv_gender.setImageDrawable(null);
        }

        byte selectedFlag = seat.getSelectedFlag();
        if((selectedFlag & ClassInHandApplication.SEATED_LEFT) != 0) {
            //convertView.setBackgroundColor(mContext.getResources().getColor(R.color.pink_200));
            convertView.setBackgroundResource(R.drawable.gridlineselectedpink);
        }
        else if((selectedFlag & ClassInHandApplication.SEATED_RIGHT) != 0) {
            //convertView.setBackgroundColor(mContext.getResources().getColor(R.color.light_blue_200));
            convertView.setBackgroundResource(R.drawable.gridlineselectedblue);
        }
        else {
            //rlayout.setBackgroundColor(mContext.getResources().getColor(R.color.grey_300));
            if(position % 2 == 0) convertView.setBackgroundResource(R.drawable.gridlineleft);
            else convertView.setBackgroundResource(R.drawable.gridlineright);
        }

        byte seatedFlag = seat.getRecentSeatedFlag();
        if((seatedFlag & ClassInHandApplication.SEATED_LEFT) != 0) {
            iv_seated_left.setVisibility(View.VISIBLE);
        }
        else {
            iv_seated_left.setVisibility(View.GONE);
        }

        if((seatedFlag & ClassInHandApplication.SEATED_RIGHT) != 0) {
            iv_seated_right.setVisibility(View.VISIBLE);
        }
        else {
            iv_seated_right.setVisibility(View.GONE);
        }

        return convertView;
    }
}
