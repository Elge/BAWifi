package de.sgoral.bawifi.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.sgoral.bawifi.R;

/**
 * Created by sebastianprivat on 06.02.17.
 */

public class MainMenuFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        PreferenceManager.getDefaultSharedPreferences(this.getContext());

        return inflater.inflate(R.layout.mainmenuview, container, false);
    }
}
