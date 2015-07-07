package com.iceru.classinhand;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

/**
 * Created by iceru on 15. 5. 9..
 */
public class MessageFragment extends Fragment {
    private final static String TAG = MessageFragment.class.getName();

    // references
    private static MessageFragment  thisObject = null;
    private ClassInHandApplication  application;

    // data structures
    private ArrayList<Student>      mContacts;
    private ContactsAdapter         mContactsAdapter;

    // views
    private RecyclerView            mContactsRecyclerView;
    private RecyclerView.LayoutManager  mLayoutManager;
    private android.support.design.widget.FloatingActionButton mMainFab, mSelectAllFab, mDeselectAllFab;
    private boolean                 mFabExpanded;
    private View                    mDimView;

    private TextView                mMainFabLabel;
    private LinearLayout            mSelectAllFabLayout, mDeselectAllFabLayout;

    // animations
    private Animation rotateAni;
    private Animation alphaAni;
    private Animation translateAni;

    public static MessageFragment getInstance() {
        if(thisObject == null) thisObject = new MessageFragment();
        return thisObject;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        application = ClassInHandApplication.getInstance();
        mContacts = application.getmStudents();

        rotateAni = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate);
        alphaAni = AnimationUtils.loadAnimation(getActivity(), R.anim.alpha);
        translateAni = AnimationUtils.loadAnimation(getActivity(), R.anim.translate);

        mFabExpanded = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_message, container, false);
        mContactsRecyclerView = (RecyclerView)v.findViewById(R.id.recyclerview_contacts);
        mMainFab = (android.support.design.widget.FloatingActionButton)v.findViewById(R.id.fab_message_main);
        mSelectAllFab = (android.support.design.widget.FloatingActionButton)v.findViewById(R.id.fab_message_selectall);
        mDeselectAllFab = (android.support.design.widget.FloatingActionButton)v.findViewById(R.id.fab_message_deselectall);
        mDimView = v.findViewById(R.id.dimview_message);
        mDimView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideFabComponents();
            }
        });
        mSelectAllFabLayout = (LinearLayout)v.findViewById(R.id.linearlayout_message_fab_selectall);
        mDeselectAllFabLayout = (LinearLayout)v.findViewById(R.id.linearlayout_message_fab_deselectall);
        mMainFabLabel = (TextView)v.findViewById(R.id.textview_sendsms);

        mLayoutManager = new LinearLayoutManager(application.getApplicationContext());
        mContactsRecyclerView.setLayoutManager(mLayoutManager);
        mContactsAdapter = new ContactsAdapter(mContacts, application.getApplicationContext());
        mContactsRecyclerView.setAdapter(mContactsAdapter);

        mSelectAllFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContactsAdapter.setAllChecked(true);
                hideFabComponents();
            }
        });
        mDeselectAllFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContactsAdapter.setAllChecked(false);
                hideFabComponents();
            }
        });

        mMainFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mFabExpanded) {
                    mMainFab.setImageResource(R.drawable.ic_message_white_18dp);
                    mMainFab.startAnimation(rotateAni);
                    mMainFabLabel.setVisibility(View.VISIBLE);
                    mDimView.setVisibility(View.VISIBLE);
                    mDimView.startAnimation(alphaAni);
                    mSelectAllFabLayout.setVisibility(View.VISIBLE);
                    mSelectAllFabLayout.startAnimation(translateAni);
                    mDeselectAllFabLayout.setVisibility(View.VISIBLE);
                    mDeselectAllFabLayout.startAnimation(translateAni);
                    mFabExpanded = true;
                }
                else {
                    hideFabComponents();
                    composeMessage();
                }
            }
        });

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }

    private void hideFabComponents() {
        mFabExpanded = false;
        mMainFab.setImageResource(R.drawable.ic_add_white_18dp);
        mMainFabLabel.setVisibility(View.GONE);
        mDimView.clearAnimation();
        mDimView.setVisibility(View.GONE);
        mSelectAllFabLayout.clearAnimation();
        mSelectAllFabLayout.setVisibility(View.GONE);
        mDeselectAllFabLayout.clearAnimation();
        mDeselectAllFabLayout.setVisibility(View.GONE);
    }

    private void composeMessage() {
        ArrayList<Student> recipients = mContactsAdapter.getSelectedStudents();
        if(recipients.size() == 0) {
            Toast.makeText(application.getApplicationContext(), R.string.warning_no_recipient, Toast.LENGTH_SHORT).show();
            return;
        }

        StringBuilder builder = new StringBuilder();
        String delim = "";
        for (Student s : recipients) {
            //  For every phone number in our list
            builder.append(delim).append(s.getPhone());
            delim=",";
        }
        String dests = builder.toString();

        MessageDialog dialog = new MessageDialog(getActivity(), dests, recipients.size());
        dialog.show();
    }
}
