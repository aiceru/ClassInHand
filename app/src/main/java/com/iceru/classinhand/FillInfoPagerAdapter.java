package com.iceru.classinhand;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;

import java.util.List;

/**
 * Created by iceru on 14. 8. 9.
 */
public class FillInfoPagerAdapter extends FragmentPagerAdapter{
	private FillRoleInfoFragment mFillRoleInfoFragment;
    private StudentListFragment mStudentListFragment;
	private Context mContext;

	public FillInfoPagerAdapter(Context context, FragmentManager fm) {
		super(fm);
		mContext = context;
		//mFillStudentInfoFragment = FillStudentInfoFragment.newInstance();
        mStudentListFragment = StudentListFragment.newInstance();
        mFillRoleInfoFragment = FillRoleInfoFragment.newInstance();
    }

	@Override
	public Fragment getItem(int i) {
		switch(i) {
			case 0:
				return mStudentListFragment;
			case 1:
				return mFillRoleInfoFragment;
		}
		return null;
	}

    @Override
	public int getCount() {
		return 2;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		switch(position) {
			case 0:
				return mContext.getString(R.string.tabtitle_studentinfo);
			case 1:
				return mContext.getString(R.string.tabtitle_roleinfo);
		}
		return null;
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		mStudentListFragment.onActivityResult(requestCode, resultCode, data);
		mFillRoleInfoFragment.onActivityResult(requestCode, resultCode, data);
	}
}
