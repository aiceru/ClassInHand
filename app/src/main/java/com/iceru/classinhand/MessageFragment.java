package com.iceru.classinhand;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
    private android.support.design.widget.FloatingActionButton
            mPlusFab, mSendFab, mSelectAllFab, mDeselectAllFab;
    private View                    mDimView;

    private TextView                mMainFabLabel;
    private LinearLayout            mSelectAllFabLayout, mDeselectAllFabLayout;

    // animations
    private Animation rotateAndAppearAni;
    private Animation rotateAndDisappearAni;
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

        rotateAndAppearAni = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_rotate_appear);
        rotateAndDisappearAni = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_rotate_disappear);
        alphaAni = AnimationUtils.loadAnimation(getActivity(), R.anim.toalpha_0_7);
        translateAni = AnimationUtils.loadAnimation(getActivity(), R.anim.translate);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_message, container, false);
        mContactsRecyclerView = (RecyclerView)v.findViewById(R.id.recyclerview_contacts);
        mPlusFab = (android.support.design.widget.FloatingActionButton)v.findViewById(R.id.fab_plus);
        mSendFab = (android.support.design.widget.FloatingActionButton)v.findViewById(R.id.fab_send);
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

        mPlusFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlusFab.startAnimation(rotateAndDisappearAni);
                mSendFab.setVisibility(View.VISIBLE);
                mSendFab.startAnimation(rotateAndAppearAni);
                mMainFabLabel.setVisibility(View.VISIBLE);
                mDimView.setVisibility(View.VISIBLE);
                mDimView.startAnimation(alphaAni);
                mDeselectAllFabLayout.setVisibility(View.VISIBLE);
                mDeselectAllFabLayout.startAnimation(translateAni);
                mSelectAllFabLayout.setVisibility(View.VISIBLE);
                mSelectAllFabLayout.startAnimation(translateAni);
            }
        });

        mSendFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideFabComponents();
                composeMessage();
            }
        });

        return v;
    }

    private void hideFabComponents() {
        mPlusFab.clearAnimation();
        mSendFab.clearAnimation();
        mSendFab.setVisibility(View.GONE);
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
