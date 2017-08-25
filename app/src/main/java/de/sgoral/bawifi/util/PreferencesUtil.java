package de.sgoral.bawifi.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import de.sgoral.bawifi.R;
import de.sgoral.bawifi.SlowDisconnectDetection;
import de.sgoral.bawifi.util.RingerModeUtil.RingerModeSetting;

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
     * @param context
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
     * The status message shows errors in the authentication or deauthentication process.
     *
     * @return The status message.
     */
    public String getStatusMessage() {
        return getStringPreference(R.string.preference_key_statusmessage);
    }

    /**
     * Saves the status message in the application preferences.
     *
     * @param message The message to store.
     */
    public void setStatusMessage(String message) {
        SharedPreferences.Editor editor = getSharesPreferences().edit();
        editor.putString(context.getString(R.string.preference_key_statusmessage), message);
        editor.apply();
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
     * Checks if already authenticated notifications are enabled.
     *
     * @return true if already authenticated notifications are enabled.
     */
    public boolean isAlreadyAuthenticatedNotificationsEnabled() {
        return isNotificationsEnabled() && getBooleanPreference(R.string.preference_key_notification_already_authenticated);
    }

    /**
     * Loads the volume control on connect setting from the default shared preferences file.
     *
     * @return a {@link RingerModeSetting}
     */
    public RingerModeSetting getVolumeControlOnConnect() {
        String setting = getStringPreference(R.string.preference_key_volume_control_connect);

        if (setting == null) {
            return RingerModeSetting.OFF;
        }
        return RingerModeSetting.valueOf(setting);
    }

    /**
     * Loads the volume control on disconnect setting from the default shared preferences file.
     *
     * @return a {@link RingerModeSetting}
     */
    public RingerModeSetting getVolumeControlOnDisconnect() {
        String setting = getStringPreference(R.string.preference_key_volume_control_disconnect);

        if (setting == null) {
            return RingerModeSetting.OFF;
        }
        return RingerModeSetting.valueOf(setting);
    }

    /**
     * Loads the slow disconnect detection setting from the default shared preferences file.
     *
     * @return a {@link SlowDisconnectDetection}
     */
    public SlowDisconnectDetection getSlowDisconnectDetectionSetting() {
        String setting = getStringPreference(R.string.preference_key_slow_disconnect_detection);

        if (setting == null) {
            return SlowDisconnectDetection.OFF;
        }
        return SlowDisconnectDetection.valueOf(setting);
    }

    /**
     * Loads default values for all preferences.
     */
    public void initialisePreferences() {
        Logger.log(context, this, "Loading preferences");
        PreferenceManager.setDefaultValues(context, R.xml.preferences, true);
        PreferenceManager.setDefaultValues(context, R.xml.preferences_notifications, true);
        PreferenceManager.setDefaultValues(context, R.xml.preferences_volume_control, true);
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
        Logger.log(context, this, "Clearing preferences");
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.clear();
        editor.apply();

        initialisePreferences();
    }

}
