package com.iceru.classinhand;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TreeMap;

/**
 * Created by iceru on 14. 12. 19..
 */
public class SeatplanEditActivity extends AppCompatActivity {
    private static final int TOP = 1;
    private static final int BOTTOM = 2;

    /* Application Class */
    private ClassInHandApplication application;

    /* Data Structures */
    private TreeMap<Integer, Student> mRemainStudents;
    private ArrayList<Seat> mFlaggedSeatList;
    private Seatplan mNewPlan, mOldPlan;
    private GregorianCalendar mNewDate, mOldDate;

    private boolean mSaved = false;
    private boolean mDoNotOpenManual;

    /* Views */
    private NestedScrollView    mRootScrollView;
    private ExpandableGridView mSeatsGridView;
    private ExpandableGridView mRemainStudentGridView;
    private SeatGridAdapter mSeatGridAdapter;
    private StudentGridAdapter mStudentGridAdapter;
    private DragEventListenerOfSeats mDragEventListenerOfSeats;

    private android.support.design.widget.FloatingActionButton  mPlusFab, mAssignRandomFab, mClearAllFab;
    private TextView    mAssignRandomLabel, mClearAllLabel;

    private View mTopRegion;
    private View mBottomRegion;
    private View mDimview;

    private LinearLayout mHistoryLayout;

    private Animation mFabRotateAppearAni, mFabRotateDisappearAni,
            mFabScaleAppearAni, mLabelAppearAni, mDimViewOnAni;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        application = ClassInHandApplication.getInstance();
        int row, col;

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        mDoNotOpenManual = sp.getBoolean(ClassInHandApplication.PREF_EDIT_SEATPLAN_DO_NOT_OPEN_MANUAL, false);

        Intent intent = getIntent();
        long newDatelong = intent.getLongExtra(ClassInHandApplication.SEATPLAN_EDIT_NEWDATE, 0);
        long oldDatelong = intent.getLongExtra(ClassInHandApplication.SEATPLAN_EDIT_OLDDATE, 0);

        if (newDatelong == 0) finish();

        mRemainStudents = application.getDatedStudentsTreeMapKeybyAttendNum(newDatelong);
        mFlaggedSeatList = new ArrayList<>();

        mNewDate = new GregorianCalendar();
        mNewDate.setTimeInMillis(newDatelong);
        if (oldDatelong == 0) {
            mOldDate = null;
            mOldPlan = null;

            col = application.globalProperties.columns;
            row = application.globalProperties.rows;

            // 좌석 수 < 학생 수 인 경우!!
            if (row * col < mRemainStudents.size()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.dialog_style);
                builder.setTitle(R.string.title_dialog_warning);
                builder.setMessage(R.string.contents_dialog_confirm_increase_row);
                builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button
                        finish();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        finish();
                    }
                });
                dialog.show();
            }

            mNewPlan = new Seatplan(
                    mNewDate,
                    new ArrayList<Seat>(),
                    col, row,
                    application.globalProperties.isBoyRight);
            for (int i = 0; i < row * col; i++) {
                Seat s = new Seat(i);
                mNewPlan.getmSeats().add(s);
            }
        } else {
            /* 학생 목록에 변동 있을 시 수정 거부! */
            if (!mRemainStudents.equals(application.getDatedStudentsTreeMapKeybyAttendNum(oldDatelong))) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.dialog_style);
                builder.setTitle(R.string.title_dialog_warning);
                builder.setMessage(R.string.contents_dialog_remainstudents_not_equals);
                builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        finish();
                    }
                });
                dialog.show();
            }

            mOldDate = new GregorianCalendar();
            mOldDate.setTimeInMillis(oldDatelong);
            mOldPlan = application.findSeatplan(mOldDate);

            col = mOldPlan.getmColumns();
            row = mOldPlan.getmRows();

            mNewPlan = new Seatplan(
                    mNewDate,
                    new ArrayList<Seat>(),
                    col, row,
                    mOldPlan.isBoyRight());
            for (int i = 0; i < row * col; i++) {
                Seat olds = mOldPlan.getmSeats().get(i);
                Seat s = new Seat(i, olds.getItsStudent(), olds.isFixed());
                if (s.getItsStudent() != null) {
                    mRemainStudents.remove(s.getItsStudent().getAttendNum());
                }
                mNewPlan.getmSeats().add(s);
            }
        }

        /* initialize Views */
        setContentView(R.layout.activity_seatplan_edit);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(application.getDateString(mNewDate));

        mRootScrollView = (NestedScrollView) findViewById(R.id.root_scrollview);
        mRemainStudentGridView = (ExpandableGridView) findViewById(R.id.gridview_remain_students);
        mSeatsGridView = (ExpandableGridView) findViewById(R.id.gridview_seats);
        mHistoryLayout = (LinearLayout) findViewById(R.id.linearlayout_inflating);
        mTopRegion = findViewById(R.id.screen_top_region);
        mBottomRegion = findViewById(R.id.screen_bottom_region);
        mDimview = findViewById(R.id.dimview);
        mPlusFab = (android.support.design.widget.FloatingActionButton)findViewById(R.id.fab_plus);
        mAssignRandomFab = (android.support.design.widget.FloatingActionButton)findViewById(R.id.fab_assign_random);
        mClearAllFab = (android.support.design.widget.FloatingActionButton)findViewById(R.id.fab_clear_all_seats);
        mAssignRandomLabel = (TextView)findViewById(R.id.label_assign_random);
        mClearAllLabel = (TextView)findViewById(R.id.label_clear_all_seats);

        mFabRotateAppearAni = AnimationUtils.loadAnimation(this, R.anim.fab_rotate_appear);
        mFabRotateDisappearAni = AnimationUtils.loadAnimation(this, R.anim.fab_rotate_disappear);
        mFabScaleAppearAni = AnimationUtils.loadAnimation(this, R.anim.fab_scale_appear);
        mDimViewOnAni = AnimationUtils.loadAnimation(this, R.anim.toalpha_0_7);
        mLabelAppearAni = AnimationUtils.loadAnimation(this, R.anim.toalpha_1_0);

        mRemainStudentGridView.setExpanded(true);
        mRemainStudentGridView.setOnDragListener(new DragEventListenerOfStudentGrid());
        mStudentGridAdapter = new StudentGridAdapter(this, mRemainStudents, new StudentGridAdapter.IStudentClick() {
            @Override
            public void studentClick(View v) {
                onStudentClick(v);
            }
        });
        mRemainStudentGridView.setAdapter(mStudentGridAdapter);

        mSeatsGridView.setExpanded(true);
        mDragEventListenerOfSeats = new DragEventListenerOfSeats();
        mSeatGridAdapter = new SeatGridAdapter(mNewPlan.getmSeats(), this, mNewPlan.getmColumns() / 2,
                new SeatGridAdapter.InnerViewHolder.ISeatClick() {
                    @Override
                    public void seatClick(View v) {
                        onSeatClick(v);
                    }
                },
                new SeatGridAdapter.InnerViewHolder.ISeatLongClick() {
                    @Override
                    public boolean seatLongClick(View v) {
                        Seat clicked = mNewPlan.getmSeats().get((int) v.getTag());
                        if(clicked.getItsStudent() == null) {
                            clicked.setFixed(!clicked.isFixed());
                            mSeatGridAdapter.notifyDataSetChanged();
                        }
                        else {
                            String[] descriptions = {
                                    ClipDescription.MIMETYPE_TEXT_PLAIN
                            };
                            ClipData.Item item = new ClipData.Item(String.valueOf((int)v.getTag()));
                            ClipData clipData = new ClipData(Constants.DRAGLABEL_FROM_SEATGRID, descriptions, item);

                            View.DragShadowBuilder shadow = new View.DragShadowBuilder(v);
                            v.startDrag(clipData, shadow, null, 0);
                        }
                        return true;
                    }
                },
                mDragEventListenerOfSeats);
        mSeatsGridView.setAdapter(mSeatGridAdapter);
        mSeatsGridView.setNumColumns(mNewPlan.getmColumns() / 2);

        mTopRegion.setOnDragListener(new DragEventListenerOfEdgeView(TOP, mRootScrollView));
        mBottomRegion.setOnDragListener(new DragEventListenerOfEdgeView(BOTTOM, mRootScrollView));

        // user manual
        if(!mDoNotOpenManual) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.dialog_style);
            builder.setMessage(R.string.edit_seatplan_user_manual);
            builder.setPositiveButton(R.string.donotshowagain, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mDoNotOpenManual = true;
                    SharedPreferences sp = PreferenceManager
                            .getDefaultSharedPreferences(getApplicationContext());
                    sp.edit().putBoolean(ClassInHandApplication.PREF_EDIT_SEATPLAN_DO_NOT_OPEN_MANUAL, true).apply();
                }
            }).show();
        }
    }

    public void onClickAssignRandom(View view) {
        hideFabComponents();

        int emptyseat = 0;
        for(Seat s : mNewPlan.getmSeats()) {
            if(!s.isFixed() && s.getItsStudent() == null) emptyseat++;
        }

        if(emptyseat < mRemainStudents.size()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.dialog_style);
            builder.setTitle(R.string.title_dialog_warning);
            builder.setMessage(R.string.contents_dialog_cannot_auto_assign);
            builder.setPositiveButton(R.string.confirm, null);
            builder.create().show();
        }

        else {
            mSaved = false;

            clearHistoryView();
            mHistoryLayout.setVisibility(View.GONE);

            clearAllocation();

            AllocateExecutor AE = new AllocateExecutor(mNewPlan, mRemainStudents);
            AE.createRuleList();
            AE.allocateAllStudent(mNewPlan.getmSeats());

            mSeatGridAdapter.notifyDataSetChanged();
            mStudentGridAdapter.notifyDataSetChanged();
        }
    }

    public void onClickClearAllSeat(View v) {
        mSaved = false;

        clearAllocation();

        clearHistoryView();
        mHistoryLayout.setVisibility(View.GONE);

        hideFabComponents();

        mSeatGridAdapter.notifyDataSetChanged();
        mStudentGridAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_oneitem_done, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_done) {
            if(mRemainStudents.size() > 0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.title_dialog_warning);
                builder.setMessage(R.string.warning_student_remain);
                builder.setPositiveButton(R.string.confirm, null).show();
                return true;
            }
            else {
                mSaved = true;

                clearHistoryView();
                mHistoryLayout.setVisibility(View.GONE);

                // Switch old and new plan simply
                if (mOldPlan != null) application.removeSeatplan(mOldPlan);
                application.addSeatplan(mNewPlan);

                finish();
            }
        }
        if (id == android.R.id.home) {
            this.onBackPressed();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (!mSaved) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.dialog_style);
            builder.setTitle(R.string.title_dialog_warning);
            builder.setMessage(R.string.warning_unsaved);
            builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            builder.setNegativeButton(R.string.no, null);
            builder.show();
        }
    }

    /**
     * Exchanged located {@link Student}s.
     * Guarantees that both seats have located {@link Student} each.
     *
     * @param src the seat where DRAG action started
     * @param dst the seat where DROP action occured
     */
    private void changeSeat(int src, int dst) {
        Seat srcSeat = mNewPlan.getmSeats().get(src);
        Seat dstSeat = mNewPlan.getmSeats().get(dst);

        Student tempStudent = srcSeat.getItsStudent();
        srcSeat.setItsStudent(dstSeat.getItsStudent());
        dstSeat.setItsStudent(tempStudent);

        mSeatGridAdapter.notifyDataSetChanged();
        mSaved = false;
    }

    /**
     * locate {@link Student} to a {@link Seat}.
     *
     * @param seatId seat ID
     * @param AttendNum students's attend num
     */
    private void locateStudent(int seatId, int AttendNum) {
        Seat seat = mNewPlan.getmSeats().get(seatId);
        Student student = mRemainStudents.get(AttendNum);
        Student victim = seat.getItsStudent();

        if(victim != null) {
            mRemainStudents.put(victim.getAttendNum(), victim);
        }
        mRemainStudents.remove(AttendNum);
        seat.setItsStudent(student);

        mStudentGridAdapter.notifyDataSetChanged();
        mSeatGridAdapter.notifyDataSetChanged();
        mSaved = false;
    }

    /**
     * vacate {@link Student} from a {@link Seat}.
     *
     * @param seatId seat ID
     */
    private void vacateStudent(int seatId) {
        Seat seat = mNewPlan.getmSeats().get(seatId);
        Student victim = seat.getItsStudent();
        if(victim != null) {
            mRemainStudents.put(victim.getAttendNum(), victim);
        }
        seat.setItsStudent(null);

        mStudentGridAdapter.notifyDataSetChanged();
        mSeatGridAdapter.notifyDataSetChanged();
        mSaved = false;
    }

    private void onStudentClick(View v) {
        int attendNum = (int)v.getTag();
        setHistoryView(null, mRemainStudents.get(attendNum));
    }

    private void onSeatClick(View v) {
        Seat seat = mNewPlan.getmSeats().get((int)v.getTag());
        Student st = seat.getItsStudent();
        if(st != null) {
            setHistoryView(seat, st);
        }
    }

    private void setHistoryView(Seat selectedSeat, Student student) {
        final LayoutInflater inflater = (LayoutInflater.from(this));
        mHistoryLayout.setVisibility(View.VISIBLE);
        inflater.inflate(R.layout.layout_history, mHistoryLayout);

        clearHistoryView();

        if(selectedSeat != null) {
            selectedSeat.setSelectedFlag(true);
            mFlaggedSeatList.add(selectedSeat);
        }

        TextView studentView = (TextView)mHistoryLayout.findViewById(R.id.textview_studentinfo);
        LinearLayout historyLayout = (LinearLayout)mHistoryLayout.findViewById(R.id.linearlayout_histories);
        historyLayout.removeAllViews();

        int historyCount = 1;

        studentView.setText(String.valueOf(student.getAttendNum()) +
                ". " + student.getName());

        for (PersonalHistory p : student.getHistories()) {
            if (p.applyDate.before(mNewDate)) {
                String whenStr = String.format("%02d.%02d ~ : ", p.applyDate.get(Calendar.MONTH) + 1, p.applyDate.get(Calendar.DAY_OF_MONTH));
                String whereStr = ConvertAbsSeatToSegAndRow(p.seatId, p.totalRows, p.totalCols) + ", ";
                Student pairStudent = application.findStudentById(p.pairId);
                String pairStr = "짝궁: " + (pairStudent == null ? "" : pairStudent.getName());
                TextView tv = new TextView(this);
                tv.setText(whenStr + whereStr + pairStr);
                historyLayout.addView(tv);

                if (p.seatId < mNewPlan.getmSeats().size()) {
                    Seat seat = mNewPlan.getmSeats().get(p.seatId);
                    seat.setRecentSeatedFlag(true);
                    mFlaggedSeatList.add(seat);
                }
                if(++historyCount > application.globalProperties.num_histories) break;
            }
        }

        mSeatGridAdapter.notifyDataSetChanged();
    }

    private void clearHistoryView() {
        for(Seat s : mFlaggedSeatList) {
            s.setSelectedFlag(false);
            s.setRecentSeatedFlag(false);
        }
        mFlaggedSeatList.clear();
        mSeatGridAdapter.notifyDataSetChanged();
    }

    private String ConvertAbsSeatToSegAndRow(int seatId, int totalRows, int totalCols) {
        int convertedId = totalRows * totalCols - 1 - seatId;
        int row = convertedId / totalCols + 1;
        int seg = (totalCols/2) - ((convertedId % totalCols) / 2);
        String segAndRow =
                String.valueOf(seg) +
                        getString(R.string.string_segment) + " " +
                        String.valueOf(row) +
                        getString(R.string.string_row) + " ";
        /*if (convertedId % 2 == 1) segAndRow += getString(R.string.string_left);
        else segAndRow += getString(R.string.string_right);*/
        return segAndRow;
    }

    private void clearAllocation() {
        for (Seat s : mNewPlan.getmSeats()) {
            if (!s.isFixed()) {
                Student st = s.getItsStudent();
                if(st != null) {
                    mRemainStudents.put(st.getAttendNum(), st);
                    s.setItsStudent(null);
                }
            }
        }
        mSaved = false;
    }

    protected class DragEventListenerOfSeats implements View.OnDragListener {
        @Override
        public boolean onDrag(View v, DragEvent event) {
            final int action = event.getAction();
            switch(action) {
                case DragEvent.ACTION_DRAG_STARTED:
                    return true;
                case DragEvent.ACTION_DRAG_ENTERED:
                    v.getBackground().setColorFilter(getResources().getColor(R.color.accent), PorterDuff.Mode.SCREEN);
                    v.invalidate();
                    return true;
                case DragEvent.ACTION_DRAG_LOCATION:
                    return true;
                case DragEvent.ACTION_DRAG_EXITED:
                    v.getBackground().clearColorFilter();
                    v.invalidate();
                    return true;
                case DragEvent.ACTION_DROP:
                    v.getBackground().clearColorFilter();
                    v.invalidate();

                    String label = (String)event.getClipDescription().getLabel();
                    ClipData.Item item = event.getClipData().getItemAt(0);

                    if(label.equals(Constants.DRAGLABEL_FROM_SEATGRID)) {
                        int srcSeatId = Integer.valueOf(String.valueOf(item.getText()));
                        int destSeatId = (int) v.getTag();
                        changeSeat(srcSeatId, destSeatId);
                        clearHistoryView();
                        mHistoryLayout.setVisibility(View.GONE);
                    }
                    else if (label.equals(Constants.DRAGLABEL_FROM_STUDENTGRID)) {
                        int studentAttendNum = Integer.valueOf(String.valueOf(item.getText()));
                        int seatId = (int) v.getTag();
                        locateStudent(seatId, studentAttendNum);
                        clearHistoryView();
                        mHistoryLayout.setVisibility(View.GONE);
                    }
                    return true;
                case DragEvent.ACTION_DRAG_ENDED:
                    v.getBackground().clearColorFilter();
                    v.invalidate();
                    return true;
            }
            return false;
        }
    }

    protected class DragEventListenerOfStudentGrid implements View.OnDragListener {
        @Override
        public boolean onDrag(View v, DragEvent event) {
            final int action = event.getAction();
            String label;
            switch(action) {
                case DragEvent.ACTION_DRAG_STARTED:
                    label = (String)event.getClipDescription().getLabel();
                    return label.equals(Constants.DRAGLABEL_FROM_SEATGRID);
                case DragEvent.ACTION_DRAG_ENTERED:
                    v.getBackground().setColorFilter(getResources().getColor(R.color.accent), PorterDuff.Mode.SCREEN);
                    v.invalidate();
                    return true;
                case DragEvent.ACTION_DRAG_LOCATION:
                    return true;
                case DragEvent.ACTION_DRAG_EXITED:
                    v.getBackground().clearColorFilter();
                    v.invalidate();
                    return true;
                case DragEvent.ACTION_DROP:
                    v.getBackground().clearColorFilter();
                    v.invalidate();

                    label = (String)event.getClipDescription().getLabel();
                    ClipData.Item item = event.getClipData().getItemAt(0);

                    if(label.equals(Constants.DRAGLABEL_FROM_SEATGRID)) {
                        int srcSeatId = Integer.valueOf(String.valueOf(item.getText()));
                        vacateStudent(srcSeatId);
                        clearHistoryView();
                        mHistoryLayout.setVisibility(View.GONE);
                    }
                    return true;
                case DragEvent.ACTION_DRAG_ENDED:
                    v.getBackground().clearColorFilter();
                    v.invalidate();
                    return true;
            }
            return false;
        }
    }

    protected class DragEventListenerOfEdgeView implements View.OnDragListener {
        private int direction;
        private int screenHeight;
        private NestedScrollView rootView;

        DragEventListenerOfEdgeView(int dir, NestedScrollView scrollView) {
            direction = dir;
            rootView = scrollView;

            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            screenHeight = metrics.heightPixels;
        }

        @Override
        public boolean onDrag(View v, DragEvent event) {
            final int action = event.getAction();
            switch(action) {
                case DragEvent.ACTION_DRAG_STARTED:
                    return true;
                case DragEvent.ACTION_DRAG_ENTERED:
                    if(direction == TOP) {
                        rootView.smoothScrollBy(0, -screenHeight);
                    }
                    else if(direction == BOTTOM) {
                        rootView.smoothScrollBy(0, screenHeight);
                    }
                    return true;
                case DragEvent.ACTION_DROP:
                    String label = (String) event.getClipDescription().getLabel();
                    ClipData.Item item = event.getClipData().getItemAt(0);

                    if (label.equals(Constants.DRAGLABEL_FROM_SEATGRID)) {
                        int srcSeatId = Integer.valueOf(String.valueOf(item.getText()));
                        vacateStudent(srcSeatId);
                        clearHistoryView();
                        mHistoryLayout.setVisibility(View.GONE);
                    }
                    return true;
                default:
                    break;
            }
            return false;
        }
    }

    public void onClickPlus(View v) {
        mDimview.setVisibility(View.VISIBLE);
        mAssignRandomFab.setVisibility(View.VISIBLE);
        mClearAllFab.setVisibility(View.VISIBLE);
        mAssignRandomLabel.setVisibility(View.VISIBLE);
        mClearAllLabel.setVisibility(View.VISIBLE);

        mDimview.startAnimation(mDimViewOnAni);
        mPlusFab.startAnimation(mFabRotateDisappearAni);
        mAssignRandomFab.startAnimation(mFabRotateAppearAni);
        mClearAllFab.startAnimation(mFabScaleAppearAni);
        mAssignRandomLabel.startAnimation(mLabelAppearAni);
        mClearAllLabel.startAnimation(mLabelAppearAni);
    }

    public void onClickDimView(View v) {
        hideFabComponents();
    }

    private void hideFabComponents() {
        mDimview.clearAnimation();
        mAssignRandomFab.clearAnimation();
        mClearAllFab.clearAnimation();
        mAssignRandomLabel.clearAnimation();
        mClearAllLabel.clearAnimation();
        mPlusFab.clearAnimation();

        mDimview.setVisibility(View.GONE);
        mAssignRandomFab.setVisibility(View.GONE);
        mClearAllFab.setVisibility(View.GONE);
        mAssignRandomLabel.setVisibility(View.GONE);
        mClearAllLabel.setVisibility(View.GONE);
    }
}
