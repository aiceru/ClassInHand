package com.iceru.classinhand;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.TreeMap;


public class AddPersonActivity extends ActionBarActivity {

    /* Application Class */
    private ClassInHandApplication          application;

    /* Data Structures */
    private TreeMap<Integer, Student>       mStudents;
    private TreeMap<Integer, Student>       mAddingStudents;
    private boolean[]                       mAttendNumArray;

    /* Views */
    private RecyclerView                    mAddingListRecyclerView;
    private RecyclerView.Adapter            mAddingListAdapter;
    private RecyclerView.LayoutManager      mAddingListLayoutManager;

    //private Spinner                         mGenderSpinner;
    private ToggleButton                    mGenderTglbtn;
    private EditText                        mNameEditText;
    private EditText                        mAttendNumEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        application = ClassInHandApplication.getInstance();
        mStudents = application.getmStudents();

        mAddingStudents = new TreeMap<Integer, Student>();

        mAttendNumArray = new boolean[ClassInHandApplication.MAX_STUDENTS]; // initialized to false
        for(TreeMap.Entry<Integer, Student> entry : mStudents.entrySet()) {
            mAttendNumArray[entry.getValue().getAttendNum()] = true;
        }

        /*Student s;
        int i;
        for(i = 0; i < 3; i++) {
            s = new Student(i, i+1, "남자추가 " + String.valueOf(i+1), true);
            mAddingStudents.put(i, s);
        }
        for(; i < 8; i++) {
            s = new Student(i, i+1, "여자추가 " + String.valueOf(i-3), false);
            mAddingStudents.put(i, s);
        }*/

        setContentView(R.layout.activity_add_person);
        initViews();
    }

    private void initViews() {
        int attendNum = 1;
        Toolbar toolbar = (Toolbar) findViewById(R.id.addpersonactivity_toolbar);

        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        setSupportActionBar(toolbar);

        mAddingListRecyclerView = (RecyclerView)findViewById(R.id.recyclerview_adding_list);
        mAddingListRecyclerView .setHasFixedSize(true);
        mAddingListLayoutManager = new LinearLayoutManager(this);
        mAddingListRecyclerView.setLayoutManager(mAddingListLayoutManager);
        mAddingListAdapter = new StudentListAdapter(mAddingStudents, this);
        mAddingListRecyclerView.setAdapter(mAddingListAdapter);

        mAttendNumEditText = (EditText)findViewById(R.id.edittext_addperson_attendnum);
        mNameEditText = (EditText)findViewById(R.id.edittext_addperson_name);

        mNameEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE) {
                    putToAddingList(getWindow().getDecorView().findViewById(android.R.id.content));
                    return true;
                }
                return false;
            }
        });

        mGenderTglbtn = (ToggleButton)findViewById(R.id.tglbtn_addperson_gender);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        while(mAttendNumArray[attendNum]) attendNum++;
        mAttendNumEditText.setText(String.valueOf(attendNum));
    }

    public void putToAddingList(View view) {
        Student student;
        String name, numStr;
        int attendNum;
        boolean isboy;

        numStr = mAttendNumEditText.getText().toString();
        name = mNameEditText.getText().toString();
        isboy = mGenderTglbtn.isChecked();

        if(numStr == null) {
            Toast.makeText(this, R.string.warning_edittext_num_is_null, Toast.LENGTH_SHORT).show();
            return;
        }

        if(name == null) {
            Toast.makeText(this, R.string.warning_edittext_name_is_null, Toast.LENGTH_SHORT).show();
            return;
        }

        attendNum = Integer.valueOf(numStr);

        if(mAttendNumArray[attendNum]) {
            Toast.makeText(this, R.string.warning_existing_num, Toast.LENGTH_SHORT).show();
            return;
        }

        student = new Student(ClassInHandApplication.NEXT_ID, attendNum, name, isboy);
        ClassInHandApplication.NEXT_ID++;

        mAddingStudents.put(student.getId(), student);
        mAttendNumArray[attendNum] = true;

        mAddingListAdapter.notifyDataSetChanged();

        while(mAttendNumArray[attendNum]) attendNum++;
        mAttendNumEditText.setText(String.valueOf(attendNum));
        mNameEditText.setText(null);
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
            mStudents.putAll(mAddingStudents);
            //TODO : Save mAddingStudents to database
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
