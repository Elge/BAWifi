package de.sgoral.bawifi.fragments;


import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import de.sgoral.bawifi.R;
import de.sgoral.bawifi.util.Logger;
import de.sgoral.bawifi.util.userlog.UserlogChangeListener;
import de.sgoral.bawifi.util.userlog.UserlogEntry;
import de.sgoral.bawifi.util.userlog.UserlogUtil;

/**
 * A simple {@link Fragment} subclass.
 */
public class LogFragment extends Fragment {

    private ArrayAdapter<UserlogEntry> adapter;
    private UserlogChangeListener listener;
    private DrawerLayout drawer;

    private Map<UserlogEntry.Type, Boolean> filters = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        filters = new HashMap<>();
        for (UserlogEntry.Type type : UserlogEntry.Type.values()) {
            filters.put(type, true);
        }


        final View view = inflater.inflate(R.layout.fragment_log, container, false);
        drawer = (DrawerLayout) view;


        ListView list = (ListView) view.findViewById(R.id.log_list);
        adapter = new UserlogArrayAdapter(getActivity(), UserlogUtil.getLogEntries(), filters);
        list.setAdapter(adapter);

        listener = new UserlogChangeListener() {
            @Override
            public void onEntryAdded(UserlogEntry entry) {
                adapter.add(entry);
            }

            @Override
            public void onUserlogCleared() {
                adapter.clear();
                adapter.addAll(UserlogUtil.getLogEntries());
            }
        };

        final ListView filter = (ListView) view.findViewById(R.id.log_filter);
        filter.setAdapter(new BaseAdapter() {

            @Override
            public int getCount() {
                return UserlogEntry.Type.values().length;
            }

            @Override
            public UserlogEntry.Type getItem(int position) {
                return UserlogEntry.Type.values()[position];
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.element_log_filter, parent, false);
                }
                final UserlogEntry.Type item = getItem(position);

                CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.log_filter_checkbox);
                checkBox.setText(item.getValue());
                checkBox.setTextColor(Color.WHITE);
                checkBox.setChecked(filters.get(item));
                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        updateFilter(item, isChecked);
                    }
                });

                return convertView;
            }
        });

        Logger.log(this.getActivity(), this, "View created");
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.actionbar_log, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.log_filter_button:
                toggleDrawer();
                return true;
            case R.id.log_reset_button:
                clearLog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void toggleDrawer() {
        if (drawer.isDrawerOpen(GravityCompat.END)) {
            drawer.closeDrawer(GravityCompat.END);
        } else {
            drawer.openDrawer(GravityCompat.END);
        }
    }

    private void clearLog() {
        UserlogUtil.clearLogEntries(getActivity());

        Toast.makeText(getActivity(), R.string.toast_log_cleared, Toast.LENGTH_SHORT).show();
    }

    private void updateFilter(UserlogEntry.Type type, boolean showEntries) {
        filters.put(type, showEntries);
        adapter.getFilter().filter(null);
    }

    @Override
    public void onResume() {
        super.onResume();

        UserlogUtil.addListener(listener);
        Logger.log(this.getActivity(), this, "Fragment resumed");
    }

    @Override
    public void onPause() {
        super.onPause();

        UserlogUtil.removeListener(listener);
        Logger.log(this.getActivity(), this, "Fragment paused");
    }
}
