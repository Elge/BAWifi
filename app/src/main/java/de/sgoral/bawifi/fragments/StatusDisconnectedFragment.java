package de.sgoral.bawifi.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.sgoral.bawifi.R;
import de.sgoral.bawifi.util.Logger;

/**
 * Created by sebastianprivat on 08.02.17.
 */

public class StatusDisconnectedFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Logger.log(this, "View created");
        return inflater.inflate(R.layout.fragment_status_disconnected, container, false);
    }
}
