package com.iceru.teacherschores;

import java.util.Iterator;
import java.util.TreeMap;
import java.util.TreeSet;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
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
	private MainActivity        mainActivity;
	private EditText			mEditTextNum, mEditTextName;
	private ToggleButton		mTglbtnBoygirl;
	private Button				mBtnAddPerson;
	private View				rootView;
	private ListView			mStudentListView;
	private TextView            mTotalTextView;
	private TreeMap<Integer, Student> mStudents;
	private studentListAdapter	mStudentListAdapter;

	private class studentListAdapter extends BaseAdapter {
		private LayoutInflater mInflater;
		private Context mContext;
		private TreeMap<Integer, Student> mItems;

		public studentListAdapter(Context context, TreeMap<Integer, Student> object) {
			mContext = context;
			mItems = object;
			mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		public int getCount() {
			return mItems.size();
		}

		public Object getItem(int position) {
			Student st = null;
			Integer key = 0;
			for(TreeMap.Entry<Integer, Student> entry : mItems.entrySet()) {
				st = entry.getValue();
				if(position-- < 0) return st;
			}
			return null;
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
        mStudentListAdapter = new studentListAdapter(this.getActivity(), mStudents);
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
        Log.d(this.getClass().getSimpleName(), "onCreateView()");
		View rootView = inflater.inflate(R.layout.fragment_fillstudentinfo, container, false);
		this.rootView = rootView;

		mStudentListView = (ListView)rootView.findViewById(R.id.listview_students);
		mStudentListView.setAdapter(mStudentListAdapter);

		/*SwipeDismissListViewTouchListener touchListener =
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
									mainActivity.removeStudent(removingStudent);
									setTotalText();
								}
								mStudentListAdapter.notifyDataSetChanged();
							}
						});
		mStudentListView.setOnTouchListener(touchListener);
		// Setting this scroll listener is required to ensure that during ListView scrolling,
		// we don't look for swipes.
		mStudentListView.setOnScrollListener(touchListener.makeScrollListener());*/

		mStudentListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				final Dialog longClickDialog = new Dialog(mainActivity);
				longClickDialog.setContentView(R.layout.dialog_student_edit);
				longClickDialog.setTitle(getString(R.string.title_dialog_student_edit));

				final Student target = (Student)mStudentListAdapter.getItem(position);

				TextView tv_num = (TextView)longClickDialog.findViewById(R.id.textview_dialog_student_edit_num);
				TextView tv_name = (TextView)longClickDialog.findViewById(R.id.textview_dialog_student_edit_name);
				ImageView iv_boygirl = (ImageView)longClickDialog.findViewById(R.id.imageview_dialog_student_edit_boygirl);
				tv_num.setText(String.valueOf(target.getNum()));
				tv_name.setText(target.getName().toString());
				iv_boygirl.setImageResource(target.isBoy()? R.drawable.ic_toggle_boy : R.drawable.ic_toggle_girl);

				Button btn_delete = (Button) longClickDialog.findViewById(R.id.button_dialog_student_edit_delete);
				Button btn_edit = (Button) longClickDialog.findViewById(R.id.button_dialog_student_edit_edit);
				Button btn_cancel = (Button) longClickDialog.findViewById(R.id.button_dialog_student_edit_cancel);

				btn_edit.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						mainActivity.removeStudent(target);
						setTotalText();
						mEditTextNum.setText(String.valueOf(target.getNum()));
						mEditTextName.setText(target.getName().toString());
						mTglbtnBoygirl.setChecked(target.isBoy()? true : false);
						longClickDialog.dismiss();
						mStudentListAdapter.notifyDataSetChanged();
					}
				});
				btn_delete.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						mainActivity.removeStudent(target);
						setTotalText();
						longClickDialog.dismiss();
						mStudentListAdapter.notifyDataSetChanged();
					}
				});
				btn_cancel.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						longClickDialog.dismiss();
					}
				});

				longClickDialog.show();
				return true;
			}
		});

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
		mEditTextName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if(actionId == EditorInfo.IME_ACTION_DONE) {
					addStudent();
					return true;
				}
				return false;
			}
		});

		mTglbtnBoygirl = (ToggleButton)rootView.findViewById(R.id.tglbtn_boygirl);

		mBtnAddPerson = (Button)rootView.findViewById(R.id.btn_addperson);
		mBtnAddPerson.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				addStudent();;
			}
		});

		mTotalTextView = (TextView)rootView.findViewById(R.id.textview_totalstudents);
		setTotalText();
		
		return rootView;
	}

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.d(this.getClass().getSimpleName(), "onActivityCreated()");
        super.onActivityCreated(savedInstanceState);
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
        Log.d(this.getClass().getSimpleName(), "onDestoryView()");
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

    private void addStudent() {
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
		if(mainActivity.addStudent(student)) {
			mStudentListAdapter.notifyDataSetChanged();
			mStudentListView.setSelection(mStudentListAdapter.getCount() - 1);

			// 다음 입력란 마련하기...
			mEditTextNum.setText(String.valueOf(curNum + 1));
			mEditTextName.setText(null);

			setTotalText();
		}
		else {
			Toast.makeText(mainActivity, getString(R.string.warning_existing_num), Toast.LENGTH_SHORT).show();
		}
	}

	private void setTotalText() {
		int sum = mainActivity.getNum_boys() + mainActivity.getNum_girls();
		mTotalTextView.setText("남자 " + mainActivity.getNum_boys() + "명, 여자 "
				+ mainActivity.getNum_girls()
				+ "명, 합계 " + sum + "명");
	}

}
