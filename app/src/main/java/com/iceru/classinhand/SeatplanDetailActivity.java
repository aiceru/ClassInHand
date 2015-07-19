package com.iceru.classinhand;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.DatePicker;
import android.widget.GridView;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TreeMap;

/**
 * Created by iceru on 14. 12. 19..
 */
public class SeatplanDetailActivity extends ActionBarActivity {

    /* Application Class */
    private ClassInHandApplication application;

    /* Data Structures */
    private Seatplan mSeatplan;
    private GregorianCalendar mDate, mNewDate;
    private SeatGridAdapter mSeatGridAdapter;

    /* Views */
    private ExpandableGridView mSeatGridView;
    private android.support.design.widget.FloatingActionButton mPlusFab, mEditFab, mDeleteFab;
    private TextView mEditLabel, mDeleteLabel;

    private Animation mFabRotateAppearAni, mFabRotateDisappearAni,
            mDimViewOnAni, mLabelAppearAni, mFabScaleAppearAni;

    private View mDimView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        application = ClassInHandApplication.getInstance();

        Intent intent = getIntent();
        int position = intent.getIntExtra(ClassInHandApplication.SEATPLAN_SELECTED_POSITION, -1);

        if (position < 0) finish();

        mSeatplan = application.getmSeatplans().get(position);
        mDate = mSeatplan.getmApplyDate();

        setContentView(R.layout.activity_seatplan_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setHomeButtonEnabled(true);

        getSupportActionBar().setTitle(application.getDateString(mDate));

        mSeatGridView = (ExpandableGridView) findViewById(R.id.gridview_seats);
        mPlusFab = (android.support.design.widget.FloatingActionButton)findViewById(R.id.fab_plus);
        mEditFab = (android.support.design.widget.FloatingActionButton)findViewById(R.id.fab_edit);
        mDeleteFab = (android.support.design.widget.FloatingActionButton)findViewById(R.id.fab_delete);

        mEditLabel = (TextView)findViewById(R.id.label_edit);
        mDeleteLabel = (TextView)findViewById(R.id.label_delete);

        mDimView = findViewById(R.id.dimview);

        mFabRotateAppearAni = AnimationUtils.loadAnimation(this, R.anim.fab_rotate_appear);
        mFabRotateDisappearAni = AnimationUtils.loadAnimation(this, R.anim.fab_rotate_disappear);
        mFabScaleAppearAni = AnimationUtils.loadAnimation(this, R.anim.fab_scale_appear);
        mLabelAppearAni = AnimationUtils.loadAnimation(this, R.anim.toalpha_1_0);
        mDimViewOnAni = AnimationUtils.loadAnimation(this, R.anim.toalpha_0_7);

        mSeatGridView.setExpanded(true);
        mSeatGridView.setNumColumns(mSeatplan.getmColumns()/2);
        mSeatGridAdapter = new SeatGridAdapter(mSeatplan.getmSeats(), this, mSeatplan.getmColumns()/2, null, null, null);
        mSeatGridView.setAdapter(mSeatGridAdapter);
    }

    private void displayDatepicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                if(view.isShown()) {
                    mNewDate = new GregorianCalendar();
                    mNewDate.clear();
                    mNewDate.set(year, monthOfYear, dayOfMonth);
                    if (mNewDate.compareTo(mDate) != 0 && application.findSeatplan(mNewDate) != null) {
                        confirmEditExistingPlan();
                    } else {
                        runSeatplanActivity();
                    }
                }
            }
        };
        DatePickerDialog dateDialog = new DatePickerDialog(this, R.style.dialog_style,
                dateSetListener, mDate.get(Calendar.YEAR), mDate.get(Calendar.MONTH),
                mDate.get(Calendar.DAY_OF_MONTH));
        dateDialog.show();
    }

    private void confirmEditExistingPlan() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.dialog_style);
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

    public void onClickPlus(View v) {
        mDimView.setVisibility(View.VISIBLE);
        mEditFab.setVisibility(View.VISIBLE);
        mEditLabel.setVisibility(View.VISIBLE);
        mDeleteFab.setVisibility(View.VISIBLE);
        mDeleteLabel.setVisibility(View.VISIBLE);

        mDimView.startAnimation(mDimViewOnAni);
        mPlusFab.startAnimation(mFabRotateDisappearAni);
        mEditFab.startAnimation(mFabRotateAppearAni);
        mDeleteFab.startAnimation(mFabScaleAppearAni);
        mEditLabel.startAnimation(mLabelAppearAni);
        mDeleteLabel.startAnimation(mLabelAppearAni);
    }

    public void onClickDimView(View v) {
        hideFabComponents();
    }

    private void hideFabComponents() {
        mDimView.clearAnimation();
        mEditFab.clearAnimation();
        mDeleteFab.clearAnimation();
        mEditLabel.clearAnimation();
        mDeleteLabel.clearAnimation();
        mPlusFab.clearAnimation();

        mDimView.setVisibility(View.GONE);
        mEditFab.setVisibility(View.GONE);
        mDeleteFab.setVisibility(View.GONE);
        mEditLabel.setVisibility(View.GONE);
        mDeleteLabel.setVisibility(View.GONE);
    }

    public void onClickEdit(View v) {
        /* Ask if changes ApplyDate or not */
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.dialog_style);
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

    public void onClickDelete(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.dialog_style);
        builder.setTitle(R.string.title_dialog_warning);
        builder.setMessage(R.string.contents_dialog_confirm_delete);
        builder.setPositiveButton(R.string.action_delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                application.removeSeatplan(mSeatplan);
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
}