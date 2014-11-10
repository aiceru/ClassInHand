package com.iceru.teacherschores;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.view.ViewConfiguration;

import org.acra.annotation.ReportsCrashes;
import org.acra.sender.HttpSender;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;

@ReportsCrashes(
        formKey = "",
        httpMethod = HttpSender.Method.PUT,
        reportType = HttpSender.Type.JSON,
        formUri = "http://aiceru.iriscouch.com/acra-classinhand/_design/acra-storage/_update/report",
        formUriBasicAuthLogin = "tester_classinhand",
        formUriBasicAuthPassword = "classinhandTester"
)
public class MainActivity extends ActionBarActivity {
        //implements NavigationDrawerFragment.NavigationDrawerCallbacks {
	private TreeMap<Integer, Student> mStudents;
	private ArrayList<Role> mRoles;
	private int num_boys, num_girls, num_roleConsume;
    private ClassDBHelper dbHelper;

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    //private NavigationDrawerFragment    mNavigationDrawerFragment;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    /**
     * Used to store the last screen title. For use in {link #restoreActionBar()}.
     */
    //private CharSequence mTitle;

	private SharedPreferences mShPrefStudentNameList;
	private SharedPreferences mShPrefStudentBoygirlList;
	private SharedPreferences mShPrefRoleList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // hack for permanentMenuKey (ex. galaxy series)
        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            if(menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (Exception ex) {
            // Ignore
        }
        // End hack

	    num_boys = 0;
	    num_girls = 0;
	    num_roleConsume = 0;

	    mStudents = new TreeMap<Integer, Student>();
	    mRoles = new ArrayList<Role>();

        dbHelper = new ClassDBHelper(this);
        Cursor c = dbHelper.getStudentsList();
        while(c.moveToNext()) {
            int id = c.getInt(c.getColumnIndexOrThrow(ClassDBContract.StudentInfo.COLUMN_NAME_STUDENT_ID));
            String name = c.getString(c.getColumnIndexOrThrow(ClassDBContract.StudentInfo.COLUMN_NAME_STUDENT_NAME));
            boolean isBoy = (c.getInt(c.getColumnIndexOrThrow(ClassDBContract.StudentInfo.COLUMN_NAME_STUDENT_GENDER)) == 1);
	        Student temp = new Student(id, name, isBoy);
            mStudents.put(id, temp);//new Student(id, name, isBoy));
            if(isBoy) num_boys++;
            else num_girls++;
        }
        c.close();

	    mShPrefStudentNameList = getSharedPreferences(getString(
                R.string.sharedpref_student_name_list), Context.MODE_PRIVATE);
	    mShPrefStudentBoygirlList = getSharedPreferences(getString(
                R.string.sharedpref_boygirl_list), Context.MODE_PRIVATE);
	    mShPrefRoleList = getSharedPreferences(getString(
			    R.string.sharedpref_role_list), Context.MODE_PRIVATE);

	    /*Map<String, ?> allEntries = mShPrefStudentNameList.getAll();
	    for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
		    Student student = new Student(Integer.valueOf(entry.getKey()),
				    entry.getValue().toString(),
				    mShPrefStudentBoygirlList.getBoolean(entry.getKey(), true));
		    mStudents.add(student);
		    if(student.isBoy()) num_boys++;
		    else num_girls++;
	    }*/

        Map<String, ?> allEntries = mShPrefRoleList.getAll();
	    for(Map.Entry<String, ?> entry : allEntries.entrySet()) {
		    Role role = new Role(0,
				    entry.getKey().toString(),
				    Integer.valueOf(entry.getValue().toString()));
		    mRoles.add(role);
		    num_roleConsume += role.getConsume();
	    }

        //mNavigationDrawerFragment = (NavigationDrawerFragment)
        //        getFragmentManager().findFragmentById(R.id.navigation_drawer);

	    //mTitle = getTitle();

        // Set up the drawer.
        //mNavigationDrawerFragment.setUp(
        //        R.id.navigation_drawer,
        //        (DrawerLayout) findViewById(R.id.drawer_layout));
        setContentView(R.layout.activity_main);
        initViews();
    }

    private void initViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);


        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        setSupportActionBar(toolbar);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar,
                R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                //getActionBar().setTitle(mTitle);
                //invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                //getActionBar().setTitle(mDrawerTitle);
                //invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };


        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

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

    public ClassDBHelper getDbHelper() {
        return dbHelper;
    }

    public int getNum_boys() {
		return num_boys;
	}

	public int getNum_girls() {
		return num_girls;
	}

	public int getNum_roleConsume() {
		return num_roleConsume;
	}

	public TreeMap<Integer, Student> getmStudents() {
		return mStudents;
	}

    public void setmStudents(TreeMap<Integer, Student> newStudents) {
        this.mStudents = newStudents;
    }

	public ArrayList<Role> getmRoles() {
		return mRoles;
	}

	public boolean addStudent(Student student) {
		boolean exist = null != mStudents.get(student.getNum());
		if(!exist) {
			mStudents.put(student.getNum(), student);
			if(student.isBoy()) num_boys++;
			else num_girls++;
            dbHelper.insert(student);
		}
		return !exist;
	}

	public boolean removeStudent(Student student) {
		boolean success = null != mStudents.remove(student.getNum());
		if(success) {
			if(student.isBoy()) num_boys--;
			else num_girls--;
			dbHelper.delete(student);
		}
		return success;
	}

    public void removeAllStudents() {
        mStudents.clear();
        num_boys = 0;
        num_girls = 0;
        dbHelper.deleteAllStudents();
    }

	public boolean addRole(Role role) {
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
