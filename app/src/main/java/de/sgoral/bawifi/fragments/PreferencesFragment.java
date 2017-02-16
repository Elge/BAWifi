package de.sgoral.bawifi.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.sgoral.bawifi.R;
import de.sgoral.bawifi.notifications.NotificationUtil;
import de.sgoral.bawifi.util.PreferencesUtil;

/**
 * Created by sebastianprivat on 08.02.17.
 */

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
    }
}
