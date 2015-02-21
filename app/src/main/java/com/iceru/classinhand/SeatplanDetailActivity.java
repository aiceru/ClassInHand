package com.iceru.classinhand;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.GridView;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TreeMap;

/**
 * Created by iceru on 14. 12. 19..
 */
public class SeatplanDetailActivity extends ActionBarActivity {

    public static final String SEATPLAN_SELECTED_POSITION = "com.iceru.classinhand.SEATPLAN_SELECTED_POSITION";
    /* Application Class */
    private ClassInHandApplication application;

    /* Data Structures */
    private TreeMap<Integer, Student> mStudents;
    private Seatplan mSeatplan;
    private GregorianCalendar mDate;
    private SeatGridAdapter mSeatGridAdapter;

    /* Views */
    private GridView gv_seats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        application = ClassInHandApplication.getInstance();

        Intent intent = getIntent();
        int position = intent.getIntExtra(SEATPLAN_SELECTED_POSITION, -1);

        if (position < 0) finish();

        mStudents = application.getmStudents();
        mSeatplan = application.getmSeatplans().get(position);
        mDate = mSeatplan.getmApplyDate();

        setContentView(R.layout.activity_seatplan_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_seatplan_detail);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setHomeButtonEnabled(true);

        String dateString =
                String.valueOf(mDate.get(Calendar.YEAR)) + ". " +
                        String.valueOf(mDate.get(Calendar.MONTH) + 1) + ". " +
                        String.valueOf(mDate.get(Calendar.DAY_OF_MONTH)) + " ~";
        getSupportActionBar().setTitle(dateString);

        gv_seats = (GridView) findViewById(R.id.gridview_seatplan_detail);
        mSeatGridAdapter = new SeatGridAdapter(mSeatplan.getmSeats(), this);
        gv_seats.setAdapter(mSeatGridAdapter);
    }

    public void openModifySeatplanActivity(View view) {
        final GregorianCalendar newDate = new GregorianCalendar();
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                newDate.clear();
                newDate.set(year, monthOfYear, dayOfMonth);

            }
        };
        new DatePickerDialog(this, dateSetListener, mDate.get(Calendar.YEAR), mDate.get(Calendar.MONTH),
                mDate.get(Calendar.DAY_OF_MONTH)).show();

        Intent intent = new Intent(this, AddSeatplanActivity.class);
        startActivity(intent);
    }
}
