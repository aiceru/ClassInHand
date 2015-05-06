package com.iceru.classinhand;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.support.v4.app.Fragment;

import java.util.List;

/**
 * Created by iceru on 14. 8. 9.
 */
public class FillInfoPagerFragment extends Fragment {

    private static FillInfoPagerFragment   thisObject = null;
    private SlidingTabLayout    mSlidingTabLayout;
    private ViewPager           mViewPager;
    private FillInfoPagerAdapter mAdapter;

	public static FillInfoPagerFragment getInstance() {
        if(thisObject == null)
            thisObject = new FillInfoPagerFragment();
		return thisObject;
	}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_fillinfopager, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mAdapter = new FillInfoPagerAdapter(getActivity(), getChildFragmentManager());
        mViewPager = (ViewPager) view.findViewById(R.id.viewpager_fillinfo);
        mViewPager.setAdapter(mAdapter);

        mSlidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setSelectedIndicatorColors(getResources().getColor(R.color.accent));
        mSlidingTabLayout.setBackgroundColor(getResources().getColor(R.color.primary));
        mSlidingTabLayout.setViewPager(mViewPager);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mAdapter.onActivityResult(requestCode, resultCode, data);
    }
}
