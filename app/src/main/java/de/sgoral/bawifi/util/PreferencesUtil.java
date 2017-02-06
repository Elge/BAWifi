package de.sgoral.bawifi.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import de.sgoral.bawifi.R;

/**
 * Allows easy access to the local application preferences.
 */
public class PreferencesUtil {

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

}
