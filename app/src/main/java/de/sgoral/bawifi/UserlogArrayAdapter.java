package de.sgoral.bawifi;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

import de.sgoral.bawifi.util.Logger;
import de.sgoral.bawifi.util.UserlogEntry;

/**
 * Custom adapter for {@link de.sgoral.bawifi.fragments.LogFragment} to display the userlog in a
 * {@link android.widget.ListView}.
 */
public class UserlogArrayAdapter extends ArrayAdapter<UserlogEntry> {

    public static final String DATEFORMAT_SKELETON = "EEE Hms";
    private final String pattern;

    /**
     * Creates a new adapter and initialises the DateFormat pattern.
     *
     * @param context
     * @param entries The list of userlog entries to display.
     */
    public UserlogArrayAdapter(Context context, List<UserlogEntry> entries) {
        super(context, 0, entries);
        pattern = DateFormat.getBestDateTimePattern(Locale.getDefault(), DATEFORMAT_SKELETON);
        Logger.log(this, "Created");
        Logger.log(this, "DateFormat pattern: ", pattern);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
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
