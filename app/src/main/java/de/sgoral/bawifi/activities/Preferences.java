package de.sgoral.bawifi.activities;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

import de.sgoral.bawifi.R;

// TODO move to fragments
public class Preferences extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, true);
        addPreferencesFromResource(R.xml.preferences);
    }
}
