package com.iceru.teacherschores;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeSet;

public class MainActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {
	private TreeSet<Student> mStudents;
	private ArrayList<Role> mRoles;
	private int num_boys, num_girls, num_roleConsume;

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment    mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

	private SharedPreferences mShPrefStudentNameList;
	private SharedPreferences mShPrefStudentBoygirlList;
	private SharedPreferences mShPrefRoleList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

	    num_boys = 0;
	    num_girls = 0;
	    num_roleConsume = 0;

	    mStudents = new TreeSet<Student>(new Comparator<Student>() {
		    @Override
		    public int compare(Student lhs, Student rhs) {
			    return lhs.getNum() - rhs.getNum();
		    }
	    });
	    mRoles = new ArrayList<Role>();

	    mShPrefStudentNameList = getSharedPreferences(getString(
			    R.string.sharedpref_student_name_list), Context.MODE_PRIVATE);
	    mShPrefStudentBoygirlList = getSharedPreferences(getString(
			    R.string.sharedpref_boygirl_list), Context.MODE_PRIVATE);
	    mShPrefRoleList = getSharedPreferences(getString(
			    R.string.sharedpref_role_list), Context.MODE_PRIVATE);

	    Map<String, ?> allEntries = mShPrefStudentNameList.getAll();
	    for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
		    Student student = new Student(Integer.valueOf(entry.getKey()),
				    entry.getValue().toString(),
				    mShPrefStudentBoygirlList.getBoolean(entry.getKey(), true));
		    mStudents.add(student);
		    if(student.isBoy()) num_boys++;
		    else num_girls++;
	    }

	    allEntries = mShPrefRoleList.getAll();
	    for(Map.Entry<String, ?> entry : allEntries.entrySet()) {
		    Role role = new Role(0,
				    entry.getKey().toString(),
				    Integer.valueOf(entry.getValue().toString()));
		    mRoles.add(role);
		    num_roleConsume += role.getConsume();
	    }

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);

	    mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getFragmentManager();
        switch(position+1) {
	        case 1:
		        fragmentManager.beginTransaction().replace(R.id.main_contents, SeatFragment.newInstance()).commit();
		        break;
	        case 5:
	            fragmentManager.beginTransaction().replace(R.id.main_contents, FillInfoPagerFragment.newInstance()).commit();
	            break;
	        default:
	            break;
        }
    }
    
    public boolean isDrawerOpen() {
    	return mNavigationDrawerFragment.isDrawerOpen();
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

	public TreeSet<Student> getmStudents() {
		return mStudents;
	}

	public ArrayList<Role> getmRoles() {
		return mRoles;
	}

	public boolean addStudent(Student student) {
		boolean success = mStudents.add(student);
		if(success) {
			if(student.isBoy()) num_boys++;
			else num_girls++;

			SharedPreferences.Editor editor = mShPrefStudentNameList.edit();
			editor.putString(String.valueOf(student.getNum()), student.getName());
			editor.apply();
			editor = mShPrefStudentBoygirlList.edit();
			editor.putBoolean(String.valueOf(student.getNum()), student.isBoy());
			editor.apply();
		}
		return success;
	}

	public boolean removeStudent(Student student) {
		boolean success = mStudents.remove(student);
		if(success) {
			if(student.isBoy()) num_boys--;
			else num_girls--;

			SharedPreferences.Editor editor = mShPrefStudentNameList.edit();
			editor.remove(String.valueOf(student.getNum()));
			editor.apply();
			editor = mShPrefStudentBoygirlList.edit();
			editor.remove(String.valueOf(student.getNum()));
			editor.apply();
		}
		return success;
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

    
/*
    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


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
