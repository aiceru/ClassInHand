package com.iceru.teacherschores;

import java.util.ArrayList;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

//temp test

/**
 * @author iceru
 * Fragment providing for user to input students' information
 */
public class FillInfoFragment extends Fragment {
	private LinearLayout		mPane, mFirstRow, mSecondRow;
	private EditText			mEditTextNum, mEditTextName;
	private ToggleButton		mTglbtnBoygirl;
	private Button				mBtnAddPerson;
	private View				rootView;
	//------------------> 리스트뷰 추가중
	private ListView			mStudentListView;
	private ArrayList<Student>	mStudents;
	private studentListAdapter	mStudentListAdapter;
	//<-------------------------------


	private static final String ARG_SECTION_NUMBER = "section_number";


	//------------------> 리스트뷰 추가중
	class Student {
		private int		num;
		private String	name;
		private boolean boy;		// true -> boy, false -> girl... T/F has no meaning. :)

		public Student(int num, String name, boolean boy) {
			this.num = num;
			this.name = name;
			this.boy = boy;
		}
		/**
		 * @return the num
		 */
		public int getNum() {
			return num;
		}
		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}
		/**
		 * @return the gender
		 */
		public boolean isBoy() {
			return boy;
		}
	}

	private class studentListAdapter extends ArrayAdapter<Student> {
		private LayoutInflater mInflater;

		public studentListAdapter(Context context, ArrayList<Student> object) {
			super(context, 0, object);
			mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		public View getView(int position, View v, ViewGroup parent) {
			View view = null;
			if(v == null) {
				view = mInflater.inflate(R.layout.student_info, null);
			}
			else {
				view = v;
			}

			final Student student = this.getItem(position);

			if(student != null) {
				TextView tvNum = (TextView)view.findViewById(R.id.textview_num);
				TextView tvName = (TextView)view.findViewById(R.id.textview_name);
				ImageView ivGender = (ImageView)view.findViewById(R.id.imageview_gender);
				tvNum.setText(String.valueOf(student.getNum()));
				tvName.setText(student.getName());
				ivGender.setImageResource((student.isBoy()? R.drawable.ic_toggle_boy : R.drawable.ic_toggle_girl));
			}

			return view;
		}
	}
	//<-----------------------------------

	/**
	 * Returns a new instance of this fragment for the given section
	 * number.
	 */
	public static FillInfoFragment newInstance(int sectionNumber) {
		FillInfoFragment fragment = new FillInfoFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		fragment.setArguments(args);
		return fragment;
	}

	public FillInfoFragment() {
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_fillinfo, container, false);
		this.rootView = rootView;

		mStudentListView = (ListView)rootView.findViewById(R.id.listview_students);
		
		//------->
		// TODO 이거... onCreate 로 옮겨야 하나 아님 생성자로...? fragment lifecycle 공부하자 ㅠㅠ
		
		
		mStudents = new ArrayList<Student>();
		mStudentListAdapter = new studentListAdapter(this.getActivity(), mStudents);
		mStudentListView.setAdapter(mStudentListAdapter);
		
		SwipeDismissListViewTouchListener touchListener =
                new SwipeDismissListViewTouchListener(
                        mStudentListView,
                        new SwipeDismissListViewTouchListener.DismissCallbacks() {
                            @Override
                            public boolean canDismiss(int position) {
                                return true;
                            }

                            @Override
                            public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {
                                    mStudentListAdapter.remove(mStudentListAdapter.getItem(position));
                                }
                                mStudentListAdapter.notifyDataSetChanged();
                            }
                        });
		mStudentListView.setOnTouchListener(touchListener);
        // Setting this scroll listener is required to ensure that during ListView scrolling,
        // we don't look for swipes.
		mStudentListView.setOnScrollListener(touchListener.makeScrollListener());
		
		
		//<-------

		mEditTextNum = (EditText)rootView.findViewById(R.id.edittext_num);
		mEditTextName = (EditText)rootView.findViewById(R.id.edittext_name);
		mTglbtnBoygirl = (ToggleButton)rootView.findViewById(R.id.tglbtn_boygirl);

		mBtnAddPerson = (Button)rootView.findViewById(R.id.btn_addperson);
		mBtnAddPerson.setOnClickListener(btnAddPersonListener);
		
		return rootView;
	}

	@Override
	public void onActivityCreated (Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		// Indicate that this fragment would like to influence the set of actions in the action bar.
		setHasOptionsMenu(true);
	}

	private ActionBar getActionBar() {
		return getActivity().getActionBar();
	}

	public void showActionBar() {
		ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(R.string.title_fillinfo);
	}

	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		if (!((MainActivity) getActivity()).isDrawerOpen()) {
			inflater.inflate(R.menu.menu_fillinfo, menu);
			showActionBar();
		}
	}

	View.OnClickListener btnAddPersonListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub    			
			int curNum = Integer.valueOf(mEditTextNum.getText().toString());
			String curName = mEditTextName.getText().toString();
			
			// TODO curName check!! NULL 이면 에러처리할것!!
			
			// 학생 추가, 리스트에 반영.
			Student student = new Student(curNum, curName, mTglbtnBoygirl.isChecked()? true : false);
			mStudentListAdapter.add(student);
			mStudentListAdapter.notifyDataSetChanged();
			mStudentListView.setSelection(mStudentListAdapter.getCount()-1);
			
			// TODO student -> 임시 Data로 저장할것...
			// *DB에 저장은 actionbar에서 확인 눌렀을 때 한다!!
			
			// 다음 입력란 마련하기...
			mEditTextNum.setText(String.valueOf(curNum+1));
			mEditTextName.setText(null);
		}
	};

}
