package com.iceru.classinhand;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getName();

    private ClassInHandApplication application;
	private ArrayList<Role> mRoles;
	private int num_roleConsume;

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private DrawerLayout            mDrawerLayout;
    private ListView                mDrawerListView;
    private List<DrawerContent>     mDrawerList;
    private ActionBarDrawerToggle   mDrawerToggle;
    private RelativeLayout          mMainLayout;

    private int     mCurrentSelectedPosition = 0;
    private String  mDrawerTitle;
    private String  mTitle;

    private boolean mFromSavedInstanceState = false;
    private boolean mUserLearnedDrawer = false;
    private boolean mFirstShowcaseShown = false;

    private GregorianCalendar mNewDate;
    private GregorianCalendar mOldDate;

    /**
     * Used to store the last screen title. For use in {link #restoreActionBar()}.
     */
    //private CharSequence mTitle;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //super.onSaveInstanceState(outState);
        outState.putInt(ClassInHandApplication.STATE_SELECTED_POSITION, mCurrentSelectedPosition);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        application = ClassInHandApplication.getInstance();

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        mUserLearnedDrawer = sp.getBoolean(ClassInHandApplication.PREF_USER_LEARNED_DRAWER, false);
        mFirstShowcaseShown = sp.getBoolean(ClassInHandApplication.PREF_FIRST_SHOWCASE, false);

        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(ClassInHandApplication.STATE_SELECTED_POSITION);
            mFromSavedInstanceState = true;
        }
        mDrawerTitle = getString(R.string.app_name);

        setContentView(R.layout.activity_main);
        initViews();
    }

    private void initViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerListView = (ListView) findViewById(R.id.navigation_drawer);
        mMainLayout = (RelativeLayout) findViewById(R.id.relativelayout_main);

        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        setSupportActionBar(toolbar);

        mDrawerList = new ArrayList<>();
        mDrawerList.add(DrawerItem.create(101, getString(R.string.title_seatplan), "ic_grid_on_grey600_24dp", true, this));
        mDrawerList.add(DrawerItem.create(102, getString(R.string.title_message), "ic_message_grey_600_24dp", true, this));
        mDrawerList.add(DrawerSection.create(200, "Settings"));
        mDrawerList.add(DrawerSubItem.create(201, getString(R.string.title_fillinfo), "ic_edit_grey600_24dp", true, this));
        mDrawerList.add(DrawerSubItem.create(301, getString(R.string.title_setting), "ic_settings_grey600_24dp", true, this));

        // DEBUG 빌드에만 포함!
        if(BuildConfig.DEBUG) {
            mDrawerList.add(DrawerSubItem.create(901, "DB추출(개발자용)", "ic_settings_grey600_24dp", false, this));
            mDrawerList.add(DrawerSubItem.create(902, "DB삭제(개발자용)", "ic_settings_grey600_24dp", false, this));
            mDrawerList.add(DrawerSubItem.create(903, "TestDB생성(개발자용)", "ic_settings_grey600_24dp", false, this));
        }

        mDrawerListView.setAdapter(new DrawerContentAdapter(getSupportActionBar().getThemedContext(), R.layout.drawer_item, mDrawerList));
        mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
            }
        });

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar,
                R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                //mDrawerToggle.syncState();
                getSupportActionBar().setTitle(mTitle);
                //invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle(mDrawerTitle);
            }
        };

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mDrawerToggle.syncState();
    }

    private void selectItem(int position) {
        if (mDrawerListView != null) {
            mDrawerListView.setItemChecked(position, false);
        }
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mDrawerListView);
        }
        if (!mUserLearnedDrawer) {
            mUserLearnedDrawer = true;
            SharedPreferences sp = PreferenceManager
                    .getDefaultSharedPreferences(getApplicationContext());
            sp.edit().putBoolean(ClassInHandApplication.PREF_USER_LEARNED_DRAWER, true).apply();
        }
        mCurrentSelectedPosition = position;

        FragmentManager fragmentManager = getSupportFragmentManager();
        // 2015. 4. 22. wooseok.
        // commit() must be executed before onSaveInstanceState(), so use commitAllowingStateLoss().
        // http://www.kmshack.kr/fragment-%ED%8C%8C%ED%97%A4%EC%B9%98%EA%B8%B0-3-fragmentmanager-fragmenttransaction%EC%97%90-%EB%8C%80%ED%95%B4%EC%84%9C/
        switch(position+1) {
            case 1:
                mTitle = getString(R.string.title_seatplan);
                fragmentManager.beginTransaction().replace(R.id.main_contents, SeatplansFragment.getInstance()).commitAllowingStateLoss();
                break;
            case 2:
                mTitle = getString(R.string.title_message);
                fragmentManager.beginTransaction().replace(R.id.main_contents, MessageFragment.getInstance()).commitAllowingStateLoss();
                break;
            case 4:
                mTitle = getString(R.string.title_fillinfo);
                fragmentManager.beginTransaction().replace(R.id.main_contents, FillInfoPagerFragment.getInstance()).commitAllowingStateLoss();
                break;
            case 5:
                mTitle = getString(R.string.title_setting);
                fragmentManager.beginTransaction().replace(R.id.main_contents, SettingsFragment.getInstance()).commitAllowingStateLoss();
                break;
            case 6:
                exportDB();
                break;
            case 7:
                ClassInHandApplication.getInstance().removeAllStudents();
                ClassInHandApplication.getInstance().removeAllSeatplans();
                break;
            case 8:
                ClassInHandApplication.getInstance().createTestData();
                break;
            default:
                break;
        }
        getSupportActionBar().setTitle(mTitle);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
            mDrawerLayout.openDrawer(mDrawerListView);
        } else selectItem(mCurrentSelectedPosition);
        if (!mFirstShowcaseShown) {
            mDrawerListView.post(new Runnable() {
                @Override
                public void run() {
                    View v = mDrawerListView.getChildAt(3);
                    int[] location = new int[2];
                    int[] size = new int[2];
                    v.getLocationOnScreen(location);
                    size[0] = v.getMeasuredWidth();
                    size[1] = v.getMeasuredHeight();
                    Intent showcaseIntent = new Intent(getApplicationContext(), ShowcaseActivity.class);
                    showcaseIntent.putExtra(ClassInHandApplication.SHOWCASE_TARGET_POSITION, location);
                    showcaseIntent.putExtra(ClassInHandApplication.SHOWCASE_TARGET_SIZE, size);
                    showcaseIntent.putExtra(ClassInHandApplication.SHOWCASE_MESSAGE, R.string.welcome_input_students_info);
                    startActivityForResult(showcaseIntent, ClassInHandApplication.REQUESTCODE_FIRST_SHOWCASE);
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode >> 16 != 0x0) { // this result is from fragment... maybe
            // notifying nested fragments (support library bug fix)
            final FragmentManager childFragmentManager = this.getSupportFragmentManager();

            if (childFragmentManager != null) {
                final List<Fragment> nestedFragments = childFragmentManager.getFragments();

                if (nestedFragments == null || nestedFragments.size() == 0) return;

                for (Fragment childFragment : nestedFragments) {
                    if (childFragment != null && !childFragment.isDetached() && !childFragment.isRemoving()) {
                        childFragment.onActivityResult(requestCode, resultCode, data);
                    }
                }
            }
        }
        else {
            switch (requestCode) {
                case ClassInHandApplication.REQUESTCODE_FIRST_SHOWCASE:
                    this.mFirstShowcaseShown = true;
                    SharedPreferences sp = PreferenceManager
                            .getDefaultSharedPreferences(getApplicationContext());
                    sp.edit().putBoolean(ClassInHandApplication.PREF_FIRST_SHOWCASE, true).apply();
                    break;
                default:
                    break;
            }
        }
    }

    /*@Override
    public void onBackPressed() {
        if(mFragmentDepth > 1) {
            getFragmentManager().popBackStackImmediate();
            mFragmentDepth--;
            getSupportActionBar().setTitle(mTitle);
        }
        else {
            super.onBackPressed();
        }
    }*/

    /*@Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getFragmentManager();
        switch(position+1) {
	        case 1:
		        fragmentManager.beginTransaction().replace(R.id.main_contents, SeatFragment.newInstance()).commit();
		        break;
	        case 3:
	            fragmentManager.beginTransaction().replace(R.id.main_contents, FillInfoPagerFragment.newInstance()).commit();
	            break;
	        default:
	            break;
        }
    }*/
    
    //public boolean isDrawerOpen() {
    //	return mNavigationDrawerFragment.isDrawerOpen();
    //}

    /*public ClassDBHelper getDbHelper() {
        return dbHelper;
    }*/

	public int getNum_roleConsume() {
		return num_roleConsume;
	}

	public ArrayList<Role> getmRoles() {
		return mRoles;
	}

	/*public boolean addRole(Role role) {
		boolean success = mRoles.add(role);
		if(success) {
			num_roleConsume += role.getConsume();

			SharedPreferences.Editor editor = mShPrefRoleList.edit();
			editor.putInt(role.getName(), role.getConsume());
			editor.apply();
		}
		return success;
	}

	public boolean removeRole(Role role) {
		boolean success = mRoles.remove(role);
		if(success) {
			num_roleConsume -= role.getConsume();

			SharedPreferences.Editor editor = mShPrefRoleList.edit();
			editor.remove(role.getName());
			editor.apply();
		}
		return success;
	}*/

    public void onClickNewPersonButton(View view) {
        Intent intent = new Intent(this, StudentAddActivity.class);
        startActivity(intent);
    }

    public void onClickNewPlanButton(View view) {
        mNewDate = application.getValueOfTodayCalendar();

        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                if(view.isShown()) {
                    mNewDate.clear();
                    mNewDate.set(year, monthOfYear, dayOfMonth);
                    if (application.findSeatplan(mNewDate) != null) {
                        confirmOverwrite();
                    } else {
                        runSeatplanActivity();
                    }
                }
            }

        };
        DatePickerDialog dateDialog = new DatePickerDialog(this, R.style.dialog_style,
                dateSetListener, mNewDate.get(Calendar.YEAR), mNewDate.get(Calendar.MONTH),
                mNewDate.get(Calendar.DAY_OF_MONTH));
        dateDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                return;
            }
        });
        dateDialog.show();
    }

    private void confirmOverwrite() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.dialog_style);
        builder.setTitle(R.string.title_dialog_warning);
        builder.setMessage(R.string.contents_dialog_seatplan_already_exists);
        builder.setPositiveButton(R.string.action_overwrite, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                mOldDate = mNewDate;
                runSeatplanActivity();
            }
        });
        builder.setNegativeButton(R.string.action_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog, Do nothing
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void runSeatplanActivity() {
        Intent intent = new Intent(this, SeatplanEditActivity.class);
        intent.putExtra(ClassInHandApplication.SEATPLAN_EDIT_NEWDATE, mNewDate.getTimeInMillis());
        intent.putExtra(ClassInHandApplication.SEATPLAN_EDIT_OLDDATE,
                mOldDate == null? 0 : mOldDate.getTimeInMillis());
        startActivity(intent);
    }

    

/*    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }*/

/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            //getMenuInflater().inflate(R.menu.menu_fillinfo, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }
    */

    private void exportDB() {
        File sd = Environment.getExternalStorageDirectory();
        File data = Environment.getDataDirectory();
        FileChannel source=null;
        FileChannel destination=null;
        String currentDBPath = "/data/"+ "com.iceru.classinhand" +"/databases/"+"Class.db";
        String backupDBPath = "Class.db";
        File currentDB = new File(data, currentDBPath);
        File backupDB = new File(sd, backupDBPath);
        try {
            source = new FileInputStream(currentDB).getChannel();
            destination = new FileOutputStream(backupDB).getChannel();
            destination.transferFrom(source, 0, source.size());
            source.close();
            destination.close();
            Toast.makeText(this, "DB Exported!", Toast.LENGTH_LONG).show();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}
