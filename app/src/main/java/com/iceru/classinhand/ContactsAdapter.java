package com.iceru.classinhand;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by iceru on 15. 5. 9..
 */
public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder> {
    private static final String TAG = ContactsAdapter.class.getName();

    private ArrayList<Student> mDataset;
    private Context mContext;
    private SparseBooleanArray mSelectedPositions;

    public static class ViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener {
        public ImageView iv_icon;
        public TextView tv_name;
        public TextView tv_phoneNumber;
        public IViewHolderOnClick mListener;

        public ViewHolder(View v, IViewHolderOnClick listener) {
            super(v);
            iv_icon = (ImageView)v.findViewById(R.id.contacts_icon);
            tv_name = (TextView)v.findViewById(R.id.contacts_name);
            tv_phoneNumber = (TextView)v.findViewById(R.id.contacts_phoneNumber);
            mListener = listener;

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

    public ContactsAdapter(ArrayList<Student> data, Context context) {
        this.mDataset = data;
        this.mContext = context;
        mSelectedPositions = new SparseBooleanArray();
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.contacts_info, parent, false);
        return new ViewHolder(v, new ViewHolder.IViewHolderOnClick() {
            @Override
            public void onClick(View v, int position) {
                setChecked(position, !isChecked(position));
                notifyItemChanged(position);
            }
        });
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Student s = mDataset.get(position);
        holder.tv_name.setText(s.getName());
        holder.tv_phoneNumber.setText(s.getPhone());
        if(isChecked(position)) {
            holder.itemView.setBackgroundColor(mContext.getResources().getColor(R.color.selected_background));
            holder.iv_icon.setImageResource(R.drawable.ic_checked);
        }
        else {
            holder.itemView.setBackgroundColor(mContext.getResources().getColor(R.color.default_background));
            holder.iv_icon.setImageResource(s.isBoy()? R.drawable.ic_gender_boy : R.drawable.ic_gender_girl);
        }
    }

    public void setChecked(int position, boolean checked) {
        mSelectedPositions.put(position, checked);
    }

    public boolean isChecked(int position) {
        return mSelectedPositions.get(position);
    }

    public void setAllChecked(boolean checked) {
        for(int i = 0; i < mDataset.size(); i++) {
            setChecked(i, checked);
        }
        notifyDataSetChanged();
    }

    public ArrayList<Student> getSelectedStudents() {
        ArrayList<Student> selected = new ArrayList<>();
        for(int i = 0; i < getItemCount(); i++) {
            if(mSelectedPositions.get(i)) {
                selected.add(mDataset.get(i));
            }
        }
        return selected;
    }
}
