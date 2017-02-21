package de.sgoral.bawifi.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import de.sgoral.bawifi.R;
import de.sgoral.bawifi.util.Logger;

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
                Logger.clearLogEntries(getActivity());
                Toast.makeText(getActivity(), R.string.toast_log_cleared, Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

}
