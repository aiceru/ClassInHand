package com.iceru.teacherschores;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.support.v13.app.FragmentPagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
/*
    *//**
     * Instantiate the {@link View} which should be displayed at {@code position}. Here we
     * inflate a layout from the apps resources and then change the text view to signify the position.
     *//*
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        // Inflate a new layout from our resources
        View view = getActivity().getLayoutInflater().inflate(R.layout.pager_item,
                container, false);
        // Add the newly created View to the ViewPager
        container.addView(view);

        // Retrieve a TextView from the inflated View, and update it's text
        TextView title = (TextView) view.findViewById(R.id.item_title);
        title.setText(String.valueOf(position + 1));

        Log.i(LOG_TAG, "instantiateItem() [position: " + position + "]");

        // Return the View
        return view;
    }

    *//**
     * Destroy the item from the {@link ViewPager}. In our case this is simply removing the
     * {@link View}.
     *//*
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
        Log.i(LOG_TAG, "destroyItem() [position: " + position + "]");
    }*/
}
