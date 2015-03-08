package com.iceru.classinhand;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
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
    private GregorianCalendar mDate, mNewDate;
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
        gv_seats.setNumColumns(mSeatplan.getmColumns());
        mSeatGridAdapter = new SeatGridAdapter(mSeatplan.getmSeats(), this, mSeatplan.getmColumns());
        gv_seats.setAdapter(mSeatGridAdapter);
    }

    public void onClickEditButton(View view) {
        /* Ask if changes ApplyDate or not */
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.contents_dialog_if_change_applydate);
        builder.setPositiveButton(R.string.edit_date_either, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // YES, display datepicker to get new ApplyDate
                displayDatepicker();
            }
        });
        builder.setNegativeButton(R.string.edit_location_only, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // No, set mNewDate to mDate (same)
                mNewDate = mDate;
                runSeatplanActivity();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void displayDatepicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // TODO : 선택한 날짜의 중복처리!!
                mNewDate = new GregorianCalendar();
                mNewDate.clear();
                mNewDate.set(year, monthOfYear, dayOfMonth);
                if(mNewDate.compareTo(mDate) != 0 && application.findSeatplan(mNewDate) != null) {
                    confirmEditExistingPlan();
                }
                else {
                    runSeatplanActivity();
                }
            }
        };
        DatePickerDialog dateDialog = new DatePickerDialog(this,
                dateSetListener, mDate.get(Calendar.YEAR), mDate.get(Calendar.MONTH),
                mDate.get(Calendar.DAY_OF_MONTH));
        dateDialog.show();
    }

    private void confirmEditExistingPlan() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String dateStr = String.valueOf(mNewDate.get(Calendar.YEAR)) + "년 " +
                String.valueOf(mNewDate.get(Calendar.MONTH)+1) + "월 " +
                String.valueOf(mNewDate.get(Calendar.DAY_OF_MONTH)) + "일" +
                getString(R.string.contents_dialog_if_edit_existing_plan);
        builder.setMessage(dateStr);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                mDate = mNewDate;
                runSeatplanActivity();
            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void runSeatplanActivity() {
        long newDateLong = mNewDate.getTimeInMillis();
        long oldDateLong = mDate == null? 0 : mDate.getTimeInMillis();

        Intent intent = new Intent(this, SeatplanEditActivity.class);
        intent.putExtra(ClassInHandApplication.SEATPLAN_EDIT_NEWDATE, newDateLong);
        intent.putExtra(ClassInHandApplication.SEATPLAN_EDIT_OLDDATE, oldDateLong);
        startActivity(intent);

        finish();
    }
}
