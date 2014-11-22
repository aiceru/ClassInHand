package com.iceru.classinhand;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

import java.util.Collection;
import java.util.TreeMap;

import javax.annotation.Nullable;

/**
 * Created by iceru on 14. 11. 19..
 */
public class StudentListFragment extends Fragment {
    private MainActivity                    mainActivity;
    private TreeMap<Integer, Student>       mStudents;

    private RecyclerView                    mStudentListRecyclerView;
    private RecyclerView.Adapter            mStudentListAdapter;
    private RecyclerView.LayoutManager      mStudentListLayoutManager;
    /*private RecyclerView                    mBoysListRecyclerView;
    private RecyclerView                    mGirlsListRecyclerView;
    private RecyclerView.Adapter            mBoysListAdapter;
    private RecyclerView.Adapter            mGirlsListAdapter;
    private RecyclerView.LayoutManager      mBoysListLayoutManager;
    private RecyclerView.LayoutManager      mGirlsListLayoutManager;*/

    public static StudentListFragment newInstance() {
        StudentListFragment fragment = new StudentListFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainActivity = (MainActivity)getActivity();
        mStudents = mainActivity.getmStudents();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_studentlist, container, false);

        mStudentListRecyclerView = (RecyclerView)rootView.findViewById(R.id.recyclerview_studentlist);
        mStudentListRecyclerView.setHasFixedSize(true);
        mStudentListLayoutManager = new LinearLayoutManager(mainActivity);
        mStudentListRecyclerView.setLayoutManager(mStudentListLayoutManager);
        mStudentListAdapter = new StudentListAdapter(mStudents);
        mStudentListRecyclerView.setAdapter(mStudentListAdapter);
        /*mBoysListRecyclerView = (RecyclerView)rootView.findViewById(R.id.recyclerview_boyslist);
        mGirlsListRecyclerView = (RecyclerView)rootView.findViewById(R.id.recyclerview_girlslist);

        mBoysListRecyclerView.setHasFixedSize(true);
        mGirlsListRecyclerView.setHasFixedSize(true);

        mBoysListLayoutManager = new LinearLayoutManager(mainActivity);
        mBoysListRecyclerView.setLayoutManager(mBoysListLayoutManager);
        mGirlsListLayoutManager = new LinearLayoutManager(mainActivity);
        mGirlsListRecyclerView.setLayoutManager(mGirlsListLayoutManager);*/

        /*Collection<Student> boysList = Collections2.filter(mStudents.values(), new Predicate<Student>() {
            @Override
            public boolean apply(@Nullable Student input) {
                return input.isBoy();
            }
        });
        Collection<Student> girlsList = Collections2.filter(mStudents.values(), new Predicate<Student>() {
            @Override
            public boolean apply(@Nullable Student input) {
                return !input.isBoy();
            }
        });

        mBoysListAdapter = new StudentListAdapter(boysList);
        mBoysListRecyclerView.setAdapter(mBoysListAdapter);
        mGirlsListAdapter = new StudentListAdapter(girlsList);
        mGirlsListRecyclerView.setAdapter(mGirlsListAdapter);*/

        return rootView;
    }
}
