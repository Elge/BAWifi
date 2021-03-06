package de.sgoral.bawifi.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import de.sgoral.bawifi.R;
import de.sgoral.bawifi.util.Logger;
import de.sgoral.bawifi.util.NetworkUtil;

public class ApplicationStateAuthenticatedFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_application_state_authenticated, container, false);

        Button b = (Button) view.findViewById(R.id.button_deauthenticate);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NetworkUtil.performLogout(getActivity());
            }
        });

        Logger.log(this.getActivity(), this, "View created");
        return view;
    }
}
