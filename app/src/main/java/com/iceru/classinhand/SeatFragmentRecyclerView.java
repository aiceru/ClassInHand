package com.iceru.classinhand;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by iceru on 14. 11. 13..
 */
public class SeatFragmentRecyclerView extends Fragment{
    private MainActivity                mainActivity;
    private RecyclerView                mRecyclerView;
    private RecyclerView.LayoutManager  mLayoutManager;

    public static SeatFragmentRecyclerView newInstance() {
        SeatFragmentRecyclerView fragment = new SeatFragmentRecyclerView();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity)this.getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_seat_recyclerview, container, false);
        mRecyclerView = (RecyclerView)rootView.findViewById(R.id.recyclerview_seatplan);
        mLayoutManager = new LinearLayoutManager(mainActivity);
        mRecyclerView.setLayoutManager(mLayoutManager);
        return rootView;
    }
}
