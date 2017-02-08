package de.sgoral.bawifi.fragments;

import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.sgoral.bawifi.R;

/**
 * Created by sebastianprivat on 06.02.17.
 */

public class MainMenuFragment extends Fragment {

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO fix api v.23 dependency
        PreferenceManager.getDefaultSharedPreferences(getContext());

        return inflater.inflate(R.layout.fragment_main_menu, container, false);
    }
}
