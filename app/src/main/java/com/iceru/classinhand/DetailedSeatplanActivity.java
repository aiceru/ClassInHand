package com.iceru.classinhand;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by iceru on 14. 12. 19..
 */
public class DetailedSeatplanActivity extends ActionBarActivity {
    /* Application Class */
    private ClassInHandApplication application;

    /* Data Structures */
    private TreeMap<Integer, Student> mStudents;
    private Seatplan mSeatplan;
    private SeatGridAdapter mSeatGridAdapter;

    /* Views */
    private GridView gv_seats;

    private GregorianCalendar mDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        application = ClassInHandApplication.getInstance();

        Intent intent = getIntent();
        int position = intent.getIntExtra(MainActivity.SEATPLAN_SELECTED_POSITION, -1);

        if (position < 0) finish();

        mStudents = application.getmStudents();
        mSeatplan = application.getmSeatplans().get(position);
        mDate = mSeatplan.getmApplyDate();

        setContentView(R.layout.activity_detailed_seatplan);
        initViews();
    }

    private void initViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.detailedseatplanactivity_toolbar);

        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        String dateString =
                String.valueOf(mDate.get(Calendar.YEAR)) + ". " +
                        String.valueOf(mDate.get(Calendar.MONTH) + 1) + ". " +
                        String.valueOf(mDate.get(Calendar.DAY_OF_MONTH)) + " ~";
        getSupportActionBar().setTitle(dateString);

        gv_seats = (GridView) findViewById(R.id.gridview_detailedseatplan);
        mSeatGridAdapter = new SeatGridAdapter(mSeatplan.getmSeats(), this);
        gv_seats.setAdapter(mSeatGridAdapter);
    }
}