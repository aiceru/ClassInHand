package com.iceru.classinhand;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.security.cert.CollectionCertStoreParameters;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by iceru on 14. 11. 19..
 */
public class StudentListAdapter extends RecyclerView.Adapter<StudentListAdapter.ViewHolder> {

    private TreeMap<Integer, Student>       mDataset;
    private Collection<Student>             mDataCollection;
    private TypedArray                      mGirlsColorArray;
    private TypedArray                      mBoysColorArray;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        //public TextView     tv_attend_num;
        public TextView     tv_name;
        public ImageView    iv_gender;

        public ViewHolder(View v) {
            super(v);
            //tv_attend_num = (TextView)v.findViewById(R.id.textview_attend_num);
            tv_name = (TextView)v.findViewById(R.id.textview_name);
            iv_gender = (ImageView)v.findViewById(R.id.imageview_gender);
        }
    }

    public StudentListAdapter(TreeMap<Integer, Student> dataset, Context context) {
        mDataset = dataset;
        mDataCollection = mDataset.values();
        mGirlsColorArray = context.getResources().obtainTypedArray(R.array.girls_ic_colors);
        mBoysColorArray = context.getResources().obtainTypedArray(R.array.boys_ic_colors);
    }

    @Override
    public StudentListAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.student_info, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        Student s = (Student)this.getItem(i);
        int colorArrayIndex = (int)(Math.random() * mGirlsColorArray.length());

        //viewHolder.tv_attend_num.setText(String.valueOf(s.getAttendNum()));
        viewHolder.tv_name.setText(String.valueOf((s.getAttendNum())) + ". " + s.getName());
        viewHolder.iv_gender.setBackgroundColor(
                s.isBoy() ?
                mBoysColorArray.getColor(colorArrayIndex, 0) :
                mGirlsColorArray.getColor(colorArrayIndex, 0));
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public Object getItem(int position) {
        return mDataCollection.toArray()[position];
    }
}
