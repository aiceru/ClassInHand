package com.iceru.classinhand;

import android.content.Context;
import android.util.TypedValue;
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
    private int             mColumns;
    private int             mAdjustedTextSize;
    private int             mAdjustedImageSize;
    private static final int    mTextWidth_Dividend = 112;
    private static final int    mTextSize_Max = 16;
    private static final int    mImageWidth_Dividend = 288;
    private static final int    mImageSize_Max = 50;

    public SeatGridAdapter(ArrayList<Seat> seats, Context context, int columns) {
        this.mDataset = seats;
        this.mContext = context;
        this.mColumns = columns;
        this.mAdjustedTextSize = mTextWidth_Dividend / mColumns > mTextSize_Max? mTextSize_Max : mTextWidth_Dividend / mColumns;
        this.mAdjustedImageSize = mImageWidth_Dividend / mColumns > mImageSize_Max? mImageSize_Max : mImageWidth_Dividend / mColumns;
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

        tv_num.setTextSize(TypedValue.COMPLEX_UNIT_SP, mAdjustedTextSize);
        tv_name.setTextSize(TypedValue.COMPLEX_UNIT_SP, mAdjustedTextSize);
        iv_gender.getLayoutParams().width = mAdjustedImageSize;
        iv_seated_left.getLayoutParams().width = mAdjustedImageSize;
        iv_seated_right.getLayoutParams().width = mAdjustedImageSize;

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

        byte selectedFlag = seat.getSelectedFlag();
        if((selectedFlag & ClassInHandApplication.SEATED_LEFT) != 0) {
            rlayout.setBackgroundColor(mContext.getResources().getColor(R.color.pink_200));
        }
        else if((selectedFlag & ClassInHandApplication.SEATED_RIGHT) != 0) {
            rlayout.setBackgroundColor(mContext.getResources().getColor(R.color.light_blue_200));
        }
        else {
            rlayout.setBackgroundColor(mContext.getResources().getColor(R.color.grey_300));
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
