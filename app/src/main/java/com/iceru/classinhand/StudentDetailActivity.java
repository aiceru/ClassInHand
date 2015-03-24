package com.iceru.classinhand;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.Calendar;
import java.util.GregorianCalendar;


public class StudentDetailActivity extends ActionBarActivity {
    private Student mStudent;
    private ClassInHandApplication application;

    private EditText        mEdittextAttendNum;
    private EditText        mEdittextName;
    private ToggleButton    mTglbtnGender;
    private GregorianCalendar   mIndate;
    private TextView        mTextviewIndate;
    private GregorianCalendar   mOutdate;
    private TextView        mTextviewOutdate;

    private Menu            menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        application = ClassInHandApplication.getInstance();

        Intent intent = getIntent();
        mStudent = application.findStudentById(intent.getIntExtra(ClassInHandApplication.STUDENT_SELECTED_ID, -1));
        if(mStudent == null) finish();

        setContentView(R.layout.activity_student_detail);
        initviews();
    }

    private void initviews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_student_detail);

        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mTglbtnGender = (ToggleButton)findViewById(R.id.tglbtn_student_detail_gender);
        mEdittextAttendNum = (EditText)findViewById(R.id.edittext_student_detail_attendnum);
        mEdittextName = (EditText)findViewById(R.id.edittext_student_detail_name);
        mTextviewIndate = (TextView)findViewById(R.id.textview_student_detail_indate);
        mTextviewOutdate = (TextView)findViewById(R.id.textview_student_detail_outdate);

        mTglbtnGender.setChecked(mStudent.isBoy());
        mEdittextAttendNum.setText(String.valueOf(mStudent.getAttendNum()));
        mEdittextName.setText(mStudent.getName());

        mIndate = new GregorianCalendar();
        mIndate.clear();
        mIndate.setTimeInMillis(mStudent.getInDate());
        mTextviewIndate.setText(getDateString(mIndate));

        mOutdate = new GregorianCalendar();
        mOutdate.clear();
        mOutdate.setTimeInMillis(mStudent.getOutDate());
        if(mOutdate.getTimeInMillis() != Long.MAX_VALUE) {
            mTextviewOutdate.setText(getDateString(mOutdate));
        }

    }

    private String getDateString(GregorianCalendar cal) {
        return (new StringBuilder().append(cal.get(Calendar.YEAR))
                .append(getString(R.string.year_string)).append(" ").append(cal.get(Calendar.MONTH) + 1)
                .append(getString(R.string.month_string)).append(" ").append(cal.get(Calendar.DAY_OF_MONTH))
                .append(getString(R.string.day_string)).append(", ")
                .append(getResources().getStringArray(R.array.dayofweek_array)[cal.get(Calendar.DAY_OF_WEEK) - 1])
                .toString());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_student_detail, menu);
        menu.findItem(R.id.action_done).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch(id) {
            case R.id.action_student_detail_edit:
                editMe();
                break;
            case R.id.action_done:
                saveMe();
                break;
            case R.id.action_student_detail_delete:
                recommendEdit();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void recommendEdit() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.title_dialog_warning);
        builder.setMessage(R.string.contents_dialog_recommend_edit);
        builder.setPositiveButton(R.string.action_delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                deleteMe();
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

    private void deleteMe() {
        application.removeStudent(mStudent);
    }

    private void editMe() {
        menu.findItem(R.id.action_student_detail_edit).setVisible(false);
        menu.findItem(R.id.action_done).setVisible(true);
        menu.findItem(R.id.action_student_detail_delete).setVisible(false);

        mEdittextAttendNum.setEnabled(true);
        mEdittextName.setEnabled(true);
        mTglbtnGender.setClickable(true);
        mTextviewIndate.setClickable(true);
        mTextviewOutdate.setClickable(true);

        mTextviewIndate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickDialog(mIndate, mTextviewIndate);
            }
        });
        mTextviewOutdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickDialog(mOutdate, mTextviewOutdate);
            }
        });
    }

    private void showDatePickDialog(final GregorianCalendar cal, final TextView tv) {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                cal.clear();
                cal.set(year, monthOfYear, dayOfMonth);
                tv.setText(getDateString(cal));
            }
        };
        GregorianCalendar todayCal = application.getmCalToday();
        DatePickerDialog dateDialog = new DatePickerDialog(this, dateSetListener,
                todayCal.get(Calendar.YEAR), todayCal.get(Calendar.MONTH), todayCal.get(Calendar.DAY_OF_MONTH));
        dateDialog.show();
    }

    private void saveMe() {
        int attendNum = Integer.parseInt(mEdittextAttendNum.getText().toString());

        if( (application.findStudentByAttendNum(attendNum) == null) ||
                (mStudent.getAttendNum() == attendNum) ) {
            menu.findItem(R.id.action_student_detail_edit).setVisible(true);
            menu.findItem(R.id.action_done).setVisible(false);
            menu.findItem(R.id.action_student_detail_delete).setVisible(true);

            mStudent.setAttendNum(attendNum);
            mStudent.setName(mEdittextName.getText().toString());
            mStudent.setBoy(mTglbtnGender.isChecked());

            mStudent.setInDate(mIndate.getTimeInMillis());
            mStudent.setOutDate(mOutdate.getTimeInMillis());

            application.updateStudent(mStudent);

            mEdittextAttendNum.setEnabled(false);
            mEdittextName.setEnabled(false);
            mTglbtnGender.setClickable(false);
            mTextviewIndate.setClickable(false);
            mTextviewOutdate.setClickable(false);
        }

        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            AlertDialog dialog = builder.setTitle(R.string.title_dialog_warning)
                                        .setMessage(R.string.warning_existing_attendnum)
                                        .setCancelable(false)
                                        .setPositiveButton(R.string.action_confirm, null)
                                        .create();
            dialog.show();
        }
    }
}
