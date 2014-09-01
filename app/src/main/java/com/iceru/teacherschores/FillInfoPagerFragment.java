package com.iceru.teacherschores;

import android.app.ActionBar;
import android.app.Activity;
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

	public static FillInfoPagerFragment newInstance() {
		FillInfoPagerFragment fragment = new FillInfoPagerFragment();
		return fragment;
	}

    @Override
    public void onAttach(Activity activity) {
        Log.d(this.getClass().getSimpleName(), "onAttach()");
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(this.getClass().getSimpleName(), "onCreate()");
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(this.getClass().getSimpleName(), "onCreateView()");
        View rootView = (LinearLayout) inflater.inflate(R.layout.fragment_fillinfopager, container, false);
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
        return rootView;
    }

    @Override
    public void onActivityCreated (Bundle savedInstanceState) {
        Log.d(this.getClass().getSimpleName(), "onActivityCreated()");
        super.onActivityCreated(savedInstanceState);
        // Indicate that this fragment would like to influence the set of actions in the action bar.
        //setHasOptionsMenu(true);
    }

    @Override
    public void onStart() {
        Log.d(this.getClass().getSimpleName(), "onStart()");
        super.onStart();
    }

    @Override
    public void onResume() {
        Log.d(this.getClass().getSimpleName(), "onResume()");
        super.onResume();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Log.d(this.getClass().getSimpleName(), "onViewCreated()");
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d(this.getClass().getSimpleName(), "onSaveInstanceState()");
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        Log.d(this.getClass().getSimpleName(), "onPause()");
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.d(this.getClass().getSimpleName(), "onStop()");
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        Log.d(this.getClass().getSimpleName(), "onDestroyView()");
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        Log.d(this.getClass().getSimpleName(), "onDestory()");
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        Log.d(this.getClass().getSimpleName(), "onDetach()");
        super.onDetach();
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
