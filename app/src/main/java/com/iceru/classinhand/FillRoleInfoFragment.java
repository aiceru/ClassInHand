package com.iceru.classinhand;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by iceru on 14. 8. 11.
 */
public class FillRoleInfoFragment extends Fragment{
	/*private MainActivity    mainActivity;
	private ArrayList<Role> mRoles;
	private RoleListAdapter mRoleListAdapter;
	private int mTotalConsume = 0;
	private TextView mTotalTextView;
	private ListView mRoleListView;
	private Spinner mSpinnerConsume;
	private EditText mEditTextName;
	private Button mBtnAddRole;*/

	/*private class RoleListAdapter extends ArrayAdapter<Role> {
		private ArrayList<Role> items;

		public RoleListAdapter(Context context, int resource, ArrayList<Role> objects) {
			super(context, resource, objects);
			this.items = objects;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.role_info, null);
			}
			Role role = items.get(position);
			if (role != null) {
				TextView tv_name = (TextView) v.findViewById(R.id.textview_rolename);
				TextView tv_consume = (TextView) v.findViewById(R.id.textview_roleconsume);
				tv_name.setText(role.getName());
				tv_consume.setText(String.valueOf(role.getConsume()));
			}
			return v;
		}
	}*/

	public static FillRoleInfoFragment newInstance() {
		FillRoleInfoFragment fragment = new FillRoleInfoFragment();
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	/*	mainActivity = (MainActivity)getActivity();
		mRoles = mainActivity.getmRoles();
		mRoleListAdapter = new RoleListAdapter(getActivity(), android.R.layout.simple_list_item_1, mRoles);*/
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_fillroleinfo, container, false);

		/*mRoleListView = (ListView)rootView.findViewById(R.id.listview_roles);
		mRoleListView.setAdapter(mRoleListAdapter);

		mSpinnerConsume = (Spinner)rootView.findViewById(R.id.spinner_roleconsume);
		// Create an ArrayAdapter using the string array and a default spinner layout
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
				R.array.roleconsume_array, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		mSpinnerConsume.setAdapter(adapter);

		mEditTextName = (EditText)rootView.findViewById(R.id.edittext_role);
		mEditTextName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if(actionId == EditorInfo.IME_ACTION_DONE) {
					addRole();
					return true;
				}
				return false;
			}
		});

		mBtnAddRole = (Button)rootView.findViewById(R.id.btn_addrole);
		mBtnAddRole.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				addRole();
			}
		});

		mTotalTextView = (TextView)rootView.findViewById(R.id.textview_totalroles);
		setTotalText();

		SwipeDismissListViewTouchListener touchListener =
				new SwipeDismissListViewTouchListener(
						mRoleListView,
						new SwipeDismissListViewTouchListener.DismissCallbacks() {
							@Override
							public boolean canDismiss(int position) {
								return true;
							}

							@Override
							public void onDismiss(ListView listView, int[] reverseSortedPositions) {
								for (int position : reverseSortedPositions) {
									Role removingRole = mRoleListAdapter.getItem(position);
									mainActivity.removeRole(removingRole);
									setTotalText();
								}
								mRoleListAdapter.notifyDataSetChanged();
							}
						});
		mRoleListView.setOnTouchListener(touchListener);
		// Setting this scroll listener is required to ensure that during ListView scrolling,
		// we don't look for swipes.
		mRoleListView.setOnScrollListener(touchListener.makeScrollListener());*/

		return rootView;
	}

	/*private void addRole() {
		String curRoleName = mEditTextName.getText().toString();
		int curConsume = Integer.valueOf(mSpinnerConsume.getSelectedItem().toString());

		if(curRoleName.isEmpty()) {
			Toast.makeText(getActivity(), R.string.warning_edittext_name_is_null, Toast.LENGTH_SHORT).show();
			return;
		}

		Role role = new Role(0, curRoleName, curConsume);
		if(mainActivity.addRole(role)) {
			mRoleListAdapter.notifyDataSetChanged();
			mRoleListView.setSelection(mRoleListAdapter.getCount() - 1);

			mEditTextName.setText(null);

			setTotalText();
		}
		else {
			Toast.makeText(mainActivity, getString(R.string.warning_existing_rolename), Toast.LENGTH_SHORT).show();
		}
	}

	private void setTotalText() {
		mTotalTextView.setText("총원 XX명, 역할인원 " +
				mainActivity.getNum_roleConsume() + "명");
	}*/
}
