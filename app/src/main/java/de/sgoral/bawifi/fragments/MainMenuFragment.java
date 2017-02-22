package de.sgoral.bawifi.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import de.sgoral.bawifi.R;
import de.sgoral.bawifi.activities.LogActivity;
import de.sgoral.bawifi.activities.PreferencesActivity;
import de.sgoral.bawifi.util.Logger;

/**
 * Created by sebastianprivat on 06.02.17.
 */

public class MainMenuFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        PreferenceManager.getDefaultSharedPreferences(this.getActivity());

        View view = inflater.inflate(R.layout.fragment_main_menu, container, false);

        Button b = (Button) view.findViewById(R.id.button_preferences);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PreferencesActivity.class);
                startActivity(intent);
            }
        });

        b = (Button) view.findViewById(R.id.button_log);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), LogActivity.class);
                startActivity(intent);
            }
        });

        Logger.log(this, "View created");
        return view;
    }
}
