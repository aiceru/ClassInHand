package com.iceru.classinhand;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.security.cert.CollectionCertStoreParameters;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by iceru on 14. 11. 19..
 */
public class StudentListAdapter extends RecyclerView.Adapter<StudentListAdapter.ViewHolder> {

    private ArrayList<Student> mDataset;
    private Context            mContext;
    //private Collection<Student> mDataCollection;

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public LinearLayout linearLayout;
        public TextView tv_name;
        public ImageView iv_gender;
        public IViewHolderClick mListener;

        public ViewHolder(View v, IViewHolderClick listener) {
            super(v);
            linearLayout = (LinearLayout)v.findViewById(R.id.linearlayout_studentlist);
            tv_name = (TextView) v.findViewById(R.id.textview_name);
            iv_gender = (ImageView) v.findViewById(R.id.imageview_gender);
            mListener = listener;
        }

        @Override
        public void onClick(View v) {
            mListener.viewholderClick(v, this.getLayoutPosition());
        }

        public static interface IViewHolderClick {
            public void viewholderClick(View v, int position);
        }
    }

    public StudentListAdapter(ArrayList<Student> dataset, Context context) {
        mDataset = dataset;
        mContext = context;
    }

    @Override
    public StudentListAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.student_info, viewGroup, false);
        StudentListAdapter.ViewHolder vh = new ViewHolder(v, new StudentListAdapter.ViewHolder.IViewHolderClick() {
            public void viewholderClick(View v, int position) {
                Student s = mDataset.get(position);
                Intent intent = new Intent(mContext, StudentDetailActivity.class);
                intent.putExtra(ClassInHandApplication.STUDENT_SELECTED_ID, s.getId());
                mContext.startActivity(intent);
            }
        });
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        Student s = (Student) this.getItem(i);

        viewHolder.tv_name.setText(String.valueOf((s.getAttendNum())) + ". " + s.getName());
        viewHolder.iv_gender.setImageResource(
                s.isBoy() ? R.drawable.ic_gender_boy : R.drawable.ic_gender_girl);

        if(!s.isInClass()) {
            viewHolder.linearLayout.setBackgroundColor(mContext.getResources().getColor(R.color.grey_300));
            viewHolder.tv_name.setPaintFlags(viewHolder.tv_name.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
        else {
            viewHolder.linearLayout.setBackgroundColor(mContext.getResources().getColor(R.color.white));
            viewHolder.tv_name.setPaintFlags(viewHolder.tv_name.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
        }
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public Object getItem(int position) {
        return mDataset.get(position);
    }
}