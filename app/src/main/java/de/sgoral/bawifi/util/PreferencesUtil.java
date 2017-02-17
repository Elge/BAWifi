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

    public static final String SUFFIX_DEFAULT = "_default";
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
     * Loads a string preference value from the default shared preferences file.
     *
     * @param resourceId The resource ID to retrieve a preference for.
     * @return The preference value, or null if unset.
     */
    public String getStringPreference(int resourceId) {
        return getStringPreference(context.getString(resourceId));
    }

    /**
     * Loads a boolean preference value from the default shared preferences file.
     *
     * @param resourceId The resource ID to retrieve a preference for.
     * @return The preference value, or false if unset.
     */
    public boolean getBooleanPreference(int resourceId) {
        return getBooleanPreference(context.getString(resourceId));
    }


    /**
     * Loads a string preference value from the default shared preferences file.
     *
     * @param resource The resource to retrieve a preference for.
     * @return The preference value, or false if unset.
     */
    public String getStringPreference(String resource) {
        SharedPreferences preferences = getSharesPreferences();
        return preferences.getString(resource, null);
    }

    /**
     * Loads a boolean preference value from the default shared preferences file.
     *
     * @param resource The resource to retrieve a preference for.
     * @return The preference value, or null if unset.
     */
    public boolean getBooleanPreference(String resource) {
        SharedPreferences preferences = getSharesPreferences();
        return preferences.getBoolean(resource, false);
    }

    /**
     * Loads the preference value if it is set, or a default value if not.
     *
     * @param resourceId        The resource ID to retrieve a preference for.
     * @param defaultResourceId The resource ID of the default value.
     * @return The preference value, the default value or null if none are set.
     */
    public String getPreferenceOrDefault(int resourceId, int defaultResourceId) {
        String value = getStringPreference(resourceId);
        if (value == null) {
            value = context.getString(defaultResourceId);
        }
        return value;
    }

    /**
     * Loads the expected network SSID from the preferences.
     *
     * @return The network SSID to automatically authenticate the user for.
     */
    public String getSSID() {
        return context.getString(R.string.network_ssid);
    }

    /**
     * Loads the start point URL from where we will log in to the network.
     *
     * @return The URL to open.
     */
    public String getLoginUrl() {
        return getPreferenceOrDefault(R.string.preference_key_loginurl, R.string.loginurl_default);
    }

    /**
     * The username to authenticate with.
     *
     * @return The username.
     */
    public String getUsername() {
        return getStringPreference(R.string.preference_key_username);
    }

    /**
     * The plaintext password to authenticate with.
     *
     * @return The password.
     */
    public String getPassword() {
        // TODO encrypt password. How? POST request requires plaintext password.
        return getStringPreference(R.string.preference_key_password);
    }

    /**
     * The url that de-authenticates the user from the WiFi network.
     *
     * @return The URL.
     */
    public String getLogoutUrl() {
        return getPreferenceOrDefault(R.string.preference_key_logouturl, R.string.logouturl_default);
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
        return getPreferenceOrDefault(R.string.preference_key_statusurl, R.string.statusurl_default);
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
        editor.commit();
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

    /**
     * Removes all log entries.
     */
    public void clearLogEntries() {
        setLogEntries(new HashSet<String>());
    }

    /**
     * Checks if notifications are enabled.
     *
     * @return true if notifications are enabled.
     */
    public boolean isNotificationsEnabled() {
        return getBooleanPreference(R.string.preference_key_notifications_all);
    }

    /**
     * Checks if connected notifications are enabled.
     *
     * @return true if connected notifications are enabled.
     */
    public boolean isConnectedNotificationsEnabled() {
        return isNotificationsEnabled() && getBooleanPreference(R.string.preference_key_notification_connected);
    }

    /**
     * Checks if disconnected notifications are enabled.
     *
     * @return true if disconnected notifications are enabled.
     */
    public boolean isDisconnectedNotificationsEnabled() {
        return isNotificationsEnabled() && getBooleanPreference(R.string.preference_key_notification_disconnected);
    }

    /**
     * Checks if authenticated notifications are enabled.
     *
     * @return true if authenticated notifications are enabled.
     */
    public boolean isAuthenticatedNotificationsEnabled() {
        return isNotificationsEnabled() && getBooleanPreference(R.string.preference_key_notification_authenticated);
    }

    /**
     * Checks if authentication failed notifications are enabled.
     *
     * @return true if connected notifications are enabled.
     */
    public boolean isAuthenticationFailedNotificationsEnabled() {
        return isNotificationsEnabled() && getBooleanPreference(R.string.preference_key_notification_authentication_failed);
    }

    /**
     * Checks if deauthentication notifications are enabled.
     *
     * @return true if deauthentication notifications are enabled.
     */
    public boolean isDeauthenticationNotificationsEnabled() {
        return isNotificationsEnabled() && getBooleanPreference(R.string.preference_key_notification_deauthenticated);
    }

    /**
     * Checks if deauthentication failed notifications are enabled.
     *
     * @return true if deauthentication failed notifications are enabled.
     */
    public boolean isDeauthenticationFailedNotificationsEnabled() {
        return isNotificationsEnabled() && getBooleanPreference(R.string.preference_key_notification_deauthentication_failed);
    }

    /**
     * Checks if missing settings notifications are enabled.
     *
     * @return true if missing settings notifications are enabled.
     */
    public boolean isMissingSettingsNotificationsEnabled() {
        return isNotificationsEnabled() && getBooleanPreference(R.string.preference_key_notification_missing_settings);
    }

    /**
     * Loads default values for all preferences.
     */
    public void initialisePreferences() {
        PreferenceManager.setDefaultValues(context, R.xml.preferences, true);
    }

    /**
     * Checks if a username and a password have been set.
     *
     * @return true if the data is entered
     */
    public boolean isValidConfiguration() {
        return getUsername() != null && getPassword() != null;
    }

    /**
     * Resets all preferences to default values.
     */
    public void resetPreferences() {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.clear();
        editor.commit();

        initialisePreferences();
    }
}
