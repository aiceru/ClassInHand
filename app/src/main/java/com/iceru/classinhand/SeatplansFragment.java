package com.iceru.classinhand;

import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by iceru on 14. 11. 13..
 */
public class SeatplansFragment extends Fragment{
    private static SeatplansFragment    thisObject = null;
    private ClassInHandApplication      application;
    private MainActivity                mainActivity;

    private ArrayList<Seatplan> mSeatplans;

    private RecyclerView                mSeatplanRecyclerView;
    private RecyclerView.LayoutManager  mSeatplanLayoutManager;
    private SeatplansAdapter            mSeatplansAdapter;

    public static SeatplansFragment getInstance() {
        if(thisObject == null) thisObject = new SeatplansFragment();
        return thisObject;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity)this.getActivity();
        application = ClassInHandApplication.getInstance();
        mSeatplans = application.getmSeatplans();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_seatplans, container, false);
        mSeatplanRecyclerView = (RecyclerView)rootView.findViewById(R.id.recyclerview_seatplans);
        mSeatplanLayoutManager = new LinearLayoutManager(mainActivity);
        mSeatplanRecyclerView.setLayoutManager(mSeatplanLayoutManager);
        mSeatplansAdapter = new SeatplansAdapter(mSeatplans, mainActivity);
        mSeatplanRecyclerView.setAdapter(mSeatplansAdapter);

        TextView tv_no_plans = (TextView)rootView.findViewById(R.id.textview_welcome_create_seatplan);
        TextView tv_no_students = (TextView)rootView.findViewById(R.id.textview_welcome_no_student);

        if(application.getmStudents().size() <= 0) {
            tv_no_students.setVisibility(View.VISIBLE);
            tv_no_plans.setVisibility(View.GONE);
        }
        else if(mSeatplans.size() <= 0) {
            tv_no_students.setVisibility(View.GONE);
            tv_no_plans.setVisibility(View.VISIBLE);
        }
        else {
            tv_no_plans.setVisibility(View.GONE);
            tv_no_students.setVisibility(View.GONE);
        }

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mSeatplansAdapter.notifyDataSetChanged();
    }
}
