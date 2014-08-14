package com.iceru.teacherschores;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by iceru on 14. 8. 9.
 */
public class FillInfoPagerFragment extends Fragment {

	public static FillInfoPagerFragment newInstance() {
		FillInfoPagerFragment fragment = new FillInfoPagerFragment();
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	private ActionBar getActionBar() {
        return getActivity().getActionBar();
    }

    @Override
    public void onActivityCreated (Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Indicate that this fragment would like to influence the set of actions in the action bar.
        setHasOptionsMenu(true);
    }

    public void showActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(R.string.title_fillinfo);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (!((MainActivity)getActivity()).isDrawerOpen()) {
            inflater.inflate(R.menu.menu_fillinfo, menu);
            showActionBar();
        }
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ActionBar actionBar = getActionBar();

		final ViewPager mViewPager = (ViewPager)inflater.inflate(R.layout.fragment_fillinfopager, container, false);
		mViewPager.setAdapter(new FillInfoPagerAdapter(getActivity(), getChildFragmentManager()));

		mViewPager.setOnPageChangeListener(
				new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						// When swiping between pages, select the
						// corresponding tab.
						getActionBar().setSelectedNavigationItem(position);
					}
				});

		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		ActionBar.TabListener tabListener = new ActionBar.TabListener() {
			public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
				// show the given tab
				mViewPager.setCurrentItem(tab.getPosition());
			}

			public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
				// hide the given tab
			}

			public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
				// probably ignore this event
			}
		};

		actionBar.removeAllTabs();
		actionBar.addTab(actionBar.newTab().setText(R.string.tabtitle_studentinfo).setTabListener(tabListener));
		actionBar.addTab(actionBar.newTab().setText(R.string.tabtitle_roleinfo).setTabListener(tabListener));
		return mViewPager;
	}
}
