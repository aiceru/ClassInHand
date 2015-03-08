package com.iceru.classinhand;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by iceru on 15. 3. 8..
 */
public class SettingsFragment extends PreferenceFragment {
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
    }
}
