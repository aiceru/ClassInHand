package com.iceru.classinhand;

import android.content.ClipData;
import android.content.ClipDescription;
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

    private ArrayList<Seat> mSeats;
    private Context         mContext;
    private int             mSegment;

    private InnerViewHolder.ISeatClick mListener;
    private InnerViewHolder.ISeatLongClick mLongClickListener;
    private SeatplanEditActivity.DragEventListenerOfSeats mDragListener;

    public SeatGridAdapter(ArrayList<Seat> seats, Context context, int segment,
                           InnerViewHolder.ISeatClick listener,
                           InnerViewHolder.ISeatLongClick llistener,
                           SeatplanEditActivity.DragEventListenerOfSeats dListener) {
        this.mSeats = seats;
        this.mContext = context;
        this.mSegment = segment;
        this.mListener = listener;
        this.mLongClickListener = llistener;
        this.mDragListener = dListener;
    }

    @Override
    public int getCount() {
        return (mSeats.size()+1) / 2;
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
        Seat leftSeat = mSeats.get(position*2);
        Seat rightSeat = mSeats.get(position*2+1);
        Student leftStudent = leftSeat.getItsStudent();
        Student rightStudent = rightSeat.getItsStudent();

        if(convertView == null) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.seat, parent, false);
            ovh = new OuterViewHolder(view, mListener, mLongClickListener, position);

            view.setTag(ovh);
        }
        else {
            ovh = (OuterViewHolder)view.getTag();
        }

        ovh.lvh.rlayout.setTag(leftSeat.getId());
        ovh.rvh.rlayout.setTag(rightSeat.getId());

        ovh.lvh.rlayout.setOnDragListener(mDragListener);
        ovh.rvh.rlayout.setOnDragListener(mDragListener);

        if(leftStudent != null) {
            ovh.lvh.textviewNum.setText(String.valueOf(leftStudent.getAttendNum()));
            ovh.lvh.textviewName.setText(leftStudent.getName());
            ovh.lvh.imageviewGender.setImageResource(
                    leftStudent.isBoy()? R.drawable.ic_gender_boy : R.drawable.ic_gender_girl);
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
        }
        else {
            ovh.rvh.textviewNum.setText(null);
            ovh.rvh.textviewName.setText(null);
            ovh.rvh.imageviewGender.setImageDrawable(null);
        }

        if(leftSeat.getSelectedFlag()) {
            ovh.lvh.rlayout.setBackgroundResource(R.drawable.seat_bg_pink_200);
        }
        else {
            ovh.lvh.rlayout.setBackgroundResource(R.drawable.seat_bg);
        }

        if(rightSeat.getSelectedFlag()) {
            ovh.rvh.rlayout.setBackgroundResource(R.drawable.seat_bg_pink_200);
        }
        else {
            ovh.rvh.rlayout.setBackgroundResource(R.drawable.seat_bg);
        }

        if(leftSeat.getRecentSeatedFlag()) {
            ovh.lvh.imageViewSeated.setVisibility(View.VISIBLE);
        }
        else {
            ovh.lvh.imageViewSeated.setVisibility(View.GONE);
        }

        if(rightSeat.getRecentSeatedFlag()) {
            ovh.rvh.imageViewSeated.setVisibility(View.VISIBLE);
        }
        else {
            ovh.rvh.imageViewSeated.setVisibility(View.GONE);
        }

        if(leftSeat.isFixed()) ovh.lvh.imageviewFixed.setVisibility(View.VISIBLE);
        else ovh.lvh.imageviewFixed.setVisibility(View.GONE);

        if(rightSeat.isFixed()) ovh.rvh.imageviewFixed.setVisibility(View.VISIBLE);
        else ovh.rvh.imageviewFixed.setVisibility(View.GONE);

        return view;
    }

    public static class OuterViewHolder {
        public InnerViewHolder lvh;
        public InnerViewHolder rvh;

        public OuterViewHolder(View v, InnerViewHolder.ISeatClick listener,
                               InnerViewHolder.ISeatLongClick llistener, int position) {
            lvh = new InnerViewHolder(v, listener, llistener, position*2);
            rvh = new InnerViewHolder(v, listener, llistener, position*2+1);
        }
    }

    public static class InnerViewHolder implements View.OnClickListener, View.OnLongClickListener {
        public ISeatClick   mOnClickListener;
        public ISeatLongClick mOnLongClickListener;
        public int          mSeatId;

        public RelativeLayout  rlayout;
        public ImageView       imageviewFixed;
        public FontFitTextView textviewNum;
        public ImageView       imageviewGender;
        public FontFitTextView textviewName;
        public ImageView       imageViewSeated;

        public InnerViewHolder(View v, ISeatClick listener, ISeatLongClick llistener, int seatId) {
            mSeatId = seatId;
            mOnClickListener = listener;
            mOnLongClickListener = llistener;

            FontFitTextView.OnLayoutChagnedListener viewsizeObserver = new FontFitTextView.OnLayoutChagnedListener() {
                @Override
                public void onLayout(float size) {
                    imageviewGender.getLayoutParams().width = (int)size;
                    imageviewGender.getLayoutParams().height = (int)size;
                    imageViewSeated.getLayoutParams().width = (int)size;
                    imageViewSeated.getLayoutParams().height = (int)size;
                }
            };

            if(seatId % 2 == 0) {
                rlayout = (RelativeLayout) v.findViewById(R.id.relativelayout_seat_background_left);
                imageviewFixed = (ImageView) v.findViewById(R.id.imageview_seat_fixed_left);
                textviewNum = (FontFitTextView) v.findViewById(R.id.textview_seated_num_left);
                textviewNum.setLayoutChagnedListener(viewsizeObserver);
                imageviewGender = (ImageView) v.findViewById(R.id.imageview_seated_boygirl_left);
                textviewName = (FontFitTextView) v.findViewById(R.id.textview_seated_name_left);
                imageViewSeated = (ImageView) v.findViewById(R.id.imageview_recently_seated_left);
            }
            else {
                rlayout = (RelativeLayout) v.findViewById(R.id.relativelayout_seat_background_right);
                imageviewFixed = (ImageView) v.findViewById(R.id.imageview_seat_fixed_right);
                textviewNum = (FontFitTextView) v.findViewById(R.id.textview_seated_num_right);
                textviewNum.setLayoutChagnedListener(viewsizeObserver);
                imageviewGender = (ImageView) v.findViewById(R.id.imageview_seated_boygirl_right);
                textviewName = (FontFitTextView) v.findViewById(R.id.textview_seated_name_right);
                imageViewSeated = (ImageView) v.findViewById(R.id.imageview_recently_seated_right);
            }
            rlayout.setOnClickListener(this);
            rlayout.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(mOnClickListener != null) {
                mOnClickListener.seatClick(v);
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if(mOnLongClickListener != null) {
                return mOnLongClickListener.seatLongClick(v);
            }
            return true;
        }

        public static interface ISeatClick {
            public void seatClick(View v);
        }

        public static interface ISeatLongClick {
            public boolean seatLongClick(View v);
        }
    }
}
