package com.iceru.classinhand;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.ArrayList;

/**
 * Created by iceru on 14. 12. 18..
 */
public class SeatGridAdapter extends BaseAdapter {

    private ArrayList<Seat> mDataset;
    private Context         mContext;
    private int             mSegment;
    private InnerViewHolder.ISeatClick mListener;

    public static class OuterViewHolder {
        public InnerViewHolder lvh;
        public InnerViewHolder rvh;

        public OuterViewHolder(View v, InnerViewHolder.ISeatClick listener, int position) {
            lvh = new InnerViewHolder(v, listener, position*2);
            rvh = new InnerViewHolder(v, listener, position*2+1);
        }
    }

    public static class InnerViewHolder implements View.OnClickListener {
        public ISeatClick   mListener;
        public int          mSeatId;
        public int          mAdjustedViewHeight;

        public RelativeLayout  rlayout;
        public ImageView       imageviewFixed;
        public FontFitTextView textviewNum;
        public ImageView       imageviewGender;
        public FontFitTextView textviewName;
        public ImageView       imageviewSeatedLeft;
        public ImageView       imageviewSeatedRight;

        public InnerViewHolder(View v, ISeatClick listener, int seatId) {
            mSeatId = seatId;
            mListener = listener;

            FontFitTextView.OnLayoutChagnedListener viewsizeObserver = new FontFitTextView.OnLayoutChagnedListener() {
                @Override
                public void onLayout(float size) {
                    imageviewGender.getLayoutParams().width = (int)size;
                    imageviewGender.getLayoutParams().height = (int)size;
                    imageviewSeatedLeft.getLayoutParams().width = (int)size;
                    imageviewSeatedLeft.getLayoutParams().height = (int)size;
                    imageviewSeatedRight.getLayoutParams().width = (int)size;
                    imageviewSeatedRight.getLayoutParams().height = (int)size;
                }
            };

            if(seatId % 2 == 0) {
                rlayout = (RelativeLayout) v.findViewById(R.id.relativelayout_seat_background_left);
                imageviewFixed = (ImageView) v.findViewById(R.id.imageview_seat_fixed_left);
                textviewNum = (FontFitTextView) v.findViewById(R.id.textview_seated_num_left);
                textviewNum.setLayoutChagnedListener(viewsizeObserver);
                imageviewGender = (ImageView) v.findViewById(R.id.imageview_seated_boygirl_left);
                textviewName = (FontFitTextView) v.findViewById(R.id.textview_seated_name_left);
                imageviewSeatedLeft = (ImageView) v.findViewById(R.id.imageview_recently_seated_left_left);
                imageviewSeatedRight = (ImageView) v.findViewById(R.id.imageview_recently_seated_right_left);
            }
            else {
                rlayout = (RelativeLayout) v.findViewById(R.id.relativelayout_seat_background_right);
                imageviewFixed = (ImageView) v.findViewById(R.id.imageview_seat_fixed_right);
                textviewNum = (FontFitTextView) v.findViewById(R.id.textview_seated_num_right);
                textviewNum.setLayoutChagnedListener(viewsizeObserver);
                imageviewGender = (ImageView) v.findViewById(R.id.imageview_seated_boygirl_right);
                textviewName = (FontFitTextView) v.findViewById(R.id.textview_seated_name_right);
                imageviewSeatedLeft = (ImageView) v.findViewById(R.id.imageview_recently_seated_left_right);
                imageviewSeatedRight = (ImageView) v.findViewById(R.id.imageview_recently_seated_right_right);
            }
            rlayout.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(mListener != null) {
                mListener.seatClick(v, mSeatId);
            }
        }

        public static interface ISeatClick {
            public void seatClick(View v, int seatId);
        }
    }

    public SeatGridAdapter(ArrayList<Seat> seats, Context context, int segment,
                           InnerViewHolder.ISeatClick listener) {
        this.mDataset = seats;
        this.mContext = context;
        this.mSegment = segment;
        this.mListener = listener;
    }

    @Override
    public int getCount() {
        return (mDataset.size()+1) / 2;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        final OuterViewHolder ovh;
        Seat leftSeat = mDataset.get(position*2);
        Seat rightSeat = position*2+1 >= mDataset.size()? null : mDataset.get(position*2+1);
        Student leftStudent = leftSeat.getItsStudent();
        Student rightStudent = rightSeat.getItsStudent();

        if(convertView == null) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.seat, parent, false);
            ovh = new OuterViewHolder(view, mListener, position);

            view.setTag(ovh);
        }
        else {
            ovh = (OuterViewHolder)view.getTag();
        }

        if(leftStudent != null) {
            ovh.lvh.textviewNum.setText(String.valueOf(leftStudent.getAttendNum()));
            ovh.lvh.textviewName.setText(leftStudent.getName());
            ovh.lvh.imageviewGender.setImageResource(
                    leftStudent.isBoy()? R.drawable.ic_gender_boy : R.drawable.ic_gender_girl);
            /*ovh.lvh.rlayout.post(new Runnable() {
                @Override
                public void run() {
                    int h = ovh.lvh.textviewNum.getHeight();
                    ovh.lvh.imageviewGender.getLayoutParams().height = h;
                    ovh.lvh.imageviewGender.getLayoutParams().width = h-8;
                    ovh.lvh.imageviewSeatedLeft.getLayoutParams().width =
                            ovh.lvh.imageviewSeatedLeft.getLayoutParams().height = h-8;
                    ovh.lvh.imageviewSeatedRight.getLayoutParams().width =
                            ovh.lvh.imageviewSeatedRight.getLayoutParams().height = h-8;
                }
            });*/
        }
        else {
            ovh.lvh.textviewNum.setText(null);
            ovh.lvh.textviewName.setText(null);
            ovh.lvh.imageviewGender.setImageDrawable(null);
        }

        if(rightStudent != null) {
            ovh.rvh.textviewNum.setText(String.valueOf(rightStudent.getAttendNum()));
            ovh.rvh.textviewName.setText(rightStudent.getName());
            ovh.rvh.imageviewGender.setImageResource(
                    rightStudent.isBoy() ? R.drawable.ic_gender_boy : R.drawable.ic_gender_girl);
            /*ovh.rvh.rlayout.post(new Runnable() {
                @Override
                public void run() {
                    int h = ovh.rvh.textviewNum.getHeight();
                    ovh.rvh.imageviewGender.getLayoutParams().height = h;
                    ovh.rvh.imageviewGender.getLayoutParams().width = h - 8;
                    ovh.rvh.imageviewSeatedLeft.getLayoutParams().width =
                            ovh.rvh.imageviewSeatedLeft.getLayoutParams().height = h - 8;
                    ovh.rvh.imageviewSeatedRight.getLayoutParams().width =
                            ovh.rvh.imageviewSeatedRight.getLayoutParams().height = h - 8;
                }
            });*/
        }
        else {
            ovh.rvh.textviewNum.setText(null);
            ovh.rvh.textviewName.setText(null);
            ovh.rvh.imageviewGender.setImageDrawable(null);
        }


        byte selectedFlag = leftSeat.getSelectedFlag();
        if((selectedFlag & ClassInHandApplication.SEATED_LEFT) != 0) {
            ovh.lvh.rlayout.setBackgroundResource(R.drawable.seat_bg_pink_200);
        }
        else if((selectedFlag & ClassInHandApplication.SEATED_RIGHT) != 0) {
            ovh.lvh.rlayout.setBackgroundResource(R.drawable.seat_bg_light_blue_200);
        }
        else {
            ovh.lvh.rlayout.setBackgroundResource(R.drawable.seat_bg);
        }
        selectedFlag = rightSeat.getSelectedFlag();
        if((selectedFlag & ClassInHandApplication.SEATED_LEFT) != 0) {
            ovh.rvh.rlayout.setBackgroundResource(R.drawable.seat_bg_pink_200);
        }
        else if((selectedFlag & ClassInHandApplication.SEATED_RIGHT) != 0) {
            ovh.rvh.rlayout.setBackgroundResource(R.drawable.seat_bg_light_blue_200);
        }
        else {
            ovh.rvh.rlayout.setBackgroundResource(R.drawable.seat_bg);
        }

        byte seatedFlag = leftSeat.getRecentSeatedFlag();
        if((seatedFlag & ClassInHandApplication.SEATED_LEFT) != 0) {
            ovh.lvh.imageviewSeatedLeft.setVisibility(View.VISIBLE);
        }
        else {
            ovh.lvh.imageviewSeatedLeft.setVisibility(View.GONE);
        }

        if((seatedFlag & ClassInHandApplication.SEATED_RIGHT) != 0) {
            ovh.lvh.imageviewSeatedRight.setVisibility(View.VISIBLE);
        }
        else {
            ovh.lvh.imageviewSeatedRight.setVisibility(View.GONE);
        }

        seatedFlag = rightSeat.getRecentSeatedFlag();
        if((seatedFlag & ClassInHandApplication.SEATED_LEFT) != 0) {
            ovh.rvh.imageviewSeatedLeft.setVisibility(View.VISIBLE);
        }
        else {
            ovh.rvh.imageviewSeatedLeft.setVisibility(View.GONE);
        }

        if((seatedFlag & ClassInHandApplication.SEATED_RIGHT) != 0) {
            ovh.rvh.imageviewSeatedRight.setVisibility(View.VISIBLE);
        }
        else {
            ovh.rvh.imageviewSeatedRight.setVisibility(View.GONE);
        }

        if(leftSeat.isFixed()) ovh.lvh.imageviewFixed.setVisibility(View.VISIBLE);
        else ovh.lvh.imageviewFixed.setVisibility(View.GONE);

        if(rightSeat.isFixed()) ovh.rvh.imageviewFixed.setVisibility(View.VISIBLE);
        else ovh.rvh.imageviewFixed.setVisibility(View.GONE);

        return view;
    }
}
