package com.iceru.classinhand;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.util.TreeMap;


public class AddPersonActivity extends ActionBarActivity {

    private ClassInHandApplication          application;
    private TreeMap<Integer, Student>       mAddingStudents;

    private RecyclerView                    mAddingListRecyclerView;
    private RecyclerView.Adapter            mAddingListAdapter;
    private RecyclerView.LayoutManager      mAddingListLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        application = ClassInHandApplication.getInstance();
        mAddingStudents = new TreeMap<Integer, Student>();

        Student s;
        int i;
        for(i = 0; i < 3; i++) {
            s = new Student(i, i+1, "남자추가 " + String.valueOf(i+1), true);
            mAddingStudents.put(i, s);
        }
        for(; i < 8; i++) {
            s = new Student(i, i+1, "여자추가 " + String.valueOf(i-9), false);
            mAddingStudents.put(i, s);
        }

        setContentView(R.layout.activity_add_person);
        initViews();
    }

    private void initViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.addpersonactivity_toolbar);

        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        setSupportActionBar(toolbar);

        mAddingListRecyclerView = (RecyclerView)findViewById(R.id.recyclerview_adding_list);
        mAddingListLayoutManager = new LinearLayoutManager(this);
        mAddingListRecyclerView.setLayoutManager(mAddingListLayoutManager);
        mAddingListAdapter = new StudentListAdapter(mAddingStudents, this);
        mAddingListRecyclerView.setAdapter(mAddingListAdapter);
    }


    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_person, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }*/
}
