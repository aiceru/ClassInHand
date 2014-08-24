package com.iceru.teacherschores;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.TreeSet;

/**
 * Created by iceru on 14. 8. 14.
 */
public class SeatFragment extends Fragment {
	private MainActivity        mainActivity;
	private TreeSet<Student>    mStudents;
	private ArrayList<Seat>     mSegment1, mSegment2, mSegment3;
	private segAdapter          mSegAdpt1, mSegAdpt2, mSegAdpt3;
    private boolean             mEditMode = false;

	/* View variables */
	private GridView    gv_segment1;
	private GridView    gv_segment2;
	private GridView    gv_segment3;

	private TextView    tv_curDate;
	private Menu        mMenu;
	private Button      btn_shuffle;

	int                 mTotalSeats;
	int                 mBoysSeats;
	int                 mGirlsSeats;

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
    public void onAttach(Activity activity) {
        Log.d(this.getClass().getSimpleName(), "onAttach()");
        super.onAttach(activity);
    }

    @Override
	public void onCreate(Bundle savedInstanceState) {
        Log.d(this.getClass().getSimpleName(), "onCreate()");
		super.onCreate(savedInstanceState);

		mainActivity = (MainActivity)getActivity();

		mStudents = mainActivity.getmStudents();
		mBoysSeats = mainActivity.getNum_boys();
		mGirlsSeats = mainActivity.getNum_girls();
		mTotalSeats = mBoysSeats + mGirlsSeats;

		mSegment1 = new ArrayList<Seat>();
		mSegment2 = new ArrayList<Seat>();
		mSegment3 = new ArrayList<Seat>();

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

		//assignRandom();
	}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(this.getClass().getSimpleName(), "onCreateView()");
        View rootView = inflater.inflate(R.layout.fragment_seat, container, false);

	    tv_curDate = (TextView) rootView.findViewById(R.id.textview_current_month);
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy. MM. dd");
	    tv_curDate.setText(sdf.format(new Date()));

        gv_segment1 = (GridView) rootView.findViewById(R.id.gridview_segment1);
        gv_segment2 = (GridView) rootView.findViewById(R.id.gridview_segment2);
        gv_segment3 = (GridView) rootView.findViewById(R.id.gridview_segment3);

	    mSegAdpt1 = new segAdapter(mainActivity, mSegment1);
	    mSegAdpt2 = new segAdapter(mainActivity, mSegment2);
	    mSegAdpt3 = new segAdapter(mainActivity, mSegment3);
        gv_segment1.setAdapter(mSegAdpt1);
        gv_segment2.setAdapter(mSegAdpt2);
        gv_segment3.setAdapter(mSegAdpt3);

	    btn_shuffle = (Button)rootView.findViewById(R.id.btn_random_assign);
        btn_shuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                assignRandom();
            }
        });
	    btn_shuffle.setVisibility(View.INVISIBLE);
        return rootView;
    }

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
        Log.d(this.getClass().getSimpleName(), "onActivityCreated()");
		super.onActivityCreated(savedInstanceState);
		setHasOptionsMenu(true);
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
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.d(this.getClass().getSimpleName(), "onCreateOptionMenu()");
		if (!mainActivity.isDrawerOpen()) {
            if(mEditMode) {
                menu.clear();
                inflater.inflate(R.menu.menu_seatplan_new, menu);
                showActionBar();
            }
            else {
                menu.clear();
                inflater.inflate(R.menu.menu_seatplan, menu);
                showActionBar();
            }
		}
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
        Log.d(this.getClass().getSimpleName(), "onDestroy()");
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        Log.d(this.getClass().getSimpleName(), "onDetach()");
        super.onDetach();
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
		if(id == R.id.seatplan_new) {
            createNewPlan();
            return true;
        }
        else if(id == R.id.seatplan_done) {
            saveCurrentPlan();
            return true;
		}
        else if(id == R.id.seatplan_cancel) {
            mEditMode = false;
            getActivity().invalidateOptionsMenu();
            return true;
        }
		return super.onOptionsItemSelected(item);
	}

	private ActionBar getActionBar() {
        return getActivity().getActionBar();
    }

    public void showActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(R.string.title_seatplan);
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

	private Seat getSeatBySegAndRow(int seg, int row, boolean isBoy) {
		ArrayList<Seat> segment = null;
		switch(seg) {
			case 1:
				segment = mSegment1;
				break;
			case 2:
				segment = mSegment2;
				break;
			case 3:
				segment = mSegment3;
				break;
			default:
				break;
		}
		return isBoy? (segment.get((row-1) * 2 + 1)) : (segment.get((row-1) * 2));
	}

    private void assignRandom() {
	    Log.d(this.getClass().getSimpleName(), "assignRandom()");
        boolean seatIsFull[] = new boolean[mTotalSeats];
        Student st = null;
        int seatId;
        boolean boys_are_more = (mBoysSeats > mGirlsSeats);
        int diff_seats = Math.abs(mBoysSeats - mGirlsSeats);
        Iterator<Student> i = mStudents.iterator();
        while(i.hasNext()) {
            st = i.next();
            do {
                //seatId = (int)(Math.random() * mTotalSeats);
                if(boys_are_more == st.isBoy()) {   // 많은쪽
                    seatId = (int)(Math.random() * mTotalSeats);
                    if(seatId < mTotalSeats - diff_seats) {
                        seatId = st.isBoy()? seatId - (seatId % 2) : seatId - (seatId % 2) + 1;
                    }
                }
                else {                              // 적은쪽
                    seatId = (int)(Math.random() * (mTotalSeats - diff_seats));
                    seatId = st.isBoy()? seatId - (seatId % 2) : seatId - (seatId % 2) + 1;
                }
            } while(seatIsFull[seatId] == true);
            st.setItsCurrentSeat(getSeatByAbsolutePosition(seatId));
            seatIsFull[seatId] = true;
        }
	    mSegAdpt1.notifyDataSetChanged();
	    mSegAdpt2.notifyDataSetChanged();
	    mSegAdpt3.notifyDataSetChanged();
    }

	private void createNewPlan() {
        mEditMode = true;
        getActivity().invalidateOptionsMenu();
		tv_curDate.setVisibility(View.INVISIBLE);

		btn_shuffle.setVisibility(View.VISIBLE);
	}

    private void saveCurrentPlan() {
        // TODO : check if all students have his/her own seat
        // TODO : save seat plan to DataBase
        mEditMode = false;
        getActivity().invalidateOptionsMenu();
        tv_curDate.setVisibility(View.VISIBLE);

        btn_shuffle.setVisibility(View.INVISIBLE);
    }
}
