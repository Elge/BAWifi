package de.sgoral.bawifi.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import de.sgoral.bawifi.R;

/**
 * Created by sebastianprivat on 06.02.17.
 */

public class MainMenuActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
    }

    public void onPreferencesButtonClicked(View view) {
        Intent intent = new Intent(this, PreferencesActivity.class);
        startActivity(intent);
    }

    public void onLogButtonClicked(View view) {
        Intent intent = new Intent(this, LogActivity.class);
        startActivity(intent);
    }
}
