package com.iceru.classinhand;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.shamanland.fab.FloatingActionButton;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by iceru on 14. 12. 19..
 */
public class SeatplanEditActivity extends ActionBarActivity {
    /* Application Class */
    private ClassInHandApplication          application;

    /* Data Structures */
    private TreeMap<Integer, Student>       mStudents;
    private TreeMap<Integer, Student>       mRemainStudents;
    private Seatplan                        mNewPlan, mOldPlan;
    private GregorianCalendar               mNewDate, mOldDate;
    private Seat                            mLeftSelectedSeat, mRightSelectedSeat;

    /* Views */
    private GridView                        gv_seats;
    private SeatGridAdapter                 mSeatGridAdapter;

    private LinearLayout                    layout_onseatclick_inflated, layout_onseatclick_left, layout_onseatclick_right;
    private Button                          mLeftCancelButton, mRightCancelButton, mChangeSeatButton, mVacateSeatButton;
    private FloatingActionButton            mRandomAssignButton;

    private ListView                        mLeftDrawerListView, mRightDrawerListView;
    private TreeMapListViewAdapter          mRemainStudentListAdapter;

    private DrawerLayout                    mDrawerLayout;
    private ActionBarDrawerToggle           mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        application = ClassInHandApplication.getInstance();
        mStudents = application.getmStudents();
        mRemainStudents = new TreeMap<>(mStudents);

        Intent intent = getIntent();
        long newDatelong = intent.getLongExtra(ClassInHandApplication.SEATPLAN_EDIT_NEWDATE, 0);
        long oldDatelong = intent.getLongExtra(ClassInHandApplication.SEATPLAN_EDIT_OLDDATE, 0);

        if(newDatelong == 0) finish();
        mNewDate = new GregorianCalendar();
        mNewDate.setTimeInMillis(newDatelong);
        if(oldDatelong == 0) {
            mOldDate = null;
            mOldPlan = null;
        }
        else {
            mOldDate = new GregorianCalendar();
            mOldDate.setTimeInMillis(oldDatelong);
            mOldPlan = application.findSeatplan(mOldDate);
        }

        mNewPlan = new Seatplan(mNewDate, new ArrayList<Seat>());
        for (int i = 0; i < mStudents.size(); i++) {
            Seat s = new Seat(i);
            mNewPlan.getmSeats().add(s);
        }

        /* initialize Views */
        setContentView(R.layout.activity_seatplan);

        // TEMP!!!!
        TextView tv1 = (TextView)findViewById(R.id.tempStr1);
        TextView tv2 = (TextView)findViewById(R.id.tempStr2);
        tv1.setText(mNewDate.get(Calendar.YEAR) + "." + mNewDate.get(Calendar.MONTH) + "." + mNewDate.get(Calendar.DAY_OF_MONTH));
        if(mOldDate != null) {
            tv2.setText(mOldDate.get(Calendar.YEAR) + "." + mOldDate.get(Calendar.MONTH) + "." + mOldDate.get(Calendar.DAY_OF_MONTH));
        }
        else {
            tv2.setText("NULL");
        }
        // TEMP END

        Toolbar toolbar = (Toolbar) findViewById(R.id.seatplanactivity_toolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.seatplanactivity_drawerlayout);
        mLeftDrawerListView = (ListView) findViewById(R.id.seatplanactivity_left_drawer);
        mRightDrawerListView = (ListView) findViewById(R.id.seatplanactivity_right_drawer);

        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        int width = getResources().getDisplayMetrics().widthPixels / 2;
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

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, null, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (mLeftSelectedSeat != null) {
                    mLeftSelectedSeat.clrSelectedFlag(ClassInHandApplication.SEATED_LEFT);
                    mLeftSelectedSeat = null;
                }
                // drawer opens only when the selected (empty) seat is mLeftSelectedSeat

                mVacateSeatButton.setVisibility(View.GONE);

                layout_onseatclick_left.removeAllViews();
                //layout_onseatclick_right.removeAllViews();
                mLeftCancelButton.setVisibility(View.GONE);
                //mRightCancelButton.setVisibility(View.GONE);

                layout_onseatclick_inflated.setVisibility(View.GONE);
                mRandomAssignButton.setVisibility(View.VISIBLE);

                mSeatGridAdapter.notifyDataSetChanged();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                mVacateSeatButton.setVisibility(View.GONE);
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        AdapterView.OnItemClickListener remainStudentListClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mLeftSelectedSeat != null) {
                    Student candidate = (Student) mRemainStudentListAdapter.getItem(position);
                    mLeftSelectedSeat.setItsStudent(candidate);

                    mRemainStudents.remove(candidate.getAttendNum());
                    mLeftSelectedSeat.clrSelectedFlag(ClassInHandApplication.SEATED_LEFT);
                    layout_onseatclick_inflated.setVisibility(View.GONE);

                    mRemainStudentListAdapter.notifyDataSetChanged();
                    mSeatGridAdapter.notifyDataSetChanged();

                    mDrawerLayout.closeDrawer(Gravity.LEFT);
                    mDrawerLayout.closeDrawer(Gravity.RIGHT);
                }
            }
        };
        mLeftDrawerListView.setOnItemClickListener(remainStudentListClickListener);
        mRightDrawerListView.setOnItemClickListener(remainStudentListClickListener);

        gv_seats = (GridView) findViewById(R.id.gridview_newseatplan);
        layout_onseatclick_inflated = (LinearLayout) findViewById(R.id.linearlayout_onseatclick_inflated);

        mSeatGridAdapter = new SeatGridAdapter(mNewPlan.getmSeats(), this);
        gv_seats.setAdapter(mSeatGridAdapter);
        gv_seats.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onSeatClick(position);
            }
        });

        mRandomAssignButton = (FloatingActionButton) findViewById(R.id.btn_random_assign);
    }

    public void assignRandom(View view) {
        ArrayList<Seat> seatArray = mNewPlan.getmSeats();
        TreeMap<Double, Student> pointedTreeMap = new TreeMap<>();

        for(Map.Entry<Integer, Student> entry : mStudents.entrySet()) {
            Student s = entry.getValue();
            pointedTreeMap.put(Math.random(), s);
        }

        AllocateExecutor AE = new AllocateExecutor();
        AE.createRuleList();
        AE.allocateAllStudent(seatArray);
        /*
        for(Seat seat : seatArray) {
            Map.Entry<Double, Student> e = pointedTreeMap.firstEntry();
            Student s = e.getValue();
            seat.setItsStudent(s);
            mRemainStudents.remove(s.getAttendNum());
            pointedTreeMap.remove(e.getKey());
        }
        */
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
            //TODO: SAVE!!
        }
        return super.onOptionsItemSelected(item);
    }

    private void changeSeat() {
        Seat seat;
        if(mLeftSelectedSeat.getItsStudent() != null) {
            for (PersonalHistory p : mLeftSelectedSeat.getItsStudent().getHistories()) {
                seat = mNewPlan.getmSeats().get(p.seatId);
                seat.clrRecentSeatedFlag(ClassInHandApplication.SEATED_LEFT);
            }
        }
        if(mRightSelectedSeat.getItsStudent() != null) {
            for (PersonalHistory p : mRightSelectedSeat.getItsStudent().getHistories()) {
                seat = mNewPlan.getmSeats().get(p.seatId);
                seat.clrRecentSeatedFlag(ClassInHandApplication.SEATED_RIGHT);
            }
        }

        Student tempStd = mLeftSelectedSeat.getItsStudent();
        mLeftSelectedSeat.setItsStudent(mRightSelectedSeat.getItsStudent());
        mRightSelectedSeat.setItsStudent(tempStd);

        mLeftSelectedSeat.clrSelectedFlag(ClassInHandApplication.SEATED_LEFT);
        mLeftSelectedSeat = null;
        mRightSelectedSeat.clrSelectedFlag(ClassInHandApplication.SEATED_RIGHT);
        mRightSelectedSeat = null;

        mChangeSeatButton.setVisibility(View.GONE);
        layout_onseatclick_left.removeAllViews();
        layout_onseatclick_right.removeAllViews();
        mLeftCancelButton.setVisibility(View.GONE);
        mRightCancelButton.setVisibility(View.GONE);

        layout_onseatclick_inflated.setVisibility(View.GONE);

        mRandomAssignButton.setVisibility(View.VISIBLE);

        mSeatGridAdapter.notifyDataSetChanged();
    }

    private void vacateSeat() {
        Seat selectedSeat = mLeftSelectedSeat == null ? mRightSelectedSeat : mLeftSelectedSeat;
        Seat seat;

        Student victim = selectedSeat.getItsStudent();
        if(victim != null) {
            selectedSeat.setItsStudent(null);
            mRemainStudents.put(victim.getAttendNum(), victim);

            for (PersonalHistory p : victim.getHistories()) {
                seat = mNewPlan.getmSeats().get(p.seatId);
                seat.clrRecentSeatedFlag(ClassInHandApplication.SEATED_LEFT);
                seat.clrRecentSeatedFlag(ClassInHandApplication.SEATED_RIGHT);
            }
        }
        // Vacate 버튼이 Visible 한 경우는, 왼쪽 오른쪽 중 하나만 선택된 상황이고, 거기서 자리비움 버튼을 누르면
        // 왼쪽 오른쪽 양쪽을 모두 null 로 초기화해도 무방함. (어차피 다른 한쪽은 원래 null 이었으므로)
        if(mLeftSelectedSeat != null) {
            mLeftSelectedSeat.clrSelectedFlag(ClassInHandApplication.SEATED_LEFT);
        }
        if(mRightSelectedSeat != null) {
            mRightSelectedSeat.clrSelectedFlag(ClassInHandApplication.SEATED_RIGHT);
        }
        mLeftSelectedSeat = null;
        mRightSelectedSeat = null;

        mVacateSeatButton.setVisibility(View.GONE);
        layout_onseatclick_left.removeAllViews();
        layout_onseatclick_right.removeAllViews();
        mLeftCancelButton.setVisibility(View.GONE);
        mRightCancelButton.setVisibility(View.GONE);

        layout_onseatclick_inflated.setVisibility(View.GONE);

        mRandomAssignButton.setVisibility(View.VISIBLE);

        mSeatGridAdapter.notifyDataSetChanged();
        mRemainStudentListAdapter.notifyDataSetChanged();
    }

    private void onSeatClick(int position) {
        final LayoutInflater inflater =  (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layout_onseatclick_inflated.setVisibility(View.VISIBLE);
        inflater.inflate(R.layout.onseatclick_inflated, layout_onseatclick_inflated);

        layout_onseatclick_left =
                (LinearLayout)layout_onseatclick_inflated.findViewById(R.id.linearlayout_onseatclick_left);
        layout_onseatclick_right =
                (LinearLayout)layout_onseatclick_inflated.findViewById(R.id.linearlayout_onseatclick_right);

        ClassDBHelper dbHelper = ClassInHandApplication.getInstance().getDbHelper();

        final Student selectedStudent;
        TextView tv;

        /* First of all, disable RANDOM ASSIGN while selected seat exists */
        mRandomAssignButton.setVisibility(View.GONE);

        /* 자리교환 버튼, 왼쪽/오른쪽 선택된 자리가 있을 경우 (null이 아닐 때)에만 보임 */
        mChangeSeatButton = (Button)layout_onseatclick_inflated.findViewById(R.id.btn_change_seat);
        mChangeSeatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeSeat();
            }
        });

        /* 선택된 자리 비우기 버튼 (왼쪽 또는 오른쪽 한쪽만 선택되었을 때만 보임) */
        mVacateSeatButton = (Button)layout_onseatclick_inflated.findViewById(R.id.btn_vacate_seat);
        mVacateSeatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vacateSeat();
            }
        });

        /* 왼쪽 선택된 자리 선택취소 버튼 */
        mLeftCancelButton = (Button)layout_onseatclick_inflated.findViewById(R.id.btn_left_cancel);
        mLeftCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Seat seat;
                if(mLeftSelectedSeat.getItsStudent() != null) {
                    for (PersonalHistory p : mLeftSelectedSeat.getItsStudent().getHistories()) {
                        seat = mNewPlan.getmSeats().get(p.seatId);
                        seat.clrRecentSeatedFlag(ClassInHandApplication.SEATED_LEFT);
                    }
                }
                mLeftSelectedSeat.clrSelectedFlag(ClassInHandApplication.SEATED_LEFT);
                mLeftSelectedSeat = null;
                layout_onseatclick_left.removeAllViews();
                mLeftCancelButton.setVisibility(View.INVISIBLE);
                mChangeSeatButton.setVisibility(View.INVISIBLE);
                if (mRightSelectedSeat == null) {
                    layout_onseatclick_inflated.setVisibility(View.GONE);
                    mRandomAssignButton.setVisibility(View.VISIBLE);
                } else {
                    mVacateSeatButton.setVisibility(View.VISIBLE);
                }
                mSeatGridAdapter.notifyDataSetChanged();
            }
        });
        /* 오른쪽 선택된 자리 선택취소 버튼 */
        mRightCancelButton = (Button)layout_onseatclick_inflated.findViewById(R.id.btn_right_cancel);
        mRightCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Seat seat;
                if(mRightSelectedSeat.getItsStudent() != null) {
                    for (PersonalHistory p : mRightSelectedSeat.getItsStudent().getHistories()) {
                        seat = mNewPlan.getmSeats().get(p.seatId);
                        seat.clrRecentSeatedFlag(ClassInHandApplication.SEATED_RIGHT);
                    }
                }
                mRightSelectedSeat.clrSelectedFlag(ClassInHandApplication.SEATED_RIGHT);
                mRightSelectedSeat = null;
                layout_onseatclick_right.removeAllViews();
                mRightCancelButton.setVisibility(View.INVISIBLE);
                mChangeSeatButton.setVisibility(View.INVISIBLE);
                if (mLeftSelectedSeat == null) {
                    layout_onseatclick_inflated.setVisibility(View.GONE);
                    mRandomAssignButton.setVisibility(View.VISIBLE);
                } else {
                    mVacateSeatButton.setVisibility(View.VISIBLE);
                }
                mSeatGridAdapter.notifyDataSetChanged();
            }
        });

        /* 선택된 seat 가 없거나, 오른쪽 자리만 선택되어 있는 상태 */
        if(mLeftSelectedSeat == null) {
            mLeftSelectedSeat = mNewPlan.getmSeats().get(position);

            /* 중복 선택 검사! */
            if(mLeftSelectedSeat == mRightSelectedSeat) {
                mLeftSelectedSeat = null;
                return;
            }
            selectedStudent = mLeftSelectedSeat.getItsStudent();
            mLeftSelectedSeat.setSelectedFlag(ClassInHandApplication.SEATED_LEFT);
            /* 선택한 자리가 빈자리일 경우 */
            if(selectedStudent == null) {
                if(mRightSelectedSeat == null) {    // 오른쪽 선택된자리 없음 : 첫 선택이 빈자리일 경우임
                    if (mLeftSelectedSeat.getId() % 6 < 3) { // 터치한 자리가 좌측 절반에 포함
                        mDrawerLayout.openDrawer(Gravity.RIGHT);
                    } else {
                        mDrawerLayout.openDrawer(Gravity.LEFT);
                    }
                }
                else {                              // 오른쪽 선택된자리 있는 상태 : 두번째 선택이 빈자리일 경우임
                    tv = new TextView(this);
                    tv.setText(getString(R.string.text_empty_seat));
                    tv.setTextSize(18);
                    layout_onseatclick_left.addView(tv);
                    mLeftCancelButton.setVisibility(View.VISIBLE);
                }
            }
            /* 선택한 자리에 배정된 학생이 있음, 학생의 과거 자리/짝 정보 표시 */
            else {
                tv = new TextView(this);
                tv.setText(selectedStudent.getName());
                tv.setTextSize(18);
                tv.setBackgroundColor(this.getResources().getColor(R.color.pink_200));
                layout_onseatclick_left.addView(tv);
                ViewGroup.LayoutParams params = tv.getLayoutParams();
                params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                tv.setLayoutParams(params);

                int historyCount = 1;
                for(PersonalHistory p : selectedStudent.getHistories()) {
                    String whenStr = String.format("%02d.%02d ~\n", p.applyDate.get(Calendar.MONTH) + 1, p.applyDate.get(Calendar.DAY_OF_MONTH));
                    String whereStr = ConvertAbsSeatToSegAndRow(p.seatId) + ", ";
                    Student pairStudent = application.findStudent(p.pairId);
                    String pairStr = pairStudent == null ? "" : pairStudent.getName();
                    tv = new TextView(this);
                    tv.setText(whenStr + whereStr + pairStr);
                    tv.setTextSize(14);
                    layout_onseatclick_left.addView(tv);

                    Seat seat = mNewPlan.getmSeats().get(p.seatId);
                    seat.setRecentSeatedFlag(ClassInHandApplication.SEATED_LEFT);

                    if(++historyCount > ClassInHandApplication.NUM_HISTORY) break;
                }
                mLeftCancelButton.setVisibility(View.VISIBLE);
            }
        }
        /* 왼쪽 자리만 선택되어 있는 상태 */
        else if(mRightSelectedSeat == null) {
            mRightSelectedSeat = mNewPlan.getmSeats().get(position);
            /* 중복 선택 검사! */
            if(mLeftSelectedSeat == mRightSelectedSeat) {
                mRightSelectedSeat = null;
                return;
            }
            selectedStudent = mRightSelectedSeat.getItsStudent();
            mRightSelectedSeat.setSelectedFlag(ClassInHandApplication.SEATED_RIGHT);
            /* 선택한 자리가 빈자리일 경우 */
            if(selectedStudent == null) {
                if(mLeftSelectedSeat == null) {     // 일어날 수 없는 경우임.
                    System.out.println("Cannot reach here!!");
                }
                else {      // 왼쪽 자리가 선택된 상태에서 오른쪽에 빈자리를 선택한 상태
                    tv = new TextView(this);
                    tv.setText(getString(R.string.text_empty_seat));
                    tv.setTextSize(18);
                    layout_onseatclick_right.addView(tv);
                    mRightCancelButton.setVisibility(View.VISIBLE);
                }
            }
            /* 선택한 자리에 배정된 학생이 있음, 학생의 과거 자리/짝 정보 표시 */
            else {
                tv = new TextView(this);
                tv.setText(selectedStudent.getName());
                tv.setTextSize(18);
                tv.setBackgroundColor(this.getResources().getColor(R.color.light_blue_200));
                layout_onseatclick_right.addView(tv);
                ViewGroup.LayoutParams params = tv.getLayoutParams();
                params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                tv.setLayoutParams(params);

                int historyCount = 1;
                for(PersonalHistory p : selectedStudent.getHistories()) {
                    String whenStr = String.format("%02d.%02d ~\n", p.applyDate.get(Calendar.MONTH) + 1, p.applyDate.get(Calendar.DAY_OF_MONTH));
                    String whereStr = ConvertAbsSeatToSegAndRow(p.seatId) + ", ";
                    Student pairStudent = application.findStudent(p.pairId);
                    String pairStr = pairStudent == null ? "" : pairStudent.getName();
                    tv = new TextView(this);
                    tv.setText(whenStr + whereStr + pairStr);
                    tv.setTextSize(14);
                    layout_onseatclick_right.addView(tv);

                    Seat seat = mNewPlan.getmSeats().get(p.seatId);
                    seat.setRecentSeatedFlag(ClassInHandApplication.SEATED_RIGHT);

                    if(++historyCount > ClassInHandApplication.NUM_HISTORY) break;
                }
                mRightCancelButton.setVisibility(View.VISIBLE);
            }
        }
        /* 자리가 하나만 선택되어 있으면, 자리비움 버튼을 표시 */
        if((mLeftSelectedSeat == null && mRightSelectedSeat != null && mRightSelectedSeat.getItsStudent() != null) ||
                (mLeftSelectedSeat != null && mRightSelectedSeat == null && mLeftSelectedSeat.getItsStudent() != null)) {
            mChangeSeatButton.setVisibility(View.GONE);
            mVacateSeatButton.setVisibility(View.VISIBLE);
        }

        /* 자리가 두개 선택되어 있으면, 자리교환 버튼을 표시 */
        if(mLeftSelectedSeat != null && mRightSelectedSeat != null) {
            mVacateSeatButton.setVisibility(View.GONE);
            mChangeSeatButton.setVisibility(View.VISIBLE);
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
