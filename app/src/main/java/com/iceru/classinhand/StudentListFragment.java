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

    private RecyclerView                    mStudentListRecyclerView;
    private StudentListAdapter              mStudentListAdapter;
    private RecyclerView.LayoutManager      mStudentListLayoutManager;
    private FloatingActionButton            mFABaddStudent;

    private boolean                         mShowcaseShown;

    public static StudentListFragment newInstance() {
        StudentListFragment fragment = new StudentListFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainActivity = (MainActivity)getActivity();
        application = ClassInHandApplication.getInstance();
        mStudents = application.getmStudents();

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mShowcaseShown = sp.getBoolean(ClassInHandApplication.PREF_STUDENTLIST_SHOWCASE, false);
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
        mFABaddStudent = (FloatingActionButton)rootView.findViewById(R.id.fab_add_student);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mStudentListAdapter.notifyDataSetChanged();

        if(!mShowcaseShown) {
            mFABaddStudent.post(new Runnable() {
                @Override
                public void run() {
                    View v = mFABaddStudent;
                    int[] location = new int[2];
                    int[] size = new int[2];
                    v.getLocationOnScreen(location);
                    size[0] = v.getMeasuredWidth();
                    size[1] = v.getMeasuredHeight();
                    Intent showcaseIntent = new Intent(getActivity(), ShowcaseActivity.class);
                    showcaseIntent.putExtra(ClassInHandApplication.SHOWCASE_TARGET_POSITION, location);
                    showcaseIntent.putExtra(ClassInHandApplication.SHOWCASE_TARGET_SIZE, size);
                    showcaseIntent.putExtra(ClassInHandApplication.SHOWCASE_MESSAGE, R.string.welcome_add_student);
                    startActivityForResult(showcaseIntent, ClassInHandApplication.REQUESTCODE_STUDENTLIST_SHOWCASE);
                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case ClassInHandApplication.REQUESTCODE_STUDENTLIST_SHOWCASE:
                this.mShowcaseShown = true;
                SharedPreferences sp = PreferenceManager
                        .getDefaultSharedPreferences(getActivity());
                sp.edit().putBoolean(ClassInHandApplication.PREF_STUDENTLIST_SHOWCASE, true).apply();
                break;
            default:
                break;
        }
    }
}
