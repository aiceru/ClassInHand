package com.iceru.teacherschores;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

/**
 * @author iceru
 * Fragment providing for user to input students' information
 */
public class FillStudentInfoFragment extends Fragment {
	private EditText			mEditTextNum, mEditTextName;
	private ToggleButton		mTglbtnBoygirl;
	private Button				mBtnAddPerson;
	private View				rootView;
	private ListView			mStudentListView;
	private TreeSet<Student>    mStudents;
	private studentListAdapter	mStudentListAdapter;

	private SharedPreferences mSharedPrefNameList;
	private SharedPreferences mSharedPrefBoygirlList;

	private static final String ARG_SECTION_NUMBER = "section_number";

	View.OnClickListener btnAddPersonListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			add_student();
		}
	};

	TextView.OnEditorActionListener editTextNameEditorActionListener = new TextView.OnEditorActionListener() {
		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			if(actionId == EditorInfo.IME_ACTION_DONE) {
				add_student();
				return true;
			}
			return false;
		}
	};

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

	private class studentListAdapter extends BaseAdapter {
		private LayoutInflater mInflater;
		private Context mContext;

		private TreeSet<Student> mItems = new TreeSet<Student>(new Comparator<Student>() {
			@Override
			public int compare(Student lhs, Student rhs) {
				return lhs.num - rhs.num;
			}
		});

		public studentListAdapter(Context context, TreeSet<Student> object) {
			mContext = context;
			mItems = object;
			mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		public int getCount() {
			return mItems.size();
		}

		public Object getItem(int position) {
			Student st = null;
			Iterator<Student> i = mItems.iterator();
			while(i.hasNext() && position >= 0) {
				position--;
				st = i.next();
			}
			return st;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View v, ViewGroup parent) {
			View view;
			if(v == null) {
				view = mInflater.inflate(R.layout.student_info, null);
			}
			else {
				view = v;
			}

			final Student student = (Student)this.getItem(position);

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

	/**
	 * Returns a new instance of this fragment for the given section
	 * number.
	 */
	public static FillStudentInfoFragment newInstance() {
		FillStudentInfoFragment fragment = new FillStudentInfoFragment();
		return fragment;
	}

	public FillStudentInfoFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//mStudents = new ArrayList<Student>();
		mStudents = new TreeSet<Student>(new Comparator<Student>() {
			@Override
			public int compare(Student lhs, Student rhs) {
				return lhs.num - rhs.num;
			}
		});
		mStudentListAdapter = new studentListAdapter(this.getActivity(), mStudents);

		mSharedPrefNameList = getActivity().getSharedPreferences(getString(R.string.sharedpref_name_list), Context.MODE_PRIVATE);
		mSharedPrefBoygirlList = getActivity().getSharedPreferences(getString(R.string.sharedpref_boygirl_list), Context.MODE_PRIVATE);

		Map<String, ?> allEntries = mSharedPrefNameList.getAll();
		for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
			Student student = new Student(Integer.valueOf(entry.getKey()),
					entry.getValue().toString(),
					mSharedPrefBoygirlList.getBoolean(entry.getKey(), true));
			mStudents.add(student);
		}

		ActionBar actionBar = getActionBar();
		ActionBar.TabListener tabListener = new ActionBar.TabListener() {
			public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
				// show the given tab
			}

			public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
				// hide the given tab
			}

			public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
				// probably ignore this event
			}
		};

		actionBar.addTab(actionBar.newTab().
				setText(R.string.tabtitle_studentinfo).setTabListener(tabListener));
		actionBar.addTab(actionBar.newTab().
				setText(R.string.tabtitle_roleinfo).setTabListener(tabListener));
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_fillstudentinfo, container, false);
		this.rootView = rootView;

		mStudentListView = (ListView)rootView.findViewById(R.id.listview_students);
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
									Student removingStudent = (Student)mStudentListAdapter.getItem(position);
									if(mStudents.remove(removingStudent)) {
										SharedPreferences.Editor editor;
										editor = mSharedPrefNameList.edit();
										editor.remove(String.valueOf(removingStudent.getNum()));
										editor.apply();
										editor = mSharedPrefBoygirlList.edit();
										editor.remove(String.valueOf(removingStudent.getNum()));
										editor.apply();
									}
									else {
										// remove fails... won't reach here
									}
								}
								mStudentListAdapter.notifyDataSetChanged();
							}
						});
		mStudentListView.setOnTouchListener(touchListener);
		// Setting this scroll listener is required to ensure that during ListView scrolling,
		// we don't look for swipes.
		mStudentListView.setOnScrollListener(touchListener.makeScrollListener());

		mEditTextNum = (EditText)rootView.findViewById(R.id.edittext_num);
		mEditTextNum.setOnEditorActionListener(new EditText.OnEditorActionListener() {
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				// TODO Auto-generated method stub
				if (actionId == EditorInfo.IME_ACTION_NEXT) {
					mEditTextName.requestFocus();

					return false;
				}
				return false;
			}
		});

		mEditTextName = (EditText)rootView.findViewById(R.id.edittext_name);
		mEditTextName.setOnEditorActionListener(editTextNameEditorActionListener);

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

	private void add_student() {
		String curNumString = mEditTextNum.getText().toString();
		String curName = mEditTextName.getText().toString();

		if(curNumString.isEmpty()) {
			Toast.makeText(getActivity(), R.string.warning_edittext_num_is_null, Toast.LENGTH_SHORT).show();
			return;
		}

		if(curName.isEmpty()) {
			Toast.makeText(getActivity(), R.string.warning_edittext_name_is_null, Toast.LENGTH_SHORT).show();
			return;
		}

		int curNum = Integer.valueOf(mEditTextNum.getText().toString());

		// TODO 번호 중복검사!!

		// 학생 추가, 리스트에 반영.
		Student student = new Student(curNum, curName, mTglbtnBoygirl.isChecked()? true : false);
		if(mStudents.add(student)) {
			// TODO student -> 임시 Data로 저장할것...
			SharedPreferences.Editor editor;
			editor = mSharedPrefNameList.edit();
			editor.putString(String.valueOf(student.getNum()), student.getName());
			editor.apply();
			editor = mSharedPrefBoygirlList.edit();
			editor.putBoolean(String.valueOf(student.getNum()), student.isBoy());
			editor.apply();
			// TODO DB에 저장은 actionbar에서 확인 눌렀을 때 한다!!

			mStudentListAdapter.notifyDataSetChanged();
			mStudentListView.setSelection(mStudentListAdapter.getCount() - 1);

			// 다음 입력란 마련하기...
			mEditTextNum.setText(String.valueOf(curNum + 1));
			mEditTextName.setText(null);
		}
		else {
			Toast.makeText(getActivity(), getString(R.string.warning_existing_num), Toast.LENGTH_SHORT).show();
		}
	}

}
