package de.sgoral.bawifi.activities;

import android.app.Activity;
import android.os.Bundle;

import de.sgoral.bawifi.R;
import de.sgoral.bawifi.util.Logger;

/**
 * Activity for the log view.
 */
public class LogActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);
        Logger.log(this, "Activity created");
    }
}
