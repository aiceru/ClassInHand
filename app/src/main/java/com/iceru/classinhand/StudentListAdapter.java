package com.iceru.classinhand;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.security.cert.CollectionCertStoreParameters;
import java.util.Collection;
import java.util.TreeMap;

/**
 * Created by iceru on 14. 11. 19..
 */
public class StudentListAdapter extends RecyclerView.Adapter<StudentListAdapter.ViewHolder> {

    private Collection<Student>     mDataset;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView     tv_attend_num;
        public TextView     tv_name;
        public RoundedImageView    iv_gender;

        public ViewHolder(View v) {
            super(v);
            tv_attend_num = (TextView)v.findViewById(R.id.textview_attend_num);
            tv_name = (TextView)v.findViewById(R.id.textview_name);
            iv_gender = (RoundedImageView)v.findViewById(R.id.imageview_gender);
        }
    }

    public StudentListAdapter(Collection<Student> dataset) {
        mDataset = dataset;
    }

    @Override
    public StudentListAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.student_info, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        Student s = (Student)this.getItem(i);
        viewHolder.tv_attend_num.setText(String.valueOf(s.getAttendNum()));
        viewHolder.tv_name.setText(s.getName());
        viewHolder.iv_gender.setForegroundColor(s.isBoy()? 0xFFFCE4EC : 0xFFE3F2FD);
        //viewHolder.iv_gender.setImageResource((s.isBoy()? R.drawable.ic_toggle_boy : R.drawable.ic_toggle_girl));
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public Object getItem(int position) {
        Object[] arrayDataset = mDataset.toArray();
        return arrayDataset[position];
    }
}
