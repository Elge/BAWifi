package de.sgoral.bawifi.fragments;

import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.sgoral.bawifi.R;
import de.sgoral.bawifi.util.PreferencesUtil;
import de.sgoral.bawifi.util.WifiHandler;

/**
 * Created by sebastianprivat on 06.02.17.
 */

public class LogoutFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_logout, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onLogoutClicked(View view) {
        PreferencesUtil prefUtil = PreferencesUtil.getInstance(this.getActivity());
        if (prefUtil.getLogoutUrl() == null) {
            prefUtil.setLogoutUrl("http://10.10.0.1:3990/logoff");
        }
        new WifiHandler(this.getActivity()).performLogout();
    }

}
