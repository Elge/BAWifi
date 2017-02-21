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
import de.sgoral.bawifi.util.UserlogChangeListener;
import de.sgoral.bawifi.util.UserlogEntry;

/**
 * Created by sebastianprivat on 15.02.17.
 */

public class ClearLogFragment extends Fragment {


    private UserlogChangeListener listener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_clear_log, container, false);

        Button b = (Button) view.findViewById(R.id.button_clear_log);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Logger.clearLogEntries(getActivity());
            }
        });

        listener = new UserlogChangeListener() {
            @Override
            public void onEntryAdded(UserlogEntry entry) {
                updateButtonVisibility();
            }

            @Override
            public void onUserlogCleared() {
                updateButtonVisibility();
            }
        };

        return view;
    }

    /**
     * Updates the clear log button visibility. If there are userlog entries, the button is visible.
     * Otherwise it is gone.
     */
    public void updateButtonVisibility() {
        View button = this.getView().findViewById(R.id.button_clear_log);
        if (Logger.getUserlog().size() == 0) {
            button.setVisibility(View.GONE);
        } else {
            button.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        updateButtonVisibility();
        Logger.addListener(listener);
    }

    @Override
    public void onPause() {
        super.onPause();

        Logger.removeListener(listener);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        listener = null;
    }

}
