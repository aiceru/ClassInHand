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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
    private android.support.design.widget.FloatingActionButton mMainFab;
    private FloatingActionButton    mFABselectall;
    private FloatingActionButton    mFABdeselectall;
    private FloatingActionButton    mFABsend;
    // adapter!!

    public static MessageFragment getInstance() {
        if(thisObject == null) thisObject = new MessageFragment();
        return thisObject;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        application = ClassInHandApplication.getInstance();
        mContacts = application.getmStudents();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_message, container, false);
        mContactsRecyclerView = (RecyclerView)v.findViewById(R.id.recyclerview_contacts);
        mMainFab = (android.support.design.widget.FloatingActionButton)v.findViewById(R.id.fab_message_main);
        /*mFABselectall = (FloatingActionButton)v.findViewById(R.id.fab_message_select_all);
        mFABdeselectall = (FloatingActionButton)v.findViewById(R.id.fab_message_deselect_all);
        mFABsend = (FloatingActionButton)v.findViewById(R.id.fab_message_send);*/

        mLayoutManager = new LinearLayoutManager(application.getApplicationContext());
        mContactsRecyclerView.setLayoutManager(mLayoutManager);
        mContactsAdapter = new ContactsAdapter(mContacts, application.getApplicationContext());
        mContactsRecyclerView.setAdapter(mContactsAdapter);

        mMainFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate);
                mMainFab.setImageDrawable(getActivity().getDrawable(R.drawable.ic_message_white_18dp));
                mMainFab.startAnimation(animation);
            }
        });

/*        mFABselectall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContactsAdapter.setAllChecked(true);
            }
        });
        mFABdeselectall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContactsAdapter.setAllChecked(false);
            }
        });
        mFABsend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                composeMessage();
            }
        });*/
        return v;
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
