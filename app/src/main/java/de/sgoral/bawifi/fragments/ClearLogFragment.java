package de.sgoral.bawifi.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.sgoral.bawifi.R;
import de.sgoral.bawifi.util.PreferencesUtil;

/**
 * Created by sebastianprivat on 15.02.17.
 */

public class ClearLogFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_clear_log, container, false);
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
