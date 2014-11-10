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
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ActionViewTarget;
import com.github.amlcurran.showcaseview.targets.ViewTarget;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
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
    private ArrayList<String>   mSavedDateStringList;
    private long                mCurrentShowingDate = 0;
    private GregorianCalendar   mCurrentShowingCal = null;

    private Seat                mLeftSelectedSeat = null;
    private Seat                mRightSelectedSeat = null;

    /*private boolean             mUserLearnedNewPlan;
    private boolean             mUserLearnedSeeHistory;*/

    /* 새 자리 배치, 배치 수정 등에 쓰일 임시 리스트들 (원본 복제해서 작업 후 save or discard) */
    private TreeMap<Integer, Student>   mNewStudents = null;
    private ArrayList<Seat>             mNewSegment1 = null, mNewSegment2 = null, mNewSegment3 = null;

	/* View variables */
    private LinearLayout    layout_onseatclick_inflated;
	private GridView        gv_segment1;
	private GridView        gv_segment2;
	private GridView        gv_segment3;

	private TextView    tv_curDate;
	private Button      btn_shuffle;

	private int         mTotalSeats, mBoysSeats, mGirlsSeats;

    private ShowcaseView        mNewPlanShowcaseview = null;
    private ShowcaseView        mHistoryShowcaseview = null;
    private static final long   mNewPlanShowcaseviewShotId = 10L;
    private static final long   mHistoryShowcaseviewShotId = 11L;

    /*private static final String PREF_USER_LEARNED_NEW_PLAN = "new_plan_learned";
    private static final String PREF_USER_LEARNED_SEE_HISTORY = "see_history_learned";*/

	private class segAdapter extends BaseAdapter {
		private LayoutInflater mInflater;
		private Context mContext;
		private ArrayList<Seat> mItems;

		public segAdapter(Context context, ArrayList<Seat> object) {
			mContext = context;
			mItems = object;
			mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

        public void setItems(ArrayList<Seat> object) {
            this.mItems = object;
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
            final Seat seat = (Seat)this.getItem(position);

			if(convertView == null) {
				view = mInflater.inflate(R.layout.seat, null);
			}
			else {
				view = convertView;
			}

			if(seat != null) {
                if(seat.isSelected()) view.setBackground(getResources().getDrawable(R.drawable.desk_selected));
                else view.setBackground(getResources().getDrawable(R.drawable.desk));
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

        /*SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mUserLearnedNewPlan = sp.getBoolean(PREF_USER_LEARNED_NEW_PLAN, false);
        mUserLearnedSeeHistory = sp.getBoolean(PREF_USER_LEARNED_SEE_HISTORY, false);*/

        ClassDBHelper dbHelper = mainActivity.getDbHelper();

        /* Loading Saved Date List from DB... */
        Cursor dateListCursor = dbHelper.getSavedDateList();
        mSavedDateList = new ArrayList<Long>();
        mSavedDateStringList = new ArrayList<String>();
        mCurrentShowingCal = new GregorianCalendar();

        if(dateListCursor.moveToFirst()) {
            while(!dateListCursor.isAfterLast()) {
                long date = dateListCursor.getLong(dateListCursor.getColumnIndexOrThrow(ClassDBContract.SeatHistory.COLUMN_NAME_DATE));
                mSavedDateList.add(date);
                mCurrentShowingCal.setTimeInMillis(date);
                mSavedDateStringList.add(String.format("%04d. %02d. %02d",
                        mCurrentShowingCal.get(Calendar.YEAR),
                        mCurrentShowingCal.get(Calendar.MONTH)+1,
                        mCurrentShowingCal.get(Calendar.DAY_OF_MONTH)));
                dateListCursor.moveToNext();
            }
            mCurrentShowingDate = mSavedDateList.get(0);        // Latest saved date
            mCurrentShowingCal.setTimeInMillis(mCurrentShowingDate);
        }
        // If there is no saved plan (ex. first run), mCurrentShowingDate is 0
        dateListCursor.close();
        /* End */

        /* Getting (latest) Students list from main Activity... */
		mStudents = mainActivity.getmStudents();
		mBoysSeats = mainActivity.getNum_boys();
		mGirlsSeats = mainActivity.getNum_girls();
		mTotalSeats = mBoysSeats + mGirlsSeats;
        /* End */

        mSegment1 = new ArrayList<Seat>();
        mSegment2 = new ArrayList<Seat>();
        mSegment3 = new ArrayList<Seat>();

        if(!mStudents.isEmpty()) {
            /* Creating segments and Seats... */
            for (int i = 0; i < mTotalSeats; i++) {
                Seat seat = new Seat(i);
                addToSegment(seat);
            }
            /* End */

	        /* apply current seat map read from database */
            Cursor seatCursor = dbHelper.getSeatPlan(mCurrentShowingDate);

            if (seatCursor.moveToFirst()) {
                while (!seatCursor.isAfterLast()) {
                    int stduent_id = seatCursor.getInt(seatCursor.getColumnIndexOrThrow(ClassDBContract.SeatHistory.COLUMN_NAME_STUDENT_ID));
                    int seat_id = seatCursor.getInt(seatCursor.getColumnIndexOrThrow(ClassDBContract.SeatHistory.COLUMN_NAME_SEAT_ID));
                    Student student = mStudents.get(stduent_id);
                    if (student != null) {
                        Seat seat = getSeatByAbsolutePosition(seat_id);
                        student.setItsCurrentSeat(seat);
                        seat.setItsStudent(student);
                    } else
                        Log.e(this.getClass().getSimpleName(), "No Student Object matching with DB");
                    seatCursor.moveToNext();
                }
            }
            seatCursor.close();
            /* End */
        }

    }

    private void addToSegment(Seat seat) {
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

        layout_onseatclick_inflated = (LinearLayout)rootView.findViewById(R.id.linearlayout_onseatclick_inflated);

	    tv_curDate = (TextView) rootView.findViewById(R.id.textview_current_month);
        if(mCurrentShowingDate != 0) {
            tv_curDate.setText(String.format("%04d. %02d. %02d",
                    mCurrentShowingCal.get(Calendar.YEAR),
                    mCurrentShowingCal.get(Calendar.MONTH) + 1,
                    mCurrentShowingCal.get(Calendar.DAY_OF_MONTH)));
        }

        tv_curDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mHistoryShowcaseview != null) mHistoryShowcaseview.hide();
                AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
                builder
                        .setTitle(R.string.title_dialog_select_date)
                        .setItems(mSavedDateStringList.toArray(
                                        new CharSequence[mSavedDateStringList.size()]),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        showPlan(mSavedDateList.get(which));
                                    }
                                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        gv_segment1 = (GridView) rootView.findViewById(R.id.gridview_segment1);
        gv_segment2 = (GridView) rootView.findViewById(R.id.gridview_segment2);
        gv_segment3 = (GridView) rootView.findViewById(R.id.gridview_segment3);

        gv_segment1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onSeatClick(mEditMode ? mNewSegment1 : mSegment1, position);
            }
        });
        gv_segment2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onSeatClick(mEditMode? mNewSegment2 : mSegment2, position);
            }
        });
        gv_segment3.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onSeatClick(mEditMode? mNewSegment3 : mSegment3, position);
            }
        });

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

        TextView tv_warning = (TextView)rootView.findViewById(R.id.textview_warning_no_student_info);
        if(!mStudents.isEmpty()) {
            tv_warning.setVisibility(View.GONE);
        }

        if(mSavedDateList.size() > 1)
            displayHistoryShowcase();

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.d(this.getClass().getSimpleName(), "onActivityCreated()");
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    private void displayNewPlanShowcase() {
        //mUserLearnedNewPlan = true;
        /*SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(getActivity());
        sp.edit().putBoolean(PREF_USER_LEARNED_NEW_PLAN, true).apply();*/
        DisplayMetrics metrics = new DisplayMetrics();
        mainActivity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int screenWidth = metrics.widthPixels;
        int screenHeight = metrics.heightPixels;

        mNewPlanShowcaseview = new ShowcaseView.Builder(mainActivity)
                .singleShot(mNewPlanShowcaseviewShotId)
                .setTarget(new ActionViewTarget(mainActivity, ActionViewTarget.Type.OVERFLOW))
                .setContentTitle(getString(R.string.showcase_title_new_plan))
                .setContentText(R.string.showcase_detail_new_plan)
                .hideOnTouchOutside()
                .setStyle(R.style.ShowcaseView_Light)
                .setScaleMultiplier(0.5F)
                .hasManualPosition(true)
                .setPosition((int)(screenWidth * 0.05), (int)(screenHeight * 0.8))
                .build();
    }

    private void displayHistoryShowcase() {
        DisplayMetrics metrics = new DisplayMetrics();
        mainActivity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int screenWidth = metrics.widthPixels;
        int screenHeight = metrics.heightPixels;

        mHistoryShowcaseview = new ShowcaseView.Builder(mainActivity)
                .singleShot(mHistoryShowcaseviewShotId)
                .setTarget(new ViewTarget((View)tv_curDate))
                .setContentTitle(getString(R.string.showcase_title_history))
                .setContentText(getString(R.string.showcase_detail_history))
                .hideOnTouchOutside()
                .setStyle(R.style.ShowcaseView_Light)
                .setScaleMultiplier(0.3F)
                .hasManualPosition(true)
                .setPosition((int)(screenWidth * 0.05), (int)(screenHeight * 0.8))
                .build();
    }

    private void onSeatClick(ArrayList<Seat> seg, int position) {
        if(seg.get(position).getItsStudent() == null) return;

        final LayoutInflater inflater =  (LayoutInflater)mainActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layout_onseatclick_inflated.setVisibility(View.VISIBLE);
        inflater.inflate(R.layout.onseatclick_inflated, layout_onseatclick_inflated);

        final LinearLayout layout_onseatclick_left =
                (LinearLayout)layout_onseatclick_inflated.findViewById(R.id.linearlayout_onseatclick_left);
        final LinearLayout layout_onseatclick_right =
                (LinearLayout)layout_onseatclick_inflated.findViewById(R.id.linearlayout_onseatclick_right);

        ClassDBHelper dbHelper = mainActivity.getDbHelper();

        int where, pair;
        long when;
        GregorianCalendar cal = new GregorianCalendar();
        Cursor historyCursor;
        Student selectedStudent;
        TextView tv;

        final Button btn_change_seat = (Button)layout_onseatclick_inflated.findViewById(R.id.btn_change_seat);
        btn_change_seat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Student tempStd = mLeftSelectedSeat.getItsStudent();
                mLeftSelectedSeat.setItsStudent(mRightSelectedSeat.getItsStudent());
                mRightSelectedSeat.setItsStudent(tempStd);

                mLeftSelectedSeat.getItsStudent().setItsCurrentSeat(mLeftSelectedSeat);
                mRightSelectedSeat.getItsStudent().setItsCurrentSeat(mRightSelectedSeat);

                ClassDBHelper dbHelper = mainActivity.getDbHelper();
                Seat pairSeat = getSeatByAbsolutePosition(mLeftSelectedSeat.getPairSeatId());
                dbHelper.update(mLeftSelectedSeat,
                        pairSeat == null? -1 : pairSeat.getItsStudent().getNum(),
                        mCurrentShowingDate);
                pairSeat = getSeatByAbsolutePosition(mRightSelectedSeat.getPairSeatId());
                dbHelper.update(mRightSelectedSeat,
                        pairSeat == null? -1 : pairSeat.getItsStudent().getNum(),
                        mCurrentShowingDate);

                mLeftSelectedSeat.setSelected(false);
                mRightSelectedSeat.setSelected(false);

                mLeftSelectedSeat = null;
                mRightSelectedSeat = null;

                layout_onseatclick_left.removeAllViews();
                layout_onseatclick_right.removeAllViews();
                layout_onseatclick_inflated.setVisibility(View.GONE);
                refreshSegView();
            }
        });
        final Button btn_left_cancel = (Button)layout_onseatclick_inflated.findViewById(R.id.btn_left_cancel);
        btn_left_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLeftSelectedSeat.setSelected(false);
                mLeftSelectedSeat = null;
                layout_onseatclick_left.removeAllViews();
                btn_left_cancel.setVisibility(View.INVISIBLE);
                btn_change_seat.setVisibility(View.INVISIBLE);
                if(mRightSelectedSeat == null) layout_onseatclick_inflated.setVisibility(View.GONE);
                refreshSegView();
            }
        });
        final Button btn_right_cancel = (Button)layout_onseatclick_inflated.findViewById(R.id.btn_right_cancel);
        btn_right_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRightSelectedSeat.setSelected(false);
                mRightSelectedSeat = null;
                layout_onseatclick_right.removeAllViews();
                btn_right_cancel.setVisibility(View.INVISIBLE);
                btn_change_seat.setVisibility(View.INVISIBLE);
                if(mLeftSelectedSeat == null) layout_onseatclick_inflated.setVisibility(View.GONE);
                refreshSegView();
            }
        });

        if(mLeftSelectedSeat == null) {
            mLeftSelectedSeat = seg.get(position);
            selectedStudent = mLeftSelectedSeat.getItsStudent();
            mLeftSelectedSeat.setSelected(true);
            tv = new TextView(mainActivity);
            tv.setText(selectedStudent.getName());
            tv.setTextSize(18);
            layout_onseatclick_left.addView(tv);
            historyCursor = dbHelper.getHistory(selectedStudent.getNum());
            if (historyCursor.moveToFirst()) {
                int historyCount = 0;
                historyCursor.moveToNext();
                while (!historyCursor.isAfterLast() && historyCount < 3) {
                    where = historyCursor.getInt(historyCursor.getColumnIndexOrThrow(ClassDBContract.SeatHistory.COLUMN_NAME_SEAT_ID));
                    pair = historyCursor.getInt(historyCursor.getColumnIndexOrThrow(ClassDBContract.SeatHistory.COLUMN_NAME_PAIR_STUDENT));
                    when = historyCursor.getLong(historyCursor.getColumnIndexOrThrow(ClassDBContract.SeatHistory.COLUMN_NAME_DATE));
                    cal.setTimeInMillis(when);
                    String whenStr = String.format("%02d.%02d ~\n", cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH));
                    String whereStr = ConvertAbsSeatToSegAndRow(where) + ", ";
                    String pariStr = mStudents.get(pair).getName();
                    tv = new TextView(mainActivity);
                    tv.setText(whenStr + whereStr + pariStr);
                    tv.setTextSize(14);
                    layout_onseatclick_left.addView(tv);
                    historyCursor.moveToNext();
                    historyCount++;
                }
            }
            btn_left_cancel.setVisibility(View.VISIBLE);
        }
        else if(mRightSelectedSeat == null) {
            mRightSelectedSeat = seg.get(position);
            selectedStudent = mRightSelectedSeat.getItsStudent();
            mRightSelectedSeat.setSelected(true);
            tv = new TextView(mainActivity);
            tv.setText(selectedStudent.getName());
            tv.setTextSize(18);
            layout_onseatclick_right.addView(tv);
            historyCursor = dbHelper.getHistory(selectedStudent.getNum());
            if(historyCursor.moveToFirst()) {
                int historyCount = 0;
                historyCursor.moveToNext();
                while(!historyCursor.isAfterLast() && historyCount < 3) {
                    where = historyCursor.getInt(historyCursor.getColumnIndexOrThrow(ClassDBContract.SeatHistory.COLUMN_NAME_SEAT_ID));
                    pair = historyCursor.getInt(historyCursor.getColumnIndexOrThrow(ClassDBContract.SeatHistory.COLUMN_NAME_PAIR_STUDENT));
                    when = historyCursor.getLong(historyCursor.getColumnIndexOrThrow(ClassDBContract.SeatHistory.COLUMN_NAME_DATE));
                    cal.setTimeInMillis(when);
                    String whenStr = String.format("%02d.%02d ~\n", cal.get(Calendar.MONTH)+1, cal.get(Calendar.DAY_OF_MONTH));
                    String whereStr = ConvertAbsSeatToSegAndRow(where) + ", ";
                    String pariStr = mStudents.get(pair).getName();
                    tv = new TextView(mainActivity);
                    tv.setText(whenStr + whereStr + pariStr);
                    tv.setTextSize(14);
                    layout_onseatclick_right.addView(tv);
                    historyCursor.moveToNext();
                    historyCount++;
                }
            }
            btn_right_cancel.setVisibility(View.VISIBLE);
        }

        if(mLeftSelectedSeat != null && mRightSelectedSeat != null) {
            btn_change_seat.setVisibility(View.VISIBLE);
        }
        refreshSegView();
    }

    private String ConvertAbsSeatToSegAndRow(int seatId) {
        int row = seatId / 6 + 1;
        int seg = ((seatId % 6) / 2) + 1;
        String segAndRow =
                String.valueOf(seg) +
                getString(R.string.string_segment) + " " +
                String.valueOf(row) +
                getString(R.string.string_row) + " ";
        if(seatId % 2 == 0) segAndRow += getString(R.string.string_left);
        else segAndRow += getString(R.string.string_right);
        return segAndRow;
    }

    private void showPlan(long plannedDate) {
        ClassDBHelper dbHelper = mainActivity.getDbHelper();
        Cursor studentCursor;
        Cursor seatsForDateCursor = dbHelper.getSeatPlan(plannedDate);

        if(seatsForDateCursor.moveToFirst()) {
            mCurrentShowingDate = plannedDate;
            mCurrentShowingCal.setTimeInMillis(mCurrentShowingDate);

            mStudents = new TreeMap<Integer, Student>();

            mSegment1 = new ArrayList<Seat>();
            mSegment2 = new ArrayList<Seat>();
            mSegment3 = new ArrayList<Seat>();

            while(!seatsForDateCursor.isAfterLast()) {
                int studentId = seatsForDateCursor.getInt(seatsForDateCursor.getColumnIndexOrThrow(ClassDBContract.SeatHistory.COLUMN_NAME_STUDENT_ID));
                int seatId = seatsForDateCursor.getInt(seatsForDateCursor.getColumnIndexOrThrow(ClassDBContract.SeatHistory.COLUMN_NAME_SEAT_ID));
                studentCursor = dbHelper.getStudent(studentId);

                if(!studentCursor.moveToFirst()) Log.e(this.getClass().getSimpleName(), "ERROR : no student with studentId");
                Student st = new Student(studentId,
                        studentCursor.getString(studentCursor.getColumnIndexOrThrow(ClassDBContract.StudentInfo.COLUMN_NAME_STUDENT_NAME)),
                        studentCursor.getInt(studentCursor.getColumnIndexOrThrow(ClassDBContract.StudentInfo.COLUMN_NAME_STUDENT_GENDER)) == 1);
                Seat seat = new Seat(seatId, st);
                st.setItsCurrentSeat(seat);
                mStudents.put(studentId, st);
                addToSegment(seat);

                studentCursor.close();
                seatsForDateCursor.moveToNext();
            }

            Comparator<Seat> comp = new Comparator<Seat>() {
                @Override
                public int compare(Seat lhs, Seat rhs) {
                    return lhs.getId() - rhs.getId();
                }
            };
            Collections.sort(mSegment1, comp);
            Collections.sort(mSegment2, comp);
            Collections.sort(mSegment3, comp);

            mSegAdpt1.setItems(mSegment1);
            mSegAdpt2.setItems(mSegment2);
            mSegAdpt3.setItems(mSegment3);

            tv_curDate.setText(String.format("%04d. %02d. %02d",
                    mCurrentShowingCal.get(Calendar.YEAR),
                    mCurrentShowingCal.get(Calendar.MONTH)+1,
                    mCurrentShowingCal.get(Calendar.DAY_OF_MONTH)));

            refreshSegView();
        }
        else {
            /* There is no saved plans, so Creating segments and Seats... */
            mSegment1 = new ArrayList<Seat>();
            mSegment2 = new ArrayList<Seat>();
            mSegment3 = new ArrayList<Seat>();

            for (int i = 0; i < mTotalSeats; i++) {
                Seat seat = new Seat(i);
                addToSegment(seat);
            }

            mSegAdpt1.setItems(mSegment1);
            mSegAdpt2.setItems(mSegment2);
            mSegAdpt3.setItems(mSegment3);

            refreshSegView();

            tv_curDate.setText(null);
        }
        seatsForDateCursor.close();
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

        mSegAdpt1.setItems(mNewSegment1);
        mSegAdpt2.setItems(mNewSegment2);
        mSegAdpt3.setItems(mNewSegment3);

        // Plan 생성시 자동으로 랜덤배치
        assignRandom();

        refreshSegView();
    }

    private void discardAndRemoveClones() {
        mSegAdpt1.setItems(mSegment1);
        mSegAdpt2.setItems(mSegment2);
        mSegAdpt3.setItems(mSegment3);

        mNewStudents = null;
        mNewSegment1 = null;
        mNewSegment2 = null;
        mNewSegment3 = null;
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
                    cal.clear();
                    cal.set(year, monthOfYear, dayOfMonth);

                    final long curDateInMills = cal.getTimeInMillis();
                    final ClassDBHelper dbHelper = mainActivity.getDbHelper();
                    Cursor c = dbHelper.getSeatPlan(curDateInMills);
                    if(!c.moveToFirst()) {
                        swapWithClones();
                        mEditMode = false;

                        /* save to DB */
                        Student st;
                        Seat seat, pairSeat;
                        for (TreeMap.Entry<Integer, Student> entry : mStudents.entrySet()) {
                            st = entry.getValue();
                            seat = st.getItsCurrentSeat();
                            pairSeat = getSeatByAbsolutePosition(seat.getPairSeatId());
                            dbHelper.insert(
                                    seat,                                                                   // seat ID
                                    pairSeat == null? -1 : pairSeat.getItsStudent().getNum(),               // paired Student's ID
                                    curDateInMills);                                                        // date
                        }

                        /* add to Saved Date List (Long, String both) */
                        mCurrentShowingDate = curDateInMills;
                        mCurrentShowingCal.setTimeInMillis(mCurrentShowingDate);
                        mSavedDateList.add(mCurrentShowingDate);
                        mSavedDateStringList.add(String.format("%04d. %02d. %02d",
                                mCurrentShowingCal.get(Calendar.YEAR),
                                mCurrentShowingCal.get(Calendar.MONTH)+1,
                                mCurrentShowingCal.get(Calendar.DAY_OF_MONTH)));
                        Collections.sort(mSavedDateList, Collections.reverseOrder());
                        Collections.sort(mSavedDateStringList, Collections.reverseOrder());

                        /* deal with views */
                        tv_curDate.setText(String.format("%04d. %02d. %02d",
                                mCurrentShowingCal.get(Calendar.YEAR),
                                mCurrentShowingCal.get(Calendar.MONTH)+1,
                                mCurrentShowingCal.get(Calendar.DAY_OF_MONTH)));
                        tv_curDate.setVisibility(View.VISIBLE);
                        btn_shuffle.setVisibility(View.INVISIBLE);

                        mainActivity.invalidateOptionsMenu();

                        if(mSavedDateList.size() > 1)
                            displayHistoryShowcase();
                    }
                    else {      // seat assignment for this date already exists. so, UPDATE. not INSERT.
                        AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
                        builder.setTitle(R.string.title_dialog_warning);
                        builder.setMessage(R.string.contents_dialog_seat_assign_already_exists);
                        builder.setPositiveButton(R.string.action_overwrite, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User clicked OK button
                                swapWithClones();
                                mEditMode = false;

                                /* update DB */
                                Student st;
                                Seat seat, pairSeat;
                                for(TreeMap.Entry<Integer, Student> entry : mStudents.entrySet()) {
                                    st = entry.getValue();
                                    seat = st.getItsCurrentSeat();
                                    pairSeat = getSeatByAbsolutePosition(seat.getPairSeatId());
                                    dbHelper.update(
                                            seat,
                                            pairSeat == null? -1 : pairSeat.getItsStudent().getNum(),
                                            curDateInMills);
                                }

                                /* No need to add Saved Date List (already exist because this is UPDATE, not INSERT
                                 * only set current showing date */
                                mCurrentShowingDate = curDateInMills;
                                mCurrentShowingCal.setTimeInMillis(mCurrentShowingDate);

                                /* deal with views */
                                tv_curDate.setText(String.format("%04d. %02d. %02d",
                                        mCurrentShowingCal.get(Calendar.YEAR),
                                        mCurrentShowingCal.get(Calendar.MONTH)+1,
                                        mCurrentShowingCal.get(Calendar.DAY_OF_MONTH)));
                                tv_curDate.setVisibility(View.VISIBLE);
                                btn_shuffle.setVisibility(View.INVISIBLE);

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
        mainActivity.setmStudents(mNewStudents);
        mSegment1 = mNewSegment1;
        mSegment2 = mNewSegment2;
        mSegment3 = mNewSegment3;

        mNewStudents = null;
        mNewSegment1 = null;
        mNewSegment2 = null;
        mNewSegment3 = null;
    }

    private void deleteShowingPlan() {
        if(mCurrentShowingDate != 0) {
            mainActivity.getDbHelper().deleteSeatPlan(mCurrentShowingDate);
            int idx = mSavedDateList.indexOf(mCurrentShowingDate);
            mSavedDateList.remove(idx);
            mSavedDateStringList.remove(idx);
            mCurrentShowingDate = mSavedDateList.isEmpty() ? 0 : mSavedDateList.get(0);
            mCurrentShowingCal.setTimeInMillis(mCurrentShowingDate);
            showPlan(mCurrentShowingDate);
        }
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

    /*@Override
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
            if(!mStudents.isEmpty()) {//&& !mUserLearnedNewPlan) {
                displayNewPlanShowcase();
            }
		}
	}*/

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
        if(mNewPlanShowcaseview != null) mNewPlanShowcaseview.hide();

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
        else if(id == R.id.seatplan_delete) {
            deleteShowingPlan();

            mEditMode = false;
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
        if(seatId > mStudents.size() - 1) return null;

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
        Seat curSeat;
        Student curStudent, candidate;

        Cursor historyCursor;

        ArrayList<Student> curArray;
        ArrayList<Integer> leftSeatIdArray = new ArrayList<Integer>();
        ArrayList<Integer> rightSeatIdArray = new ArrayList<Integer>();
        ArrayList<Student> boysArray = new ArrayList<Student>();
        ArrayList<Student> girlsArray = new ArrayList<Student>();

        for(int i = 0; i < mTotalSeats; i += 2) leftSeatIdArray.add(i);
        for(int i = 1; i < mTotalSeats; i += 2) rightSeatIdArray.add(i);
        for(TreeMap.Entry<Integer, Student> entry : mNewStudents.entrySet()) {
            Student st = entry.getValue();
            if(st.isBoy()) boysArray.add(st);
            else girlsArray.add(st);
        }

        int studentIdx, seatIdx;
        int historyCount;
        double seatPoint, pairPoint;


        // 왼쪽 자리에 남학생 배치, 자리가 남으면 여학생 일부 배치
        seatIdx = 0;
        while(seatIdx < leftSeatIdArray.size()) {
            curSeat = getSeatByAbsolutePosition(leftSeatIdArray.get(seatIdx));

            if(!boysArray.isEmpty()) curArray = boysArray;
            else curArray = girlsArray;

            // curSeat에 대해, curArray의 학생들의 포인트 계산
            studentIdx = 0;
            while(studentIdx < curArray.size()) {
                curStudent = curArray.get(studentIdx);
                seatPoint = 0.0;
                historyCursor = mainActivity.getDbHelper().getHistory(curStudent.getNum());
                if(historyCursor.moveToFirst()) {
                    historyCount = 3;
                    while(!historyCursor.isAfterLast() && historyCount > 0) {
                        int seatedLocation = historyCursor.getInt(historyCursor.getColumnIndexOrThrow(
                                ClassDBContract.SeatHistory.COLUMN_NAME_SEAT_ID));
                        // 예전에 앉았던 자리와 같은 분단 (same column)
                        if(seatedLocation % 6 == curSeat.getId() % 6) {
                            seatPoint += (5.0 * historyCount * 0.3);
                        }
                        // 예전에 앉았던 자리와 같은 줄 (same row)
                        if(seatedLocation / 6 == curSeat.getId() / 6) {
                            seatPoint += (7.0 * historyCount * 0.3);
                        }
                        // 여학생이 왼쪽 자리에 앉은 적이 있는 경우 포인트 크게 증가
                        if(!curStudent.isBoy() && seatedLocation % 2 == 0) {
                            seatPoint += (20.0 * historyCount * 0.3);
                        }
                        historyCursor.moveToNext();
                        historyCount--;
                    }
                }
                historyCursor.close();
                curStudent.setSeatPoint(seatPoint);
                studentIdx++;
            }
            // seatPoint 계산 끝

            // seatPoint 순으로 정렬
            Comparator<Student> compBySeatPoint = new Comparator<Student>() {
                @Override
                public int compare(Student lhs, Student rhs) {
                    double diff = lhs.getSeatPoint() - rhs.getSeatPoint();
                    // history 없을 때 앞에서부터 번호순으로 정렬되지 않게 하도록, 포인트가 같을 경우 random 배치
                    return diff < 0 ? -1 : diff > 0 ? 1 : (int)(Math.random() * 3) - 1;
                }
            };
            Collections.sort(curArray, compBySeatPoint);

            // seatPoint 가 가장 작은놈!
            curStudent = curArray.get(0);

            curSeat.setItsStudent(curStudent);
            curStudent.setItsCurrentSeat(curSeat);

            curArray.remove(curStudent);
            seatIdx++;
        }

        while(!leftSeatIdArray.isEmpty()) {
            curSeat = getSeatByAbsolutePosition(leftSeatIdArray.get(0) + 1);
            curStudent = getSeatByAbsolutePosition(leftSeatIdArray.get(0)).getItsStudent();
            // curSeat == null 이면 마지막 혼자 앉는 자리임
            if(curSeat != null) {
                if(!girlsArray.isEmpty()) curArray = girlsArray;
                else curArray = boysArray;

                // 왼쪽 자리에 앉은 학생에 대하여 짝궁 포인트 계산, 현재 자리(오른쪽)에 대한 자리 포인트 계산
                studentIdx = 0;
                while(studentIdx < curArray.size()) {
                    candidate = curArray.get(studentIdx);
                    pairPoint = 0.0;
                    seatPoint = 0.0;
                    historyCursor = mainActivity.getDbHelper().getHistory(curStudent.getNum());
                    if(historyCursor.moveToFirst()) {
                        historyCount = 3;
                        while(!historyCursor.isAfterLast() && historyCount > 0) {
                            int seatedLocation = historyCursor.getInt(historyCursor.getColumnIndexOrThrow(
                                    ClassDBContract.SeatHistory.COLUMN_NAME_SEAT_ID));
                            int pairedStudent = historyCursor.getInt(historyCursor.getColumnIndexOrThrow(
                                    ClassDBContract.SeatHistory.COLUMN_NAME_PAIR_STUDENT));
                            // 한 번 앉았던 짝, 포인트 최대치로 설정
                            if(pairedStudent == curStudent.getNum()) {
                                pairPoint += (30.0 * historyCount * 0.3);
                            }
                            // 예전에 앉았던 자리와 같은 분단 (same column)
                            if(seatedLocation % 6 == curSeat.getId() % 6) {
                                seatPoint += (5.0 * historyCount * 0.3);
                            }
                            // 예전에 앉았던 자리와 같은 줄 (same row)
                            if(seatedLocation / 6 == curSeat.getId() / 6) {
                                seatPoint += (7.0 * historyCount * 0.3);
                            }
                            historyCursor.moveToNext();
                            historyCount--;
                        }
                    }
                    historyCursor.close();
                    candidate.setPairPoint(pairPoint);
                    candidate.setSeatPoint(seatPoint);
                    studentIdx++;
                }
                // 포인트 계산 완료

                // 짝궁 포인트 1순위, 자리포인트 2순위로 정렬
                Comparator<Student> compByPairAndSeat = new Comparator<Student>() {
                    @Override
                    public int compare(Student lhs, Student rhs) {
                        double diff = lhs.getPairPoint() - rhs.getPairPoint();
                        if(diff != 0) {
                            return diff < 0 ? -1 : 1;
                        }
                        else {
                            diff = lhs.getSeatPoint() - rhs.getSeatPoint();
                            return diff < 0 ? -1 : diff > 0 ? 1 : (int)(Math.random() * 3) - 1;
                        }
                    }
                };

                Collections.sort(curArray, compByPairAndSeat);

                candidate = curArray.get(0);

                candidate.setItsCurrentSeat(curSeat);
                curSeat.setItsStudent(candidate);

                curArray.remove(candidate);
            }
            leftSeatIdArray.remove(0);
        }

        /*
        while(!rightSeatIdArray.isEmpty()) {                    // 나머지 오른쪽 자리 모두 배치
            curSeat = getSeatByAbsolutePosition(rightSeatIdArray.get(0));

            if(!girlsArray.isEmpty()) curArray = girlsArray;
            else curArray = boysArray;

            curStudent = curArray.get((int)(Math.random() * curArray.size()));

            curSeat.setItsStudent(curStudent);
            curStudent.setItsCurrentSeat(curSeat);

            curArray.remove(curStudent);
            rightSeatIdArray.remove(0);
        }*/

	    refreshSegView();
    }

    private void refreshSegView() {
        mSegAdpt1.notifyDataSetChanged();
        mSegAdpt2.notifyDataSetChanged();
        mSegAdpt3.notifyDataSetChanged();
    }
}
