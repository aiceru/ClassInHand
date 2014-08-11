package com.iceru.teacherschores;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.support.v13.app.FragmentPagerAdapter;

/**
 * Created by iceru on 14. 8. 9.
 */
public class FillInfoPagerAdapter extends FragmentPagerAdapter{
	private FillStudentInfoFragment mFillStudentInfoFragment;
	private FillRoleInfoFragment mFillRoleInfoFragment;
	private Context mContext;

	public FillInfoPagerAdapter(Context context, FragmentManager fm) {
		super(fm);
		mContext = context;
		mFillStudentInfoFragment = FillStudentInfoFragment.newInstance();
		mFillRoleInfoFragment = FillRoleInfoFragment.newInstance();
	}

	@Override
	public Fragment getItem(int i) {
		switch(i) {
			case 0:
				return mFillStudentInfoFragment;
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
}
