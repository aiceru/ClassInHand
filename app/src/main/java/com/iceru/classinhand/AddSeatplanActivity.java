package com.iceru.classinhand;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by iceru on 14. 12. 19..
 */
public class AddSeatplanActivity extends ActionBarActivity {
    /* Application Class */
    private ClassInHandApplication          application;

    /* Data Structures */
    private TreeMap<Integer, Student>       mStudents;
    private Seatplan                        mNewPlan;
    private SeatGridAdapter                 mSeatGridAdapter;

    private Seat                            mLeftSelectedSeat, mRightSelectedSeat;

    /* Views */
    private GridView                        gv_seats;
    private LinearLayout                    layout_onseatclick_inflated;

    private GregorianCalendar               mNewDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        application = ClassInHandApplication.getInstance();
        mStudents = application.getmStudents();

        mNewDate = new GregorianCalendar();
        mNewDate.clear(Calendar.HOUR);
        mNewDate.clear(Calendar.MINUTE);
        mNewDate.clear(Calendar.SECOND);
        mNewDate.clear(Calendar.MILLISECOND);

        mNewPlan = new Seatplan(mNewDate, new ArrayList<Seat>());
        for(int i = 0; i < mStudents.size(); i++) {
            Seat s = new Seat(i);
            mNewPlan.getmSeats().add(s);
        }

        setContentView(R.layout.activity_add_seatplan);
        initViews();
    }

    private void initViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.addseatplanactivity_toolbar);

        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        gv_seats = (GridView)findViewById(R.id.gridview_newseatplan);
        layout_onseatclick_inflated = (LinearLayout)findViewById(R.id.linearlayout_onseatclick_inflated);

        mSeatGridAdapter = new SeatGridAdapter(mNewPlan.getmSeats(), this);
        gv_seats.setAdapter(mSeatGridAdapter);
        gv_seats.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), String.valueOf(position), Toast.LENGTH_SHORT).show();
                onSeatClick(position);
            }
        });
    }

    public void assignRandom(View view) {
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
    }

    @Override
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
    }

    private void onSeatClick(int position) {
        final LayoutInflater inflater =  (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layout_onseatclick_inflated.setVisibility(View.VISIBLE);
        inflater.inflate(R.layout.onseatclick_inflated, layout_onseatclick_inflated);

        final LinearLayout layout_onseatclick_left =
                (LinearLayout)layout_onseatclick_inflated.findViewById(R.id.linearlayout_onseatclick_left);
        final LinearLayout layout_onseatclick_right =
                (LinearLayout)layout_onseatclick_inflated.findViewById(R.id.linearlayout_onseatclick_right);

        ClassDBHelper dbHelper = ClassInHandApplication.getInstance().getDbHelper();

        int where;
        long when;
        GregorianCalendar cal = new GregorianCalendar();
        Cursor historyCursor;
        Student selectedStudent;
        TextView tv;

        final Button btn_change_seat = (Button)layout_onseatclick_inflated.findViewById(R.id.btn_change_seat);
        btn_change_seat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(Seat seat : mLeftSelectedSeat.getItsStudent().getItsPastSeats()) {
                    seat.setRecentSeatedLev(ClassInHandApplication.SEATED_NOT);
                }
                for(Seat seat : mRightSelectedSeat.getItsStudent().getItsPastSeats()) {
                    seat.setRecentSeatedLev(ClassInHandApplication.SEATED_NOT);
                }
                mLeftSelectedSeat.getItsStudent().getItsPastSeats().clear();
                mRightSelectedSeat.getItsStudent().getItsPastSeats().clear();

                Student tempStd = mLeftSelectedSeat.getItsStudent();
                mLeftSelectedSeat.setItsStudent(mRightSelectedSeat.getItsStudent());
                mRightSelectedSeat.setItsStudent(tempStd);

                //ClassDBHelper dbHelper = mainActivity.getDbHelper();
                //Seat pairSeat = getSeatByAbsolutePosition(mLeftSelectedSeat.getPairSeatId());
                /*dbHelper.update(mLeftSelectedSeat,
                        pairSeat == null? -1 : pairSeat.getItsStudent().getNum(),
                        mCurrentShowingDate);
                pairSeat = getSeatByAbsolutePosition(mRightSelectedSeat.getPairSeatId());
                dbHelper.update(mRightSelectedSeat,
                        pairSeat == null? -1 : pairSeat.getItsStudent().getNum(),
                        mCurrentShowingDate);
                */

                mLeftSelectedSeat = null;
                mRightSelectedSeat = null;

                layout_onseatclick_left.removeAllViews();
                layout_onseatclick_right.removeAllViews();
                layout_onseatclick_inflated.setVisibility(View.GONE);
                mSeatGridAdapter.notifyDataSetChanged();
            }
        });
        final Button btn_left_cancel = (Button)layout_onseatclick_inflated.findViewById(R.id.btn_left_cancel);
        btn_left_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(Seat seat : mLeftSelectedSeat.getItsStudent().getItsPastSeats()) {
                    seat.setRecentSeatedLev(ClassInHandApplication.SEATED_NOT);
                }
                mLeftSelectedSeat.getItsStudent().getItsPastSeats().clear();
                mLeftSelectedSeat = null;
                layout_onseatclick_left.removeAllViews();
                btn_left_cancel.setVisibility(View.INVISIBLE);
                btn_change_seat.setVisibility(View.INVISIBLE);
                if(mRightSelectedSeat == null) layout_onseatclick_inflated.setVisibility(View.GONE);
                mSeatGridAdapter.notifyDataSetChanged();
            }
        });
        final Button btn_right_cancel = (Button)layout_onseatclick_inflated.findViewById(R.id.btn_right_cancel);
        btn_right_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(Seat seat : mRightSelectedSeat.getItsStudent().getItsPastSeats()) {
                        seat.setRecentSeatedLev(ClassInHandApplication.SEATED_NOT);
                }
                mRightSelectedSeat.getItsStudent().getItsPastSeats().clear();
                mRightSelectedSeat = null;
                layout_onseatclick_right.removeAllViews();
                btn_right_cancel.setVisibility(View.INVISIBLE);
                btn_change_seat.setVisibility(View.INVISIBLE);
                if(mLeftSelectedSeat == null) layout_onseatclick_inflated.setVisibility(View.GONE);
                mSeatGridAdapter.notifyDataSetChanged();
            }
        });

        if(mLeftSelectedSeat == null) {
            mLeftSelectedSeat = mNewPlan.getmSeats().get(position);
            selectedStudent = mLeftSelectedSeat.getItsStudent();
            tv = new TextView(this);
            tv.setText(selectedStudent.getName());
            tv.setTextSize(18);
            layout_onseatclick_left.addView(tv);
            historyCursor = dbHelper.getHistory(selectedStudent.getId());
            if (historyCursor.moveToFirst()) {
                int historyCount = 0;
                while (!historyCursor.isAfterLast() && historyCount < 3) {
                    where = historyCursor.getInt(historyCursor.getColumnIndexOrThrow(ClassDBContract.SeatHistory.COLUMN_NAME_ID));
                    selectedStudent.getItsPastSeats().add(mNewPlan.getmSeats().get(where));
                    when = historyCursor.getLong(historyCursor.getColumnIndexOrThrow(ClassDBContract.SeatHistory.COLUMN_NAME_APPLY_DATE));
                    cal.setTimeInMillis(when);
                    String whenStr = String.format("%02d.%02d ~\n", cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH));
                    String whereStr = ConvertAbsSeatToSegAndRow(where) + ", ";
                    Student pairStudent = application.findStudent(dbHelper.getSeatedStudent(where % 2 == 0? where+1 : where-1, when));
                    String pairStr = pairStudent == null? null : pairStudent.getName();
                    tv = new TextView(this);
                    tv.setText(whenStr + whereStr + pairStr);
                    tv.setTextSize(14);
                    layout_onseatclick_left.addView(tv);
                    historyCursor.moveToNext();
                    historyCount++;
                }
                for(Seat seat : selectedStudent.getItsPastSeats()) {
                    seat.setRecentSeatedLev(ClassInHandApplication.SEATED_LEFT);
                }
            }
            btn_left_cancel.setVisibility(View.VISIBLE);
        }
        else if(mRightSelectedSeat == null) {
            mRightSelectedSeat = mNewPlan.getmSeats().get(position);
            selectedStudent = mRightSelectedSeat.getItsStudent();
            tv = new TextView(this);
            tv.setText(selectedStudent.getName());
            tv.setTextSize(18);
            layout_onseatclick_right.addView(tv);
            historyCursor = dbHelper.getHistory(selectedStudent.getId());
            if(historyCursor.moveToFirst()) {
                int historyCount = 0;
                while(!historyCursor.isAfterLast() && historyCount < 3) {
                    where = historyCursor.getInt(historyCursor.getColumnIndexOrThrow(ClassDBContract.SeatHistory.COLUMN_NAME_ID));
                    selectedStudent.getItsPastSeats().add(mNewPlan.getmSeats().get(where));
                    when = historyCursor.getLong(historyCursor.getColumnIndexOrThrow(ClassDBContract.SeatHistory.COLUMN_NAME_APPLY_DATE));
                    cal.setTimeInMillis(when);
                    String whenStr = String.format("%02d.%02d ~\n", cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH));
                    String whereStr = ConvertAbsSeatToSegAndRow(where) + ", ";
                    Student pairStudent = application.findStudent(dbHelper.getSeatedStudent(where % 2 == 0? where+1 : where-1, when));
                    String pairStr = pairStudent == null? null : pairStudent.getName();
                    tv = new TextView(this);
                    tv.setText(whenStr + whereStr + pairStr);
                    tv.setTextSize(14);
                    layout_onseatclick_right.addView(tv);
                    historyCursor.moveToNext();
                    historyCount++;
                }
                for(Seat seat : selectedStudent.getItsPastSeats()) {
                    seat.setRecentSeatedLev(ClassInHandApplication.SEATED_RIGHT);
                }
            }
            btn_right_cancel.setVisibility(View.VISIBLE);
        }

        if(mLeftSelectedSeat != null && mRightSelectedSeat != null) {
            btn_change_seat.setVisibility(View.VISIBLE);
        }
        mSeatGridAdapter.notifyDataSetChanged();
    }

    private String ConvertAbsSeatToSegAndRow(int seatId) {
        int row = seatId / 6 + 1;
        int seg = ((seatId % 6) / 2) + 1;
        String segAndRow =
                String.valueOf(seg) +
                        getString(R.string.string_segment) + " " +
                        String.valueOf(row) +
                        getString(R.string.string_row) + " ";
        if(seatId % 2 == 0) segAndRow += getString(R.string.string_left);
        else segAndRow += getString(R.string.string_right);
        return segAndRow;
    }
}
