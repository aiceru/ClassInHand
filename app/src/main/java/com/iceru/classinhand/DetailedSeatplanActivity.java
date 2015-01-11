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
    private ClassInHandApplication          application;

    /* Data Structures */
    private TreeMap<Integer, Student>       mStudents;
    private Seatplan                        mSeatplan;
    private SeatGridAdapter                 mSeatGridAdapter;

    /* Views */
    private GridView                        gv_seats;

    private GregorianCalendar               mDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        application = ClassInHandApplication.getInstance();

        Intent intent = getIntent();
        int position = intent.getIntExtra(MainActivity.SEATPLAN_SELECTED_POSITION, -1);

        if(position < 0) finish();

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

        gv_seats = (GridView)findViewById(R.id.gridview_detailedseatplan);
        mSeatGridAdapter = new SeatGridAdapter(mSeatplan.getmSeats(), this);
        gv_seats.setAdapter(mSeatGridAdapter);
    }

    /*public void assignRandom(View view) {
        ArrayList<Seat> seatArray = mNewPlan.getmSeats();
        TreeMap<Double, Student> pointedTreeMap = new TreeMap<>();

        for(Map.Entry<Integer, Student> entry : mStudents.entrySet()) {
            Student s = entry.getValue();
            pointedTreeMap.put(Math.random(), s);
        }

        for(Seat seat : seatArray) {
            Map.Entry<Double, Student> e = pointedTreeMap.firstEntry();
            seat.setItsStudent(e.getValue());
            pointedTreeMap.remove(e.getKey());
        }

        mSeatGridAdapter.notifyDataSetChanged();
    }*/

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_person, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_done) {
            GregorianCalendar today = new GregorianCalendar();
            DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    //if(view.isShown()) {
                        mNewDate.clear();
                        mNewDate.set(year, monthOfYear, dayOfMonth);
                        mNewPlan.setmApplyDate(mNewDate);
                        if (dateAlreadyExist(mNewDate)) {
                            askOverwriteOrNot();
                        } else {
                            application.addSeatplan(mNewPlan);
                            finish();
                        }
                    //}
                }
            };
            new DatePickerDialog(this, dateSetListener, today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH)).show();
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean dateAlreadyExist(GregorianCalendar cal) {
        for(Seatplan plan : application.getmSeatplans()) {
            if(plan.getmApplyDate().equals(cal)) return true;
        }
        return false;
    }

    private void askOverwriteOrNot() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.title_dialog_warning);
        builder.setMessage(R.string.contents_dialog_seat_assign_already_exists);
        builder.setPositiveButton(R.string.action_overwrite, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                application.removeSeatplan(mNewDate);
                application.addSeatplan(mNewPlan);
                finish();
            }
        });
        builder.setNegativeButton(R.string.action_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog, Do nothing
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }*/
}