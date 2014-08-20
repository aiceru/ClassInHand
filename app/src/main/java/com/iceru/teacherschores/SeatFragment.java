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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;

/**
 * Created by iceru on 14. 8. 14.
 */
public class SeatFragment extends Fragment {
	MainActivity        mainActivity;
	TreeSet<Student>    mStudents;
	//TreeSet<Seat>       tempSeats;
	ArrayList<Seat> mSegment1, mSegment2, mSegment3;

	int                 mTotalSeats;

	static private View rootView = null;

	private class segAdapter extends BaseAdapter {
		private LayoutInflater mInflater;
		private Context mContext;
		private ArrayList<Seat> mItems;

		public segAdapter(Context context, ArrayList<Seat> object) {
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
			/*Seat st = null;
			Iterator<Seat> i = mItems.iterator();
			while(i.hasNext() && position >= 0) {
				position--;
				st = i.next();
			}
			return st;*/
			return mItems.get(position);
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

			if(seat != null && seat.getItsStudent() != null) {
				TextView tvNum = (TextView)view.findViewById(R.id.textview_seated_num);
				TextView tvName = (TextView)view.findViewById(R.id.textview_seated_name);
				ImageView ivBoygirl = (ImageView)view.findViewById(R.id.imageview_seated_boygirl);
				tvNum.setText(String.valueOf(seat.getItsStudent().getNum()));
				tvName.setText(seat.getItsStudent().getName());
				ivBoygirl.setImageResource(seat.getItsStudent().isBoy()? R.drawable.ic_toggle_boy : R.drawable.ic_toggle_girl);
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

		mStudents = mainActivity.getmStudents();
		mTotalSeats = mainActivity.getNum_boys() + mainActivity.getNum_girls();
		int rows = (int)Math.ceil(((double)mTotalSeats) / (double)6.0);

		mSegment1 = new ArrayList<Seat>();
		mSegment2 = new ArrayList<Seat>();
		mSegment3 = new ArrayList<Seat>();
		/*mSegment3 = new TreeSet<Seat>(new Comparator<Seat>() {
			@Override
			public int compare(Seat lhs, Seat rhs) {
				return lhs.getId() - rhs.getId();
			}
		});*/
		for(int i = 0; i < mTotalSeats; i++) {
			Seat seat = new Seat(i);
			switch(seat.getId() % 6) {
				case 0:
				case 1:
					mSegment1.add(seat);
					break;
				case 2:
				case 3:
					mSegment2.add(seat);
					break;
				case 4:
				case 5:
					mSegment3.add(seat);
					break;
				default:
					break;
			}
		}

		assignRandom();
	}

	private void assignRandom() {
		boolean seatIsFull[] = new boolean[mTotalSeats];
		Student st = null;
		int seatId;
		Iterator<Student> i = mStudents.iterator();
		while(i.hasNext()) {
			st = i.next();
			do {
				seatId = (int)(Math.random() * mTotalSeats);
			} while(seatIsFull[seatId] == true);
			st.setItsCurrentSeat(getSeatByAbsolutePosition(seatId));
			seatIsFull[seatId] = true;
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
		if(rootView == null) {
			rootView = inflater.inflate(R.layout.fragment_seat, container, false);

			GridView gv_segment1 = (GridView) rootView.findViewById(R.id.gridview_segment1);
			GridView gv_segment2 = (GridView) rootView.findViewById(R.id.gridview_segment2);
			GridView gv_segment3 = (GridView) rootView.findViewById(R.id.gridview_segment3);
			gv_segment1.setAdapter(new segAdapter(mainActivity, mSegment1));
			gv_segment2.setAdapter(new segAdapter(mainActivity, mSegment2));
			gv_segment3.setAdapter(new segAdapter(mainActivity, mSegment3));
		}
		return rootView;
	}

	private Seat getSeatByAbsolutePosition(int seatId) {
		int mod = seatId % 6;
		int mod2 = mod % 2;
		int div = seatId / 6;
		ArrayList<Seat> seg = mSegment1;
		switch(mod) {
			case 0:
			case 1:
				seg = mSegment1;
				break;
			case 2:
			case 3:
				seg = mSegment2;
				break;
			case 4:
			case 5:
				seg = mSegment3;
				break;
			default:
				break;
		}
		return seg.get((div * 2) + (mod2));
	}
}
