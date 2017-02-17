package de.sgoral.bawifi.activities;

import android.app.Activity;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.SwitchPreference;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Switch;

import de.sgoral.bawifi.R;
import de.sgoral.bawifi.fragments.ClearLogFragment;
import de.sgoral.bawifi.util.Logger;
import de.sgoral.bawifi.util.PreferencesUtil;

/**
 * Activity for the preferences.
 */
public class PreferencesActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);
    }

    public void onClearLogButtonClicked(View view) {
        PreferencesUtil.getInstance(this).clearLogEntries();
        ((ClearLogFragment) this.getFragmentManager().findFragmentById(R.id.fragment_clear_log))
                .updateButtonVisibility();
    }

    public void onResetPreferencesClicked(View view) {
        PreferencesUtil.getInstance(this).resetPreferences();
    }
}
