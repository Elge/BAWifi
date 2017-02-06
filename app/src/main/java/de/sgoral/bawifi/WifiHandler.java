package de.sgoral.bawifi;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.preference.PreferenceManager;

import de.sgoral.bawifi.util.Logger;

/**
 * Checks if we are connected to the correct WiFi network and starts an {@link AsyncAuthTask} using
 * the preferences data.
 */
class WifiHandler {

    private final Context context;

    /**
     * Constructor with context parameter.
     *
     * @param context The application context.
     */
    public WifiHandler(Context context) {
        this.context = context;
    }

    /**
     * Performs the bulk of the work: Checks if we are connected to the correct network, checks if
     * the configuration looks good (and opens a preferences fragment otherwise), prepares data
     * saved in preferences for execution and starts the {@link AsyncAuthTask}.
     */
    void handleWifi() {
        Logger.log(WifiHandler.class, "handleWifi");

        WifiInfo wifiInfo = getWifiInfo();
        String ssid = getSSID();

        if (isConnectedToCorrectNetwork(wifiInfo, ssid)) {
            @SuppressLint("HardwareIds")
            String url = parseUrl(getURL(), translateMacAddress(wifiInfo.getMacAddress()),
                    translateIpAddress(wifiInfo.getIpAddress()));
            String username = getUsername();
            String password = getPassword();
            AuthenticationPayload payload = new AuthenticationPayload(ssid, url, username, password);

            new AsyncAuthTask(context).execute(payload);
        }
    }

    /**
     * Replaces tokens in the url with appropriate information.
     *
     * @param url        The string in which the tokens will be replaced.
     * @param macAddress The MAC address to replace the {mac} token with.
     * @param ipAddress  The IP address to replace the {ip} token with.
     * @return The string with the tokens replaced.
     */
    private String parseUrl(String url, String macAddress, String ipAddress) {
        url = url.replace(this.context.getString(R.string.mac_address_token), macAddress);
        url = url.replace(this.context.getString(R.string.ip_address_token), ipAddress);
        return url;
    }

    /**
     * Loads the WiFi connection information from the {@link WifiManager} system service.
     *
     * @return The {@link WifiInfo} for the current network, or null if not connected.
     */
    private WifiInfo getWifiInfo() {
        WifiManager wifiManager = (WifiManager) this.context.getSystemService(Context.WIFI_SERVICE);
        if (wifiManager != null) {
            return wifiManager.getConnectionInfo();
        }

        return null;
    }

    /**
     * Checks the {@link WifiInfo} object's non-nullness and SSID.
     *
     * @param wifiInfo The {@link WifiInfo} to check.
     * @param ssid     The expected network SSID.
     * @return true if we are connected to the correct network, false otherwise.
     */
    private boolean isConnectedToCorrectNetwork(WifiInfo wifiInfo, String ssid) {
        return wifiInfo != null && (wifiInfo.getSSID().equals(ssid) || wifiInfo.getSSID().equals('"' + ssid + '"'));
    }

    /**
     * Translates the IP address given by the Android system to human readable format.
     *
     * @param ip The IP to translate.
     * @return A string representation of the IP address in dot-decimal notation consisting of four
     * octets.
     */
    private String translateIpAddress(int ip) {
        // Bit shift magic
        StringBuilder builder = new StringBuilder();
        builder.append(ip & 0xFF);
        builder.append('.');
        builder.append(ip >> 8 & 0xFF);
        builder.append('.');
        builder.append(ip >> 16 & 0xFF);
        builder.append('.');
        builder.append(ip >> 24 & 0xFF);
        return builder.toString();
    }

    /**
     * Replaces colons with dashes.
     *
     * @param macAddress The MAC address to replace in.
     * @return The cleaned up MAC address string.
     */
    private String translateMacAddress(String macAddress) {
        return macAddress.replace(':', '-');
    }

    /**
     * Loads a preference value from the default shared preferences file.
     *
     * @param resourceId The resource ID to retrieve a preference for.
     * @return The default {@link SharedPreferences} object.
     */
    private String getPreference(int resourceId) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this.context);
        return preferences.getString(this.context.getString(resourceId), null);
    }

    /**
     * Loads the expected network SSID from the preferences.
     *
     * @return The network SSID to automatically authenticate the user for.
     */
    private String getSSID() {
        return getPreference(R.string.preference_key_ssid);
    }

    /**
     * Loads the start point URL from where we will log in to the network.
     *
     * @return The URL to open.
     */
    private String getURL() {
        return getPreference(R.string.preference_key_url);
    }

    /**
     * The username to authenticate with.
     *
     * @return The username.
     */
    private String getUsername() {
        return getPreference(R.string.preference_key_username);
    }

    /**
     * The plaintext password to authenticate with.
     *
     * @return The password.
     */
    private String getPassword() {
        // TODO encrypt password. How? POST request requires plaintext password.
        return getPreference(R.string.preference_key_password);
    }
}