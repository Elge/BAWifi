package de.sgoral.bawifi.activities;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import de.sgoral.bawifi.R;
import de.sgoral.bawifi.fragments.PreferencesFragment;
import de.sgoral.bawifi.util.Logger;

/**
 * Activity for the preferences.
 */
public class PreferencesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_preferences);
        getFragmentManager().beginTransaction().add(R.id.container_preferences, new PreferencesFragment()).commit();
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar_preferences));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Logger.log(this, this, "Activity created");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() == 0) {
            super.onBackPressed();
        } else {
            getFragmentManager().popBackStack();
        }
    }

    public void switchFragment(Fragment fragment) {
        getFragmentManager().beginTransaction()
                .replace(R.id.container_preferences, fragment)
                .addToBackStack(null)
                .commit();
    }
}
