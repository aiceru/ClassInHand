package com.iceru.classinhand;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        application = ClassInHandApplication.getInstance();

        Intent intent = getIntent();
        mStudent = application.findStudent(intent.getIntExtra(ClassInHandApplication.STUDENT_SELECTED_ID, -1));
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
        mIndate.setTimeInMillis(mStudent.getInDate());
        mTextviewIndate.setText(getDateString(mIndate));

        if(mStudent.getOutDate() == Long.MAX_VALUE) {
            mOutdate = null;
        }
        else {
            mOutdate = new GregorianCalendar();
            mOutdate.setTimeInMillis(mStudent.getOutDate());
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
        getMenuInflater().inflate(R.menu.menu_student_detail, menu);
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
                return true;
            case R.id.action_student_detail_delete:
                deleteMe();
        }

        return super.onOptionsItemSelected(item);
    }

    private void deleteMe() {

    }
}
