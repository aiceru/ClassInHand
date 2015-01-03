package com.iceru.classinhand;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by iceru on 14. 12. 15..
 */
public class SeatplansAdapter extends RecyclerView.Adapter<SeatplansAdapter.ViewHolder> {
    private TreeMap<GregorianCalendar, Seatplan> mDataset;
    private Context             mContext;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_apply_date;
        public ExpandableGridView gv_seats;

        public ViewHolder(View v) {
            super(v);
            tv_apply_date = (TextView)v.findViewById(R.id.textview_apply_date);
            gv_seats = (ExpandableGridView)v.findViewById(R.id.gridview_seats);
            gv_seats.setExpanded(true);
        }
    }

    public SeatplansAdapter(TreeMap<GregorianCalendar, Seatplan> seatplans, Context context) {
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
        Seatplan seatplan = (Seatplan)(mDataset.values().toArray()[position]);

        GregorianCalendar cal = seatplan.getmApplyDate();

        holder.tv_apply_date.setText(String.valueOf(cal.get(Calendar.YEAR)) + ". " +
                String.valueOf(cal.get(Calendar.MONTH) + 1) + ". " +
                String.valueOf(cal.get(Calendar.DAY_OF_MONTH)) + ".");
        holder.gv_seats.setAdapter(new SeatGridAdapter(seatplan.getmSeats(), mContext));
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
