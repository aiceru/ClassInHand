package com.iceru.classinhand;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView tv_apply_date;
        public IViewHolderOnClick mListener;

        public ViewHolder(View v, IViewHolderOnClick listener) {
            super(v);
            tv_apply_date = (TextView)v.findViewById(R.id.textview_apply_date);
            this.mListener = listener;
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListener.onClick(v, this.getLayoutPosition());
        }

        public static interface IViewHolderOnClick {
            public void onClick(View v, int position);
        }
    }

    public SeatplansAdapter(ArrayList<Seatplan> seatplans, Context context) {
        this.mDataset = seatplans;
        this.mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardtitle_seatplan, parent, false);
        return new ViewHolder(v, new ViewHolder.IViewHolderOnClick() {
            @Override
            public void onClick(View v, int position) {
                Intent intent = new Intent(mContext, SeatplanDetailActivity.class);
                intent.putExtra(ClassInHandApplication.SEATPLAN_SELECTED_POSITION, position);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Seatplan seatplan = mDataset.get(position);

        GregorianCalendar cal = seatplan.getmApplyDate();

        holder.tv_apply_date.setText(String.valueOf(cal.get(Calendar.YEAR)) + ". " +
                String.valueOf(cal.get(Calendar.MONTH) + 1) + ". " +
                String.valueOf(cal.get(Calendar.DAY_OF_MONTH)) + " ~");
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
