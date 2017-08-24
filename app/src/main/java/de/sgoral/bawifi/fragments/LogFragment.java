package de.sgoral.bawifi.fragments;


import android.app.Fragment;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import de.sgoral.bawifi.R;
import de.sgoral.bawifi.util.UserlogArrayAdapter;
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

    private Map<UserlogEntry.Type, Boolean> filters = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        filters = new HashMap<>();
        for (UserlogEntry.Type type : UserlogEntry.Type.values()) {
            filters.put(type, true);
        }


        final View view = inflater.inflate(R.layout.fragment_log, container, false);

        ListView list = (ListView) view.findViewById(R.id.log_list);
        adapter = new UserlogArrayAdapter(getActivity(), UserlogUtil.getLogEntries(), filters);
        list.setAdapter(adapter);

        listener = new UserlogChangeListener() {
            @Override
            public void onEntryAdded(UserlogEntry entry) {
                adapter.add(entry);
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

        FloatingActionButton filterButton = (FloatingActionButton) view.findViewById(R.id.log_filter_button);
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawerLayout layout = (DrawerLayout) view;
                layout.openDrawer(GravityCompat.END);
            }
        });

        Logger.log(this.getActivity(), this, "View created");
        return view;
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
