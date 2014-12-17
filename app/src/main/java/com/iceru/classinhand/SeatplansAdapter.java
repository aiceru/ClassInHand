package com.iceru.classinhand;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by iceru on 14. 12. 15..
 */
public class SeatplansAdapter extends RecyclerView.Adapter<SeatplansAdapter.ViewHolder> {
    private ArrayList<Seatplan> mDataset;
    private Context             mContext;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_apply_date;
        public ExpandableGridView gv_seatplan;

        public ViewHolder(View v) {
            super(v);
            tv_apply_date = (TextView)v.findViewById(R.id.textview_apply_date);
            gv_seatplan = (ExpandableGridView)v.findViewById(R.id.gridview_seats);
            gv_seatplan.setExpanded(true);
        }
    }

    public SeatplansAdapter(ArrayList<Seatplan> seatplans, Context context) {
        this.mDataset = seatplans;
        this.mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.seatplan, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Seatplan seatplan = mDataset.get(position);
        GregorianCalendar cal = seatplan.getmApplyDate();

        holder.tv_apply_date.setText(String.valueOf(cal.get(Calendar.YEAR)) + ". " +
                                     String.valueOf(cal.get(Calendar.MONTH)+1) + ". " +
                                     String.valueOf(cal.get(Calendar.DAY_OF_MONTH)) + ".");
        holder.gv_seatplan.setAdapter(new SeatGridAdapter(seatplan.getmSeats(), mContext));
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public class SeatGridAdapter extends BaseAdapter {
        private ArrayList<Seat> mDataset;
        private TypedArray      mGirlsColorArray;
        private TypedArray      mBoysColorArray;

        public SeatGridAdapter(ArrayList<Seat> seats, Context context) {
            this.mDataset = seats;
            mGirlsColorArray = context.getResources().obtainTypedArray(R.array.girls_ic_colors);
            mBoysColorArray = context.getResources().obtainTypedArray(R.array.boys_ic_colors);
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
            int colorArrayIndex = (int) (Math.random() * mGirlsColorArray.length());

            if(convertView == null) {
                convertView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.seat, parent, false);
            }

            Seat seat = mDataset.get(position);
            TextView tv_num = (TextView)convertView.findViewById(R.id.textview_seated_num);
            ImageView iv_gender = (ImageView)convertView.findViewById(R.id.imageview_seated_boygirl);
            TextView tv_name = (TextView)convertView.findViewById(R.id.textview_seated_name);

            tv_num.setText(String.valueOf(seat.getItsStudent().getAttendNum()));
            iv_gender.setBackgroundColor(
                    seat.getItsStudent().isBoy() ?
                            mBoysColorArray.getColor(colorArrayIndex, 0) :
                            mGirlsColorArray.getColor(colorArrayIndex, 0));
            tv_name.setText(seat.getItsStudent().getName());

            return convertView;
        }
    }
}
