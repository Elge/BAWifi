package de.sgoral.bawifi.fragments;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import de.sgoral.bawifi.R;
import de.sgoral.bawifi.util.PreferencesUtil;

/**
 * Created by sebastianprivat on 15.02.17.
 */

public class ClearLogFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_clear_log, container, false);

        Button b = (Button) view.findViewById(R.id.button_clear_log);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PreferencesUtil.getInstance(getActivity()).clearLogEntries();
            }
        });

        PreferencesUtil.getInstance(getActivity()).getSharesPreferences().
                registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
                    @Override
                    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                        if (key.equals(getActivity().getString(R.string.log_file))) {
                            updateButtonVisibility();
                        }
                    }
                });

        return view;
    }

    public void updateButtonVisibility() {
        View button = this.getView().findViewById(R.id.button_clear_log);
        if (PreferencesUtil.getInstance(this.getActivity()).getLogEntries().size() == 0) {
            button.setVisibility(View.GONE);
        } else {
            button.setVisibility(View.VISIBLE);
        }
    }
}
