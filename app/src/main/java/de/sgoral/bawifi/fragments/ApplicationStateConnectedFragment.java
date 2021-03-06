package de.sgoral.bawifi.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import de.sgoral.bawifi.R;
import de.sgoral.bawifi.activities.PreferencesActivity;
import de.sgoral.bawifi.util.Logger;
import de.sgoral.bawifi.util.PreferencesUtil;
import de.sgoral.bawifi.util.NetworkUtil;

public class ApplicationStateConnectedFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_application_state_connected, container, false);

        Button b = (Button) view.findViewById(R.id.button_authenticate);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PreferencesUtil.getInstance(getActivity()).isValidConfiguration()) {
                    NetworkUtil.performLogin(getActivity());
                } else {
                    startActivity(new Intent(getActivity(), PreferencesActivity.class));
                    Toast.makeText(getActivity(), R.string.toast_username_password_required, Toast.LENGTH_LONG).show();
                }
            }
        });

        Logger.log(this.getActivity(), this, "View created");
        return view;
    }

}
