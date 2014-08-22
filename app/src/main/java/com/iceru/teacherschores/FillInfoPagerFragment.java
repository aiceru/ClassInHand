package com.iceru.teacherschores;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.v13.app.FragmentTabHost;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TabHost;

/**
 * Created by iceru on 14. 8. 9.
 */
public class FillInfoPagerFragment extends Fragment {

	private View rootView = null;

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
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
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
		if(rootView == null) {
			rootView = (LinearLayout) inflater.inflate(R.layout.fragment_fillinfopager, container, false);
			final TabHost mTabHost = (TabHost) rootView.findViewById(R.id.tabhost_fillinfo);
			mTabHost.setup();

			TabHost.TabSpec mTabSpec1 = mTabHost.newTabSpec("studentinfo").setIndicator("학생정보");
			TabHost.TabSpec mTabSpec2 = mTabHost.newTabSpec("roleinfo").setIndicator("역할정보");

			mTabSpec1.setContent(new TabFactory(getActivity()));
			mTabSpec2.setContent(new TabFactory(getActivity()));

			mTabHost.addTab(mTabSpec1);
			mTabHost.addTab(mTabSpec2);

			final ViewPager mViewPager = (ViewPager) rootView.findViewById(R.id.viewpager_fillinfo);
			mViewPager.setAdapter(new FillInfoPagerAdapter(getActivity(), getChildFragmentManager()));

			mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
				@Override
				public void onTabChanged(String tabId) {
					int pos = mTabHost.getCurrentTab();
					mViewPager.setCurrentItem(pos);
				}
			});

			mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
				@Override
				public void onPageSelected(int position) {
					mTabHost.setCurrentTab(position);
				}
			});
		}
		return rootView;
	}

	private class TabFactory implements TabHost.TabContentFactory {
		private final Context mContext;

		public TabFactory(Context context) {
			mContext = context;
		}

		public View createTabContent(String tag) {
			View v = new View(mContext);
			v.setMinimumWidth(0);
			v.setMinimumHeight(0);
			return v;
		}
	}
}
