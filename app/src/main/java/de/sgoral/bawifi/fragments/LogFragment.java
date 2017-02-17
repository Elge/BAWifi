package de.sgoral.bawifi.fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Set;

import de.sgoral.bawifi.R;
import de.sgoral.bawifi.util.PreferencesUtil;

/**
 * A simple {@link Fragment} subclass.
 */
public class LogFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_log, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();

        TextView textView = (TextView) getView().findViewById(R.id.textview_log);
        Set<String> logEntries = PreferencesUtil.getInstance(this.getActivity()).getLogEntries();
        StringBuilder log = new StringBuilder();
        for (String logEntry : logEntries) {
            log.append(logEntry);
            log.append('\n');
        }
        textView.setText(log.toString());
    }
}
