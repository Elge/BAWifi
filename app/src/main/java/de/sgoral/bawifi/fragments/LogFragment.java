package de.sgoral.bawifi.fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import de.sgoral.bawifi.R;
import de.sgoral.bawifi.UserlogArrayAdapter;
import de.sgoral.bawifi.util.Logger;
import de.sgoral.bawifi.util.UserlogChangeListener;
import de.sgoral.bawifi.util.UserlogEntry;
import de.sgoral.bawifi.util.UserlogUtil;

/**
 * A simple {@link Fragment} subclass.
 */
public class LogFragment extends Fragment {

    private ArrayAdapter<UserlogEntry> adapter;
    private UserlogChangeListener listener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_log, container, false);

        ListView list = (ListView) view.findViewById(R.id.log_list);
        adapter = new UserlogArrayAdapter(getActivity(), UserlogUtil.getLogEntries());
        list.setAdapter(adapter);

        listener = new UserlogChangeListener() {
            @Override
            public void onEntryAdded(UserlogEntry entry) {
                adapter.add(entry);
            }
        };

        Logger.log(this, "View created");
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        UserlogUtil.addListener(listener);
        Logger.log(this, "Fragment resumed");
    }

    @Override
    public void onPause() {
        super.onPause();

        UserlogUtil.removeListener(listener);
        Logger.log(this, "Fragment paused");
    }
}
