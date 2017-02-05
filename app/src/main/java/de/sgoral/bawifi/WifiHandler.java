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

    private WifiHandler() {
    }

    /**
     * Performs the bulk of the work: Checks if we are connected to the correct network, checks if
     * the configuration looks good (and opens a preferences fragment otherwise), prepares data
     * saved in preferences for execution and starts the {@link AsyncAuthTask}.
     *
     * @param context The application context.
     */
    static void handleWifi(Context context) {
        Logger.log(WifiHandler.class, "onReceive");

        WifiInfo wifiInfo = getWifiInfo(context);
        String ssid = getSSID(context);

        if (isConnectedToCorrectNetwork(wifiInfo, ssid)) {
            @SuppressLint("HardwareIds")
            String url = parseUrl(getURL(context), translateMacAddress(wifiInfo.getMacAddress()),
                    translateIpAddress(wifiInfo.getIpAddress()));
            String username = getUsername(context);
            String password = getPassword(context);
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
    private static String parseUrl(String url, String macAddress, String ipAddress) {
        url = url.replace("{mac}", macAddress);
        url = url.replace("{ip}", ipAddress);
        return url;
    }

    /**
     * Loads the WiFi connection information from the application context.
     *
     * @param context The application context.
     * @return The {@link WifiInfo} for the current network, or null if not connected.
     */
    private static WifiInfo getWifiInfo(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
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
    private static boolean isConnectedToCorrectNetwork(WifiInfo wifiInfo, String ssid) {
        return wifiInfo != null && wifiInfo.getSSID().equals('"' + ssid + '"');
    }

    /**
     * Performs bit shift magic on the integer IP address provided by the Android system.
     *
     * @param ip The IP to translate.
     * @return A string representation of the IP address in dot-decimal notation consisting of four
     * octets.
     */
    private static String translateIpAddress(int ip) {
        // Bit shift magic
        return String.valueOf((ip & 0xFF)) +
                '.' +
                (ip >> 8 & 0xFF) +
                '.' +
                (ip >> 16 & 0xFF) +
                '.' +
                (ip >> 24 & 0xFF);
    }

    /**
     * Replaces colons with dashes.
     *
     * @param macAddress The MAC address to replace in.
     * @return The cleaned up MAC address string.
     */
    private static String translateMacAddress(String macAddress) {
        return macAddress.replace(':', '-');
    }

    /**
     * Shortcut to the {@link SharedPreferences} from the application context.
     *
     * @param context The application context.
     * @return The default {@link SharedPreferences} object.
     */
    private static SharedPreferences getPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     * Loads the expected network SSID from the preferences.
     *
     * @param context The application context.
     * @return The network SSID to automatically authenticate the user for.
     */
    private static String getSSID(Context context) {
        return getPreferences(context).getString("SSID", null);
    }

    /**
     * Loads the start point URL from where we will log in to the network.
     *
     * @param context The application context.
     * @return The URL to open.
     */
    private static String getURL(Context context) {
        return getPreferences(context).getString("URL", null);
    }

    /**
     * The username to authenticate with.
     *
     * @param context The application context.
     * @return The username.
     */
    private static String getUsername(Context context) {
        return getPreferences(context).getString("username", null);
    }

    /**
     * The plaintext password to authenticate with.
     *
     * @param context The application context.
     * @return The password.
     */
    private static String getPassword(Context context) {
        // TODO encrypt password. How? POST request requires plaintext password.
        return getPreferences(context).getString("password", null);
    }
}