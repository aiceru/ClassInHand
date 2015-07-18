package com.iceru.classinhand;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Collection;
import java.util.TreeMap;

/**
 * Created by iceru on 15. 1. 12..
 */
class StudentGridAdapter extends BaseAdapter {
    private TreeMap<Integer, Student> mDataset;
    private Collection<Student> mDataCollection;
    private LayoutInflater inflater;
    private View.OnLongClickListener mItemLongClickListener;

    public StudentGridAdapter(Context context, TreeMap<Integer, Student> dataset) {
        this.mDataset = dataset;
        inflater = LayoutInflater.from(context);
        mDataCollection = mDataset.values();

        mItemLongClickListener = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                String[] descriptions = {
                        ClipDescription.MIMETYPE_TEXT_PLAIN
                };
                ClipData.Item item = new ClipData.Item(String.valueOf((int)v.getTag()));

                ClipData clipData = new ClipData(Constants.DRAGLABEL_FROM_STUDENTGRID, descriptions, item);

                View.DragShadowBuilder shadow = new View.DragShadowBuilder(v);
                v.startDrag(clipData, shadow, null, 0);
                return true;
            }
        };
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getCount() {
        return mDataset.size();
    }

    @Override
    public Object getItem(int position) {
        return mDataCollection.toArray()[position];
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if(v == null) {
            v = inflater.inflate(R.layout.student_info_grid, parent, false);
        }

        Student s = (Student)getItem(position);

        ImageView iv = (ImageView)v.findViewById(R.id.imageview_gender);
        TextView tv_num = (TextView)v.findViewById(R.id.textview_attendnum);
        TextView tv = (TextView)v.findViewById(R.id.textview_name);

        iv.setImageResource(
                s.isBoy() ? R.drawable.ic_gender_boy : R.drawable.ic_gender_girl);
        tv_num.setText(Integer.toString(s.getAttendNum()));
        tv.setText(s.getName());

        v.setTag(s.getAttendNum());
        v.setOnLongClickListener(mItemLongClickListener);

        return v;
    }
}
