package de.sgoral.bawifi.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import de.sgoral.bawifi.R;
import de.sgoral.bawifi.activities.PreferencesActivity;
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
                        if (key.equals(context.getString(R.string.preference_key_username))
                                || key.equals(context.getString(R.string.preference_key_password))) {

                            PreferencesUtil prefUtil = PreferencesUtil.getInstance(context);
                            if (prefUtil.getUsername() == null || prefUtil.getUsername().equals("")
                                    || prefUtil.getPassword() == null || prefUtil.getPassword().equals("")) {
                                NotificationUtil.addMissingPreferencesNotification(context);
                            } else {
                                NotificationUtil.removeMissingPreferencesNotification(context);
                            }
                        }
                    }
                });

        Preference.OnPreferenceClickListener clickListener = new Preference.OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference preference) {
                if (preference.getKey().equals(context.getString(R.string.preference_key_notifications))) {
                    ((PreferencesActivity) getActivity()).switchFragment(new PreferencesNotificationsFragment());
                    return true;
                }
                if (preference.getKey().equals(context.getString(R.string.preference_key_volume_control))) {
                    ((PreferencesActivity) getActivity()).switchFragment(new PreferencesVolumeControlFragment());
                    return true;
                }
                return false;
            }

        };

        findPreference(context.getString(R.string.preference_key_notifications))
                .setOnPreferenceClickListener(clickListener);
        findPreference(context.getString(R.string.preference_key_volume_control))
                .setOnPreferenceClickListener(clickListener);

        Logger.log(this.getActivity(), this, "PreferenceFragment created");

    }
}
