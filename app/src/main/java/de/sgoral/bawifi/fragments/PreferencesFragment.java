package de.sgoral.bawifi.fragments;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import de.sgoral.bawifi.R;
import de.sgoral.bawifi.activities.VolumeControlPreferencesActivity;
import de.sgoral.bawifi.util.Logger;
import de.sgoral.bawifi.util.NotificationUtil;
import de.sgoral.bawifi.util.PreferencesUtil;

public class PreferencesFragment extends PreferenceFragment {

    private Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);

        context = this.getActivity();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(
                new SharedPreferences.OnSharedPreferenceChangeListener() {
                    @Override
                    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                        PreferencesUtil prefUtil = PreferencesUtil.getInstance(context);
                        if (prefUtil.getUsername() == null || prefUtil.getUsername().equals("")
                                || prefUtil.getPassword() == null || prefUtil.getPassword().equals("")) {
                            NotificationUtil.addMissingPreferencesNotification(context);
                        } else {
                            NotificationUtil.removeMissingPreferencesNotification(context);
                        }
                    }
                });

        Preference volumeControl = findPreference(getString(R.string.preference_key_volume_control));
        volumeControl.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                startActivity(new Intent(getActivity(), VolumeControlPreferencesActivity.class));
                return true;
            }
        });

        Logger.log(this, "PreferenceFragment created");

    }

    public void redrawScreen() {
        Logger.log(this, "Redraw requested");
        setPreferenceScreen(null);
        addPreferencesFromResource(R.xml.preferences);
    }
}
