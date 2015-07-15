package com.iceru.classinhand;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneNumberUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;


public class StudentAddActivity extends AppCompatActivity {

    /* Application Class */
    private ClassInHandApplication          application;

    /* Data Structures */
    private ArrayList<Student> mStudents;
    private boolean[]                       mAttendNumArray;
    private GregorianCalendar               mInDate;

    /* Views */
    private NestedScrollView                mRootScrollView;
    /*private RecyclerView                    mStudentsList;
    private RecyclerView.Adapter            mStudentsListAdapter;
    private RecyclerView.LayoutManager      mStudentsListLayoutManager;*/
    private ToggleButton                    mGenderTglbtn;
    private EditText                        mNameEditText;
    private EditText                        mAttendNumEditText;
    private TextView                        mInDateTextView;
    private EditText                        mPhoneEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        application = ClassInHandApplication.getInstance();
        mStudents = application.getmStudents();

        mInDate = application.getValueOfTodayCalendar();

        mAttendNumArray = new boolean[ClassInHandApplication.MAX_STUDENTS]; // initialized to false
        for(Student s: mStudents) {
            mAttendNumArray[s.getAttendNum()] = true;
        }

        setContentView(R.layout.activity_student_common);
        initViews();
    }

    private void initViews() {
        int attendNum = 1;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        /*mStudentsList = (RecyclerView)findViewById(R.id.recyclerview_student_add_studentlist);
        mStudentsListLayoutManager = new LinearLayoutManager(this);
        mStudentsList.setLayoutManager(mStudentsListLayoutManager);
        mStudentsListAdapter = new StudentListAdapter(mStudents, this);
        mStudentsList.setAdapter(mStudentsListAdapter);
        mStudentsList.scrollToPosition(mStudents.size()-1);*/

        mRootScrollView = (NestedScrollView)findViewById(R.id.root_scrollview);

        mAttendNumEditText = (EditText)findViewById(R.id.edittext_attendnum);
        while(mAttendNumArray[attendNum]) attendNum++;
        mAttendNumEditText.setText(String.valueOf(attendNum));

        mGenderTglbtn = (ToggleButton)findViewById(R.id.tglbtn_gender);

        mNameEditText = (EditText)findViewById(R.id.edittext_name);

        mInDateTextView = (TextView)findViewById(R.id.textview_indate);
        mInDateTextView.setText(getDateString(mInDate));
        mInDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInDatePickerDialog();
            }
        });

        mPhoneEditText = (EditText)findViewById(R.id.edittext_phone);
        mPhoneEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    showSnackbar(addStudent());
                    return true;
                }
                return false;
            }
        });
    }

    private void showSnackbar(Student student) {
        final Snackbar bar;
        if(student != null) {
            StringBuilder builder = new StringBuilder();
            builder.append("\"").append(student.getAttendNum()).append(". ")
                    .append(student.getName()).append("\"")
                    .append(getString(R.string.action_added));
            bar = Snackbar.make(mRootScrollView, builder.toString(), Snackbar.LENGTH_LONG);
            bar.setAction(R.string.action_dismiss, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bar.dismiss();
                }
            });
            View view = bar.getView();
            view.setBackgroundColor(getResources().getColor(R.color.accent));
            TextView tv = (TextView)view.findViewById(android.support.design.R.id.snackbar_text);
            tv.setTextColor(Color.WHITE);
            tv = (TextView)view.findViewById(android.support.design.R.id.snackbar_action);
            tv.setTextColor(Color.WHITE);
            bar.show();
        }
    }

    private Student addStudent() {
        Student student;
        String name, numStr;
        String phone;
        int attendNum;
        boolean isboy;
        boolean ret;

        long inDate = mInDate.getTimeInMillis();
        long today = application.getValueOfTodayCalendar().getTimeInMillis();

        numStr = mAttendNumEditText.getText().toString();
        name = mNameEditText.getText().toString();
        isboy = mGenderTglbtn.isChecked();
        phone = PhoneNumberUtils.formatNumber(mPhoneEditText.getText().toString(), "KR");

        if(numStr.equals("")) {
            Toast.makeText(this, R.string.warning_edittext_num_is_null, Toast.LENGTH_SHORT).show();
            return null;
        }

        if(name.equals("")) {
            Toast.makeText(this, R.string.warning_edittext_name_is_null, Toast.LENGTH_SHORT).show();
            return null;
        }

        attendNum = Integer.valueOf(numStr);

        if(mAttendNumArray[attendNum]) {
            Toast.makeText(this, R.string.warning_existing_attendnum, Toast.LENGTH_SHORT).show();
            return null;
        }

        student = new Student(ClassInHandApplication.NEXT_ID, attendNum, name, isboy,
                phone, (inDate <= today), inDate, Long.MAX_VALUE);
        ClassInHandApplication.NEXT_ID++;

        ret = application.addStudent(student);
        if(ret) {
            mAttendNumArray[attendNum] = true;
            while(mAttendNumArray[attendNum]) attendNum++;
            mAttendNumEditText.setText(String.valueOf(attendNum));
            mNameEditText.setText(null);
            mPhoneEditText.setText(null);

            mAttendNumEditText.requestFocus();
            return student;
        }

        return null;
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
            addStudent();
        }
        return super.onOptionsItemSelected(item);
    }

    private String getDateString(GregorianCalendar cal) {
        return (new StringBuilder().append(cal.get(Calendar.YEAR))
                .append(getString(R.string.year_string)).append(" ").append(cal.get(Calendar.MONTH) + 1)
                .append(getString(R.string.month_string)).append(" ").append(cal.get(Calendar.DAY_OF_MONTH))
                .append(getString(R.string.day_string)).append(", ")
                .append(getResources().getStringArray(R.array.dayofweek_array)[cal.get(Calendar.DAY_OF_WEEK) - 1])
                .toString());
    }

    private void showInDatePickerDialog() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                if(view.isShown()) {
                    mInDate.clear();
                    mInDate.set(year, monthOfYear, dayOfMonth);
                    mInDateTextView.setText(getDateString(mInDate));
                }
            }
        };
        DatePickerDialog dateDialog = new DatePickerDialog(this, R.style.dialog_style, dateSetListener,
                mInDate.get(Calendar.YEAR), mInDate.get(Calendar.MONTH), mInDate.get(Calendar.DAY_OF_MONTH));
        dateDialog.show();
    }
}
