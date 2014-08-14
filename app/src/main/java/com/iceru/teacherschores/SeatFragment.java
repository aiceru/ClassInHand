package com.iceru.teacherschores;

import android.app.ActionBar;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;

/**
 * Created by iceru on 14. 8. 14.
 */
public class SeatFragment extends Fragment {
	MainActivity mainActivity;
	TreeSet<Student> tempStudents;
	TreeSet<Seat> tempSeats;

	private class segAdapter extends BaseAdapter {
		private LayoutInflater mInflater;
		private Context mContext;
		private TreeSet<Seat> mItems;

		public segAdapter(Context context, TreeSet<Seat> object) {
			mContext = context;
			mItems = object;
			mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		@Override
		public int getCount() {
			return mItems.size();
		}

		@Override
		public Object getItem(int position) {
			Seat st = null;
			Iterator<Seat> i = mItems.iterator();
			while(i.hasNext() && position >= 0) {
				position--;
				st = i.next();
			}
			return st;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view;
			if(convertView == null) {
				view = mInflater.inflate(R.layout.seat, null);
			}
			else {
				view = convertView;
			}

			final Seat seat = (Seat)this.getItem(position);

			if(seat != null) {
				TextView tvNum = (TextView)view.findViewById(R.id.textview_seated_num);
				TextView tvName = (TextView)view.findViewById(R.id.textview_seated_name);
				tvNum.setText(String.valueOf(seat.getItsStudent().getNum()));
				tvName.setText(seat.getItsStudent().getName());
			}

			return view;
		}
	}

	public static SeatFragment newInstance() {
		SeatFragment fragment = new SeatFragment();
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mainActivity = (MainActivity)getActivity();

		tempStudents = mainActivity.getmStudents();
		tempSeats = new TreeSet<Seat>(new Comparator<Seat>() {
			@Override
			public int compare(Seat lhs, Seat rhs) {
				return lhs.getId() - rhs.getId();
			}
		});

		Student st = null;
		Seat seat = null;
		int seatId = 0;
		Iterator<Student> i = tempStudents.iterator();
		while(i.hasNext()) {
			st = i.next();
			seatId++;
			seat = new Seat(seatId, st);
			tempSeats.add(seat);
		}
	}

	private ActionBar getActionBar() {
		return getActivity().getActionBar();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setHasOptionsMenu(true);
	}

	public void showActionBar() {
		ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(R.string.title_seatplan);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		if (!mainActivity.isDrawerOpen()) {
			inflater.inflate(R.menu.menu_fillinfo, menu);
			showActionBar();
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootview = inflater.inflate(R.layout.fragment_seat, container, false);

		GridView seg1view = (GridView)rootview.findViewById(R.id.gridview_sep1);
		seg1view.setAdapter(new segAdapter(mainActivity, tempSeats));

		return rootview;
	}
}
