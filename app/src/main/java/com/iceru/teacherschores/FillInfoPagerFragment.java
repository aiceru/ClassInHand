package com.iceru.teacherschores;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by iceru on 14. 8. 9.
 */
public class FillInfoPagerFragment extends Fragment {
	//private FillInfoPagerAdapter mFillInfoPagerAdapter;
	//private ViewPager mViewPager;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//mFillInfoPagerAdapter = new FillInfoPagerAdapter(getChildFragmentManager());
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		ViewPager mViewPager = (ViewPager) view.findViewById(R.id.viewpager_fillinfo);
		mViewPager.setAdapter(new FillInfoPagerAdapter(getChildFragmentManager()));
	}

	/*@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		View view = inflater.inflate(R.layout.fragment_fillinfopager, container, false);
		mViewPager = (ViewPager) view.findViewById(R.id.viewpager_fillinfo);
		mViewPager.setAdapter(mFillInfoPagerAdapter);

		return view;
	}*/
}
