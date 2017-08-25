package de.sgoral.bawifi.fragments;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import de.sgoral.bawifi.R;
import de.sgoral.bawifi.dialogs.RequestDndPermissionDialog;
import de.sgoral.bawifi.util.RingerModeUtil;

public class PreferencesNotificationsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences_notifications);
    }
}
