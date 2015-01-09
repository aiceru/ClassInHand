package com.iceru.classinhand;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.TreeMap;

/**
 * Created by iceru on 14. 11. 13..
 */
public class SeatplansFragment extends Fragment{
    private ClassInHandApplication      application;
    private MainActivity                mainActivity;

    private ArrayList<Seatplan> mSeatplans;

    private RecyclerView                mSeatplanRecyclerView;
    private RecyclerView.LayoutManager  mSeatplanLayoutManager;
    private SeatplansAdapter            mSeatplansAdapter;

    public static SeatplansFragment newInstance() {
        SeatplansFragment fragment = new SeatplansFragment();
        return fragment;
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
        mSeatplanRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(mainActivity, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Intent intent = new Intent(mainActivity, DetailedSeatplanActivity.class);
                        intent.putExtra(MainActivity.SEATPLAN_SELECTED_POSITION, position);
                        startActivity(intent);
                    }
                })
        );

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mSeatplansAdapter.notifyDataSetChanged();
    }
}
