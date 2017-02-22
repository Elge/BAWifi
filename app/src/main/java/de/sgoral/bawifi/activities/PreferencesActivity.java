package de.sgoral.bawifi.activities;

import android.app.Activity;
import android.os.Bundle;

import de.sgoral.bawifi.R;
import de.sgoral.bawifi.util.Logger;

/**
 * Activity for the preferences.
 */
public class PreferencesActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);
        Logger.log(this, "Activity created");
    }
}
