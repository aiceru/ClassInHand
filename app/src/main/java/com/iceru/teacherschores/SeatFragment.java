package com.iceru.teacherschores;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
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
import android.widget.DatePicker;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TreeMap;

/**
 * Created by iceru on 14. 8. 14.
 */
public class SeatFragment extends Fragment {
	private MainActivity        mainActivity;
	private TreeMap<Integer, Student> mStudents;
	private ArrayList<Seat>     mSegment1, mSegment2, mSegment3;
	private segAdapter          mSegAdpt1, mSegAdpt2, mSegAdpt3;
    private boolean             mEditMode = false;
    private ArrayList<Long>     mSavedDateList;

    /* 새 자리 배치, 배치 수정 등에 쓰일 임시 리스트들 (원본 복제해서 작업 후 save or discard) */
    private TreeMap<Integer, Student>   mNewStudents = null;
    private ArrayList<Seat>             mNewSegment1 = null, mNewSegment2 = null, mNewSegment3 = null;
    private segAdapter                  mNewSegAdpt1 = null, mNewSegAdpt2 = null, mNewSegAdpt3 = null;

	/* View variables */
	private GridView    gv_segment1;
	private GridView    gv_segment2;
	private GridView    gv_segment3;

	private TextView    tv_curDate;
	private Button      btn_shuffle;

	private int         mTotalSeats, mBoysSeats, mGirlsSeats;
    private GregorianCalendar recentSavedCal;

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

			if(seat != null) {
                TextView tvNum = (TextView) view.findViewById(R.id.textview_seated_num);
                TextView tvName = (TextView) view.findViewById(R.id.textview_seated_name);
                ImageView ivBoygirl = (ImageView) view.findViewById(R.id.imageview_seated_boygirl);
                if(seat.getItsStudent() != null) {
                    tvNum.setText(String.valueOf(seat.getItsStudent().getNum()));
                    tvName.setText(seat.getItsStudent().getName());
                    ivBoygirl.setImageResource(seat.getItsStudent().isBoy() ? R.drawable.ic_toggle_boy : R.drawable.ic_toggle_girl);
                }
                else {
                    tvNum.setText(null);
                    tvName.setText(null);
                    ivBoygirl.setImageResource(0);
                }
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

        ClassDBHelper dbHelper = mainActivity.getDbHelper();

        Cursor dateListCursor = dbHelper.getSavedDateList();
        mSavedDateList = new ArrayList<Long>();
        if(dateListCursor.moveToFirst()) {
            while(!dateListCursor.isAfterLast()) {
                mSavedDateList.add(dateListCursor.getLong(dateListCursor.getColumnIndexOrThrow(ClassDBContract.SeatHistory.COLUMN_NAME_DATE)));
                dateListCursor.moveToNext();
            }
        }
        dateListCursor.close();

	    /* apply current seat map read from database */
        Cursor recentSeatCursor = dbHelper.getRecentSeats();

	    if(recentSeatCursor.moveToFirst()) {
            long date = recentSeatCursor.getLong(recentSeatCursor.getColumnIndexOrThrow(ClassDBContract.SeatHistory.COLUMN_NAME_DATE));
            recentSavedCal = new GregorianCalendar();
            recentSavedCal.setTimeInMillis(date);

            while(!recentSeatCursor.isAfterLast()) {
                int stduent_id = recentSeatCursor.getInt(recentSeatCursor.getColumnIndexOrThrow(ClassDBContract.SeatHistory.COLUMN_NAME_STUDENT_ID));
                int seat_id = recentSeatCursor.getInt(recentSeatCursor.getColumnIndexOrThrow(ClassDBContract.SeatHistory.COLUMN_NAME_SEAT_ID));
                Student student = mStudents.get(stduent_id);
                if(student != null) {
                    Seat seat = getSeatByAbsolutePosition(seat_id);
                    student.setItsCurrentSeat(seat);
                    seat.setItsStudent(student);
                }
                else Log.e(this.getClass().getSimpleName(), "No Student Object matching with DB");
                recentSeatCursor.moveToNext();
            }
        }
        recentSeatCursor.close();
        //assignRandom();
    }

    public void addToClonedSegment(Seat seat) {
        switch(seat.getId() % 6) {
            case 0:
            case 1:
                mNewSegment1.add(seat);
                break;
            case 2:
            case 3:
                mNewSegment2.add(seat);
                break;
            case 4:
            case 5:
                mNewSegment3.add(seat);
                break;
            default:
                break;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(this.getClass().getSimpleName(), "onCreateView()");
        View rootView = inflater.inflate(R.layout.fragment_seat, container, false);

	    tv_curDate = (TextView) rootView.findViewById(R.id.textview_current_month);
        if(recentSavedCal != null) {
            tv_curDate.setText(String.format("%d. %d. %d",
                    recentSavedCal.get(Calendar.YEAR),
                    recentSavedCal.get(Calendar.MONTH) + 1,
                    recentSavedCal.get(Calendar.DAY_OF_MONTH)));
        }
        final ArrayList<String> savedDateStringList = new ArrayList<String>();
        GregorianCalendar tempcal = new GregorianCalendar();
        for(Long milDate : mSavedDateList) {
            tempcal.setTimeInMillis(milDate);
            savedDateStringList.add(String.format("%d. %d. %d",
                    tempcal.get(Calendar.YEAR),
                    tempcal.get(Calendar.MONTH)+1,
                    tempcal.get(Calendar.DAY_OF_MONTH)));
        }
        tv_curDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
                builder
                        .setTitle(R.string.title_dialog_select_date)
                        .setItems(savedDateStringList.toArray(
                                        new CharSequence[savedDateStringList.size()]),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

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

    private void createNewPlan() {
        tv_curDate.setVisibility(View.INVISIBLE);
        btn_shuffle.setVisibility(View.VISIBLE);

        /* Cloning all lists and Treemaps... */
        mNewStudents = new TreeMap<Integer, Student>();
        mNewSegment1 = new ArrayList<Seat>();
        mNewSegment2 = new ArrayList<Seat>();
        mNewSegment3 = new ArrayList<Seat>();

        Student st = null;
        for(TreeMap.Entry<Integer, Student> entry : mStudents.entrySet()) {
            try {
                st = entry.getValue().clone();
                mNewStudents.put(st.getNum(), st);
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }
        for(Seat seat : mSegment1) {
            try {
                Seat newSeat = seat.clone();
                addToClonedSegment(newSeat);
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }
        for(Seat seat : mSegment2) {
            try {
                Seat newSeat = seat.clone();
                addToClonedSegment(newSeat);
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }
        for(Seat seat : mSegment3) {
            try {
                Seat newSeat = seat.clone();
                addToClonedSegment(newSeat);
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }
        /* Cloning End : link is twisted orz */

        /* Remove all links of the cloned lists and treemap (because this is a NEW plan) */
        removeAssignments(mNewStudents);
        removeAssignments(mNewSegment1);
        removeAssignments(mNewSegment2);
        removeAssignments(mNewSegment3);

        mNewSegAdpt1 = new segAdapter(mainActivity, mNewSegment1);
        mNewSegAdpt2 = new segAdapter(mainActivity, mNewSegment2);
        mNewSegAdpt3 = new segAdapter(mainActivity, mNewSegment3);

        gv_segment1.setAdapter(mNewSegAdpt1);
        gv_segment2.setAdapter(mNewSegAdpt2);
        gv_segment3.setAdapter(mNewSegAdpt3);
    }

    private void discardAndRemoveClones() {
        gv_segment1.setAdapter(mSegAdpt1);
        gv_segment2.setAdapter(mSegAdpt2);
        gv_segment3.setAdapter(mSegAdpt3);

        mNewStudents = null;
        mNewSegment1 = null;
        mNewSegment2 = null;
        mNewSegment3 = null;
        mNewSegAdpt1 = null;
        mNewSegAdpt2 = null;
        mNewSegAdpt3 = null;
    }

    private void discardCurrentPlan() {
        discardAndRemoveClones();

        tv_curDate.setVisibility(View.VISIBLE);
        btn_shuffle.setVisibility(View.INVISIBLE);
    }

    private void saveCurrentPlan() {
        assert(mNewStudents != null && mNewSegment1 != null
                && mNewSegment2 != null && mNewSegment3 != null);

        // TODO : check if all students have his/her own seat

	    /* get current date */
        int year, month, day;
        final GregorianCalendar cal = new GregorianCalendar();
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH);
        day = cal.get(Calendar.DAY_OF_MONTH);

		/* dialog : select date */
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                if(view.isShown()) {
                    tv_curDate.setText(String.format("%d. %d. %d", year, monthOfYear + 1, dayOfMonth));
                    cal.clear();
                    cal.set(year, monthOfYear, dayOfMonth);

                    final long curDateInMills = cal.getTimeInMillis();
                    final ClassDBHelper dbHelper = mainActivity.getDbHelper();
                    Cursor c = dbHelper.getSeatsForDate(curDateInMills);
                    if(!c.moveToFirst()) {
                        swapWithClones();

                        /* save to DB */
                        Student st;
                        for (TreeMap.Entry<Integer, Student> entry : mStudents.entrySet()) {
                            st = entry.getValue();
                            dbHelper.insert(st.getItsCurrentSeat(), curDateInMills);
                        }

                        /* deal with views */
                        tv_curDate.setVisibility(View.VISIBLE);
                        btn_shuffle.setVisibility(View.INVISIBLE);

                        mEditMode = false;
                        mainActivity.invalidateOptionsMenu();
                    }
                    else {      // seat assignment for this date already exists. so, UPDATE. not INSERT.
                        AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
                        builder.setTitle(R.string.title_dialog_warning);
                        builder.setMessage(R.string.contents_dialog_seat_assign_already_exists);
                        builder.setPositiveButton(R.string.action_overwrite, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User clicked OK button
                                swapWithClones();

                                /* update DB */
                                Student st;
                                for(TreeMap.Entry<Integer, Student> entry : mStudents.entrySet()) {
                                    st = entry.getValue();
                                    dbHelper.update(st.getItsCurrentSeat(), curDateInMills);
                                }

                                /* deal with views */
                                tv_curDate.setVisibility(View.VISIBLE);
                                btn_shuffle.setVisibility(View.INVISIBLE);

                                mEditMode = false;
                                mainActivity.invalidateOptionsMenu();
                            }
                        });
                        builder.setNegativeButton(R.string.action_cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                    c.close();
                }
            }
        };
        new DatePickerDialog(mainActivity, dateSetListener, year, month, day).show();
    }

    private void swapWithClones() {
        mStudents = mNewStudents;
        mSegment1 = mNewSegment1;
        mSegment2 = mNewSegment2;
        mSegment3 = mNewSegment3;
        mSegAdpt1 = mNewSegAdpt1;
        mSegAdpt2 = mNewSegAdpt2;
        mSegAdpt3 = mNewSegAdpt3;

        gv_segment1.setAdapter(mSegAdpt1);
        gv_segment2.setAdapter(mSegAdpt2);
        gv_segment3.setAdapter(mSegAdpt3);

        mNewStudents = null;
        mNewSegment1 = null;
        mNewSegment2 = null;
        mNewSegment3 = null;
        mNewSegAdpt1 = null;
        mNewSegAdpt2 = null;
        mNewSegAdpt3 = null;
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
        discardAndRemoveClones();
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
            mEditMode = true;
            mainActivity.invalidateOptionsMenu();

            createNewPlan();
            return true;
        }
        else if(id == R.id.seatplan_done) {
            saveCurrentPlan();
            return true;
		}
        else if(id == R.id.seatplan_cancel) {
            discardCurrentPlan();

            mEditMode = false;
            refreshSegView();

            mainActivity.invalidateOptionsMenu();
            return true;
        }
		else if(id == R.id.seatplan_delete_all) {
			ClassDBHelper dbHelper = mainActivity.getDbHelper();
			dbHelper.deleteAllSeats();

            removeAssignments(mSegment1);
            removeAssignments(mSegment2);
            removeAssignments(mSegment3);
            removeAssignments(mStudents);

            tv_curDate.setText(null);
            mEditMode = false;
            refreshSegView();

            mainActivity.invalidateOptionsMenu();
            return true;
		}
		return super.onOptionsItemSelected(item);
	}

    private void removeAssignments(TreeMap<Integer, Student> studentList) {
        for(TreeMap.Entry<Integer, Student> entry : studentList.entrySet()) {
            entry.getValue().setItsCurrentSeat(null);
        }
    }

    private void removeAssignments(ArrayList<Seat> seatList) {
        for(Seat seat : seatList) {
            seat.setItsStudent(null);
        }
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
        ArrayList<Seat> seg = null;

        if(mEditMode) {
            switch (mod) {
                case 0:
                case 1:
                    seg = mNewSegment1;
                    break;
                case 2:
                case 3:
                    seg = mNewSegment2;
                    break;
                case 4:
                case 5:
                    seg = mNewSegment3;
                    break;
                default:
                    break;
            }
        }
        else {
            switch (mod) {
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

	    for(TreeMap.Entry<Integer, Student> entry : mNewStudents.entrySet()) {
            st = entry.getValue();
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
            Seat seat = getSeatByAbsolutePosition(seatId);
            seat.setItsStudent(st);
            st.setItsCurrentSeat(seat);
            seatIsFull[seatId] = true;
        }
	    refreshSegView();
    }

    private void refreshSegView() {
        if(mEditMode) {
            mNewSegAdpt1.notifyDataSetChanged();
            mNewSegAdpt2.notifyDataSetChanged();
            mNewSegAdpt3.notifyDataSetChanged();
        }
        else {
            mSegAdpt1.notifyDataSetChanged();
            mSegAdpt2.notifyDataSetChanged();
            mSegAdpt3.notifyDataSetChanged();
        }
    }
}
