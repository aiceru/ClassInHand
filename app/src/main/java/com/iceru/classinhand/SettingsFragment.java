package com.iceru.classinhand;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

/**
 * Created by iceru on 15. 3. 8..
 */
public class SettingsFragment extends PreferenceFragment
                                implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static SettingsFragment    thisObject = null;
    private ClassInHandApplication      application;

    public static SettingsFragment getInstance() {
        if(thisObject == null) thisObject = new SettingsFragment();
        return thisObject;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        application = ClassInHandApplication.getInstance();
        addPreferencesFromResource(R.xml.preferences);

        Preference pref = findPreference(getString(R.string.sharedpref_key_num_histories));
        pref.setSummary(getString(R.string.sharedpref_summary_current_value) +
                application.globalProperties.num_histories);
        pref = findPreference(getString(R.string.sharedpref_key_columns));
        pref.setSummary(getString(R.string.sharedpref_summary_current_value) +
                application.globalProperties.columns/2);
        pref = findPreference(getString(R.string.sharedpref_key_is_boy_right));
        pref.setSummary(getString(R.string.sharedpref_summary_current_value) +
                (application.globalProperties.isBoyRight?
                        getString(R.string.sharedpref_summary_boy_is_right) :
                        getString(R.string.sharedpref_summary_boy_is_left)));
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals(getString(R.string.sharedpref_key_num_histories))) {
            int numHistory = Integer.parseInt(sharedPreferences.getString(key, ""));
            application.globalProperties.num_histories = numHistory;
            Preference pref = findPreference(key);
            pref.setSummary(getString(R.string.sharedpref_summary_current_value) + numHistory);
        }
        else if(key.equals(getString(R.string.sharedpref_key_columns))) {
            int columns = Integer.parseInt(sharedPreferences.getString(key, ""));
            application.globalProperties.columns = columns;
            Preference pref = findPreference(key);
            pref.setSummary(getString(R.string.sharedpref_summary_current_value) + columns/2);
        }
        else if(key.equals(getString(R.string.sharedpref_key_is_boy_right))) {
            boolean isBoyRight = Boolean.parseBoolean(sharedPreferences.getString(key, ""));
            application.globalProperties.isBoyRight = isBoyRight;
            Preference pref = findPreference(key);
            pref.setSummary(getString(R.string.sharedpref_summary_current_value) +
                    (isBoyRight? getString(R.string.sharedpref_summary_boy_is_right) :
                    getString(R.string.sharedpref_summary_boy_is_left)));
        }
    }
}
