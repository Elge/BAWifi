package de.sgoral.bawifi.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import de.sgoral.bawifi.R;
import de.sgoral.bawifi.activities.PreferencesActivity;
import de.sgoral.bawifi.util.PreferencesUtil;
import de.sgoral.bawifi.util.WifiUtil;

public class StatusConnectedFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_status_connected, container, false);

        Button b = (Button) view.findViewById(R.id.button_authenticate);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PreferencesUtil.getInstance(getActivity()).isValidConfiguration()) {
                    new WifiUtil(getActivity()).performLogin();
                } else {
                    startActivity(new Intent(getActivity(), PreferencesActivity.class));
                }
            }
        });

        return view;
    }

}
