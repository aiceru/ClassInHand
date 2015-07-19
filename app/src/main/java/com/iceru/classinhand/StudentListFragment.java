package com.iceru.classinhand;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.getbase.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

/**
 * Created by iceru on 14. 11. 19..
 */
public class StudentListFragment extends Fragment {
    private ClassInHandApplication          application;
    private MainActivity                    mainActivity;
    private ArrayList<Student>              mStudents;

    private static StudentListFragment      thisObject;
    private RecyclerView                    mStudentListRecyclerView;
    private StudentListAdapter              mStudentListAdapter;
    private RecyclerView.LayoutManager      mStudentListLayoutManager;

    public static StudentListFragment getInstance() {
        if(thisObject == null) {
            thisObject = new StudentListFragment();
        }
        return thisObject;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainActivity = (MainActivity)getActivity();
        application = ClassInHandApplication.getInstance();
        mStudents = application.getmStudents();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_studentlist, container, false);

        mStudentListRecyclerView = (RecyclerView)rootView.findViewById(R.id.recyclerview_studentlist);
        mStudentListRecyclerView.setHasFixedSize(false);
        mStudentListLayoutManager = new LinearLayoutManager(mainActivity);
        mStudentListRecyclerView.setLayoutManager(mStudentListLayoutManager);
        mStudentListAdapter = new StudentListAdapter(mStudents, mainActivity);
        mStudentListRecyclerView.setAdapter(mStudentListAdapter);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mStudentListAdapter.notifyDataSetChanged();
    }
}
