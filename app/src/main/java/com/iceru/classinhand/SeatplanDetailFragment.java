package com.iceru.classinhand;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TreeMap;

/**
 * Created by iceru on 14. 12. 19..
 */
public class SeatplanDetailFragment extends Fragment {

    public static final String SEATPLAN_SELECTED_POSITION = "com.iceru.classinhand.SEATPLAN_SELECTED_POSITION";
    /* Application Class */
    private ClassInHandApplication application;
    private MainActivity            mainActivity;

    /* Data Structures */
    private TreeMap<Integer, Student> mStudents;
    private Seatplan mSeatplan;
    private SeatGridAdapter mSeatGridAdapter;

    /* Views */
    private GridView gv_seats;

    private GregorianCalendar mDate;

    public static SeatplanDetailFragment newInstance(int position) {
        SeatplanDetailFragment fragment = new SeatplanDetailFragment();

        Bundle args = new Bundle();
        args.putInt(SEATPLAN_SELECTED_POSITION, position);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        application = ClassInHandApplication.getInstance();
        mainActivity = (MainActivity)getActivity();

        int position = getArguments().getInt(SEATPLAN_SELECTED_POSITION, -1);
        /*Intent intent = getIntent();
        int position = intent.getIntExtra(MainActivity.SEATPLAN_SELECTED_POSITION, -1);

        if (position < 0) finish();*/

        mStudents = application.getmStudents();
        mSeatplan = application.getmSeatplans().get(position);
        mDate = mSeatplan.getmApplyDate();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_seatplan_detail, container, false);

        String dateString =
                String.valueOf(mDate.get(Calendar.YEAR)) + ". " +
                        String.valueOf(mDate.get(Calendar.MONTH) + 1) + ". " +
                        String.valueOf(mDate.get(Calendar.DAY_OF_MONTH)) + " ~";
        mainActivity.getSupportActionBar().setTitle(dateString);

        gv_seats = (GridView) rootView.findViewById(R.id.gridview_seatplan_detail);
        mSeatGridAdapter = new SeatGridAdapter(mSeatplan.getmSeats(), mainActivity);
        gv_seats.setAdapter(mSeatGridAdapter);

        return rootView;
    }
}
