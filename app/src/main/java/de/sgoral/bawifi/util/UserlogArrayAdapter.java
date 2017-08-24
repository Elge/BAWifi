package de.sgoral.bawifi.util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.sgoral.bawifi.R;
import de.sgoral.bawifi.util.Logger;
import de.sgoral.bawifi.util.userlog.UserlogEntry;

/**
 * Custom adapter for {@link de.sgoral.bawifi.fragments.LogFragment} to display the userlog in a
 * {@link android.widget.ListView}.
 */
public class UserlogArrayAdapter extends ArrayAdapter<UserlogEntry> implements Filterable {

    public static final String DATEFORMAT_SKELETON = "EEE Hms";
    private final String pattern;
    private final Map<UserlogEntry.Type, Boolean> filters;
    private final List<UserlogEntry> entries;

    /**
     * Creates a new adapter and initialises the DateFormat pattern.
     *  @param context
     * @param entries The list of userlog entries to display.
     * @param filters
     */
    public UserlogArrayAdapter(Context context, List<UserlogEntry> entries, Map<UserlogEntry.Type, Boolean> filters) {
        super(context, 0, entries);
        pattern = DateFormat.getBestDateTimePattern(Locale.getDefault(), DATEFORMAT_SKELETON);
        Logger.log(context, this, "Created");
        Logger.log(context, this, "DateFormat pattern: ", pattern);
        this.filters = filters;
        this.entries = new ArrayList<>(entries);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                ArrayList<UserlogEntry> tempList = new ArrayList<>();

                if (entries != null) {
                    for (UserlogEntry entry : entries) {
                        if (filters.get(entry.getType())) {
                            tempList.add(entry);
                        }
                    }

                    filterResults.values = tempList;
                    filterResults.count = tempList.size();
                }

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                clear();
                addAll((ArrayList<UserlogEntry>) results.values);
            }
        };
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        UserlogEntry item = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.element_log_entry, parent, false);
        }

        TextView textViewTime = (TextView) convertView.findViewById(R.id.log_entry_time);
        TextView textViewMessage = (TextView) convertView.findViewById(R.id.log_entry_message);

        textViewTime.setText(DateFormat.format(pattern, item.getTime()));
        textViewMessage.setText(item.getMessage());

        return convertView;
    }
}
