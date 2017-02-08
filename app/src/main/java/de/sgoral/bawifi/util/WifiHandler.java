package de.sgoral.bawifi.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import de.sgoral.bawifi.R;
import de.sgoral.bawifi.asynctasks.LoginTask;
import de.sgoral.bawifi.asynctasks.LogoutTask;

/**
 * Checks if we are connected to the correct WiFi network and starts an {@link LoginTask} using
 * the preferences data.
 */
public class WifiHandler {

    private final Context context;

    /**
     * Constructor with context parameter.
     *
     * @param context The application environment.
     */
    public WifiHandler(Context context) {
        this.context = context;
    }

    /**
     * Checks if we are connected to the correct network, checks if
     * the configuration looks good (and opens a preferences fragment otherwise), prepares data
     * saved in preferences for execution and starts the {@link LoginTask}.
     */
    public void performLogin() {
        Logger.log(this.getClass(), "performLogin");

        WifiInfo wifiInfo = getWifiInfo();
        PreferencesUtil prefUtil = PreferencesUtil.getInstance(this.context);
        String ssid = prefUtil.getSSID();

        if (isConnectedToCorrectNetwork(wifiInfo, ssid)) {
            @SuppressLint("HardwareIds")
            String url = parseUrl(prefUtil.getURL(), translateMacAddress(wifiInfo.getMacAddress()),
                    translateIpAddress(wifiInfo.getIpAddress()));
            String username = prefUtil.getUsername();
            String password = prefUtil.getPassword();
            AuthenticationPayload payload = new AuthenticationPayload(ssid, url, username, password);

            new LoginTask(context).execute(payload);
        }
    }

    /**
     * Starts the {@link LogoutTask} to de-authenticate the user from the BA WiFi network.
     */
    public void performLogout() {
        Logger.log(this.getClass(), "performLogout");

        String url = PreferencesUtil.getInstance(this.context).getLogoutUrl();
        new LogoutTask(context).execute(url);
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

}