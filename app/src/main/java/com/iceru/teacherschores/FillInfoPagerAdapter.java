package com.iceru.teacherschores;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;

/**
 * Created by iceru on 14. 8. 9.
 */
public class FillInfoPagerAdapter extends FragmentPagerAdapter{
	private FillStudentInfoFragment mFillStudentInfoFragment;

	public FillInfoPagerAdapter(FragmentManager fm) {
		super(fm);
		mFillStudentInfoFragment = FillStudentInfoFragment.newInstance();
	}

	@Override
	public Fragment getItem(int i) {
		switch(i) {
			case 1:
				return mFillStudentInfoFragment;
			case 2:
				break;
		}
		return null;
	}

	@Override
	public int getCount() {
		return 2;
	}
}
