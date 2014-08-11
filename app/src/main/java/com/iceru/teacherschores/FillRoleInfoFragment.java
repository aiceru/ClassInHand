package com.iceru.teacherschores;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

/**
 * Created by iceru on 14. 8. 11.
 */
public class FillRoleInfoFragment extends Fragment{

	public static FillRoleInfoFragment newInstance() {
		FillRoleInfoFragment fragment = new FillRoleInfoFragment();
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_fillroleinfo, container, false);

		Spinner spinner = (Spinner)rootView.findViewById(R.id.spinner_roleconsume);
		// Create an ArrayAdapter using the string array and a default spinner layout
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
				R.array.roleconsume_array, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		spinner.setAdapter(adapter);

		return rootView;
	}
}
