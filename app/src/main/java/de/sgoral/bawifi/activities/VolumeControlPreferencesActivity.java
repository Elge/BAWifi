package de.sgoral.bawifi.activities;

import android.app.Activity;
import android.os.Bundle;

import de.sgoral.bawifi.R;
import de.sgoral.bawifi.util.Logger;

public class VolumeControlPreferencesActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences_volume_control);
        Logger.log(this, "Activity created");
    }

}
