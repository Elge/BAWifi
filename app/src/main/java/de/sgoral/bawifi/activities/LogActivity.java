package de.sgoral.bawifi.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import de.sgoral.bawifi.R;
import de.sgoral.bawifi.util.Logger;

/**
 * Activity for the log view.
 */
public class LogActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_log);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar_log));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Logger.log(this, this, "Activity created");
    }
}
