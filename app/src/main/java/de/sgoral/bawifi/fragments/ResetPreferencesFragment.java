package de.sgoral.bawifi.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import de.sgoral.bawifi.R;
import de.sgoral.bawifi.util.PreferencesUtil;

/**
 * Created by sebastianprivat on 17.02.17.
 */

public class ResetPreferencesFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reset_preferences, container, false);

        Button b = (Button) view.findViewById(R.id.button_reset_preferences);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PreferencesUtil.getInstance(getActivity()).resetPreferences();
            }
        });

        return view;
    }
}
