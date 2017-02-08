package de.sgoral.bawifi.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import de.sgoral.bawifi.R;

/**
 * Allows easy access to the local application preferences.
 */
public class PreferencesUtil {

    /**
     * The maximum size before the log gets shrunk.
     */
    private static final int MAX_LOG_ENTRIES = 250;
    /**
     * The number of entries that will be removed from the log when shrinking.
     */
    private static final int NUMBER_OF_DROPPED_LOG_ENTRIES = 100;

    private final Context context;

    private PreferencesUtil(Context context) {
        this.context = context;
    }

    /**
     * Creates and returns a new instance of the utility.
     *
     * @param context The application environment.
     * @return A new instance of the {@link PreferencesUtil}.
     */
    public static PreferencesUtil getInstance(Context context) {
        return new PreferencesUtil(context);
    }

    /**
     * Loads the default SharesPreferences for this application.
     *
     * @return The {@link SharedPreferences} object.
     */
    public SharedPreferences getSharesPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(this.context);
    }

    /**
     * Loads a preference value from the default shared preferences file.
     *
     * @param resourceId The resource ID to retrieve a preference for.
     * @return The default {@link SharedPreferences} object.
     */
    public String getPreference(int resourceId) {
        SharedPreferences preferences = getSharesPreferences();
        return preferences.getString(this.context.getString(resourceId), null);
    }

    /**
     * Loads the expected network SSID from the preferences.
     *
     * @return The network SSID to automatically authenticate the user for.
     */
    public String getSSID() {
        return getPreference(R.string.preference_key_ssid);
    }

    /**
     * Loads the start point URL from where we will log in to the network.
     *
     * @return The URL to open.
     */
    public String getURL() {
        return getPreference(R.string.preference_key_url);
    }

    /**
     * The username to authenticate with.
     *
     * @return The username.
     */
    public String getUsername() {
        return getPreference(R.string.preference_key_username);
    }

    /**
     * The plaintext password to authenticate with.
     *
     * @return The password.
     */
    public String getPassword() {
        // TODO encrypt password. How? POST request requires plaintext password.
        return getPreference(R.string.preference_key_password);
    }

    /**
     * The url that de-authenticates the user from the WiFi network.
     *
     * @return The URL.
     */
    public String getLogoutUrl() {
        return getPreference(R.string.preference_key_logouturl);
    }

    /**
     * Saves the logout url in the application preferences.
     *
     * @param url The URL to store.
     */
    public void setLogoutUrl(String url) {
        SharedPreferences.Editor editor = getSharesPreferences().edit();
        editor.putString(
                this.context.getString(R.string.preference_key_logouturl), url);
        editor.apply();
    }

    /**
     * The url that shows the current connection status.
     *
     * @return The URL.
     */
    public String getStatusUrl() {
        return getPreference(R.string.preference_key_statusurl);
    }

    /**
     * Saves the status url in the application preferences.
     *
     * @param url The URL to store.
     */
    public void setStatusUrl(String url) {
        SharedPreferences.Editor editor = getSharesPreferences().edit();
        editor.putString(
                this.context.getString(R.string.preference_key_statusurl), url);
        editor.apply();
    }

    /**
     * Retrieves the set of log entries from the application preferences.
     *
     * @return The set of log messages.
     */
    public Set<String> getLogEntries() {
        Set<String> log = getSharesPreferences().getStringSet(context.getString(R.string.log_file), null);
        if (log == null) {
            return new HashSet<>();
        }
        return new HashSet<>(log);
    }

    /**
     * Updates the set of log messages in the application preferences.
     *
     * @param log The new log to save.
     */
    public void setLogEntries(Set<String> log) {
        SharedPreferences.Editor editor = getSharesPreferences().edit();
        editor.putStringSet(context.getString(R.string.log_file), log);
        editor.apply();
    }

    /**
     * Adds a new message to the bottom of the log. Truncates the log if it has grown beyond specifications.
     *
     * @param message The message to add to the log.
     */
    public void addLogEntry(String message) {
        Set<String> log = getLogEntries();
        Iterator<String> iterator = log.iterator();
        if (log.size() > MAX_LOG_ENTRIES) {
            for (int i = 0; i < NUMBER_OF_DROPPED_LOG_ENTRIES && iterator.hasNext(); i++) {
                iterator.next();
            }

            Set<String> newLog = new HashSet<>();
            while (iterator.hasNext()) {
                newLog.add(iterator.next());
            }
            newLog.add(message);
            setLogEntries(newLog);
        } else {
            log.add(message);
            setLogEntries(log);
        }
    }

}
