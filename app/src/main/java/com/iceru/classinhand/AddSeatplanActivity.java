package com.iceru.classinhand;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
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
    private TreeMap<Integer, Student>       mRemainStudents;
    private Seatplan                        mNewPlan;
    private SeatGridAdapter                 mSeatGridAdapter;

    private Seat                            mLeftSelectedSeat, mRightSelectedSeat;

    /* Views */
    private GridView                        gv_seats;
    private LinearLayout                    layout_onseatclick_inflated;
    private ListView                        mLeftDrawerListView, mRightDrawerListView;
    private TreeMapListViewAdapter          mRemainStudentListAdapter;
    private DrawerLayout                    mDrawerLayout;
    private ActionBarDrawerToggle           mDrawerToggle;

    private GregorianCalendar               mNewDate;

    class TreeMapListViewAdapter extends BaseAdapter {
        private TreeMap<Integer, Student> mDataset;
        private Collection<Student> mDataCollection;
        private LayoutInflater inflater;

        public TreeMapListViewAdapter(Context context, TreeMap<Integer, Student> dataset) {
            this.mDataset = dataset;
            inflater = LayoutInflater.from(context);
            mDataCollection = mDataset.values();
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
                v = inflater.inflate(R.layout.student_info, parent, false);
            }

            Student s = (Student)getItem(position);

            ImageView iv = (ImageView)v.findViewById(R.id.imageview_gender);
            TextView tv = (TextView)v.findViewById(R.id.textview_name);

            iv.setImageResource(
                    s.isBoy() ? R.drawable.ic_gender_boy : R.drawable.ic_gender_girl);
            tv.setText(s.getAttendNum() + s.getName());

            return v;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        application = ClassInHandApplication.getInstance();
        mStudents = application.getmStudents();
        mRemainStudents = new TreeMap<>(mStudents);

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
        mDrawerLayout = (DrawerLayout)findViewById(R.id.addseatplanactivity_drawerlayout);
        mLeftDrawerListView = (ListView)findViewById(R.id.addseatplanactivity_left_drawer);
        mRightDrawerListView = (ListView)findViewById(R.id.addseatplanactivity_right_drawer);

        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        int width = getResources().getDisplayMetrics().widthPixels/2;
        DrawerLayout.LayoutParams params;
        params = (android.support.v4.widget.DrawerLayout.LayoutParams) mLeftDrawerListView.getLayoutParams();
        params.width = width;
        mLeftDrawerListView.setLayoutParams(params);
        params = (android.support.v4.widget.DrawerLayout.LayoutParams) mRightDrawerListView.getLayoutParams();
        params.width = width;
        mRightDrawerListView.setLayoutParams(params);

        mRemainStudentListAdapter = new TreeMapListViewAdapter(this, mRemainStudents);
        mLeftDrawerListView.setAdapter(mRemainStudentListAdapter);
        mRightDrawerListView.setAdapter(mRemainStudentListAdapter);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, null, R.string.drawer_open, R.string.drawer_close);
        mDrawerLayout.setDrawerListener(mDrawerToggle);

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
            Student s = e.getValue();
            seat.setItsStudent(s);
            mRemainStudents.remove(s.getAttendNum());
            pointedTreeMap.remove(e.getKey());
        }

        mSeatGridAdapter.notifyDataSetChanged();
        mRemainStudentListAdapter.notifyDataSetChanged();
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

        /* 자리교환 버튼, 왼쪽/오른쪽 선택된 자리가 있을 경우 (null이 아닐 때)에만 보임 */
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

                mLeftSelectedSeat = null;
                mRightSelectedSeat = null;

                layout_onseatclick_left.removeAllViews();
                layout_onseatclick_right.removeAllViews();
                layout_onseatclick_inflated.setVisibility(View.GONE);
                mSeatGridAdapter.notifyDataSetChanged();
            }
        });
        /* 왼쪽 선택된 자리 선택취소 버튼 */
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
        /* 오른쪽 선택된 자리 선택취소 버튼 */
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

        /* 선택된 seat 가 없거나, 오른쪽 자리만 선택되어 있는 상태 */
        if(mLeftSelectedSeat == null) {
            mLeftSelectedSeat = mNewPlan.getmSeats().get(position);
            selectedStudent = mLeftSelectedSeat.getItsStudent();
            /* 선택한 자리가 빈자리일 경우 */
            if(selectedStudent == null) {

            }
            /* 선택한 자리에 배정된 학생이 있음, 학생의 과거 자리/짝 정보 표시 */
            else {
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
                        Student pairStudent = application.findStudent(dbHelper.getSeatedStudent(where % 2 == 0 ? where + 1 : where - 1, when));
                        String pairStr = pairStudent == null ? "" : pairStudent.getName();
                        tv = new TextView(this);
                        tv.setText(whenStr + whereStr + pairStr);
                        tv.setTextSize(14);
                        layout_onseatclick_left.addView(tv);
                        historyCursor.moveToNext();
                        historyCount++;
                    }
                    for (Seat seat : selectedStudent.getItsPastSeats()) {
                        seat.setRecentSeatedLev(ClassInHandApplication.SEATED_LEFT);
                    }
                }
                btn_left_cancel.setVisibility(View.VISIBLE);
            }
        }
        /* 왼쪽 자리만 선택되어 있는 상태 */
        else if(mRightSelectedSeat == null) {
            mRightSelectedSeat = mNewPlan.getmSeats().get(position);
            selectedStudent = mRightSelectedSeat.getItsStudent();
            /* 선택한 자리가 빈자리일 경우 */
            if(selectedStudent == null) {

            }
            /* 선택한 자리에 배정된 학생이 있음, 학생의 과거 자리/짝 정보 표시 */
            else {
                tv = new TextView(this);
                tv.setText(selectedStudent.getName());
                tv.setTextSize(18);
                layout_onseatclick_right.addView(tv);
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
                        Student pairStudent = application.findStudent(dbHelper.getSeatedStudent(where % 2 == 0 ? where + 1 : where - 1, when));
                        String pairStr = pairStudent == null ? "" : pairStudent.getName();
                        tv = new TextView(this);
                        tv.setText(whenStr + whereStr + pairStr);
                        tv.setTextSize(14);
                        layout_onseatclick_right.addView(tv);
                        historyCursor.moveToNext();
                        historyCount++;
                    }
                    for (Seat seat : selectedStudent.getItsPastSeats()) {
                        seat.setRecentSeatedLev(ClassInHandApplication.SEATED_RIGHT);
                    }
                }
                btn_right_cancel.setVisibility(View.VISIBLE);
            }
        }

        /* 자리가 두개 선택되어 있으면, 자리교환 버튼을 표시 */
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
