package de.sgoral.bawifi.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.util.concurrent.ExecutionException;

import de.sgoral.bawifi.R;
import de.sgoral.bawifi.asynctasks.CheckAuthenticatedPayload;
import de.sgoral.bawifi.asynctasks.CheckAuthenticatedTask;
import de.sgoral.bawifi.asynctasks.LoginPayload;
import de.sgoral.bawifi.asynctasks.LoginTask;
import de.sgoral.bawifi.asynctasks.LogoutTask;

/**
 * Checks if we are connected to the correct WiFi network and starts an {@link LoginTask} using
 * the preferences data.
 */
public class WifiUtil {

    private final Context context;

    /**
     * Constructor with context parameter.
     *
     * @param context The application environment.
     */
    public WifiUtil(Context context) {
        this.context = context;
    }

    /**
     * Starts the {@link LoginTask} to authenticate the user in the BA WiFi network.
     */
    public void performLogin() {
        WifiInfo wifiInfo = getWifiInfo();
        PreferencesUtil prefUtil = PreferencesUtil.getInstance(this.context);
        String ssid = prefUtil.getSSID();

        @SuppressLint("HardwareIds")
        String url = parseUrl(prefUtil.getLoginUrl(), translateMacAddress(wifiInfo.getMacAddress()),
                translateIpAddress(wifiInfo.getIpAddress()));
        String username = prefUtil.getUsername();
        String password = prefUtil.getPassword();
        LoginPayload payload = new LoginPayload(ssid, url, username, password);

        new LoginTask(context).execute(payload);
    }

    /**
     * Starts the {@link LogoutTask} to de-authenticate the user from the BA WiFi network.
     */
    public void performLogout() {
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
    public WifiInfo getWifiInfo() {
        WifiManager wifiManager = (WifiManager) this.context.getSystemService(Context.WIFI_SERVICE);
        if (wifiManager != null) {
            return wifiManager.getConnectionInfo();
        }

        return null;
    }

    /**
     * Checks if the device are connected to the configured WiFi network.
     *
     * @return true if the device is connected to the correct network.
     */
    public boolean isConnected() {
        WifiInfo wifiInfo = getWifiInfo();
        String ssid = PreferencesUtil.getInstance(context).getSSID();

        return wifiInfo != null && (wifiInfo.getSSID().equals(ssid) || wifiInfo.getSSID().equals('"' + ssid + '"'));
    }

    /**
     * Checks if the user is authenticated in the BA WiFi.
     *
     * @return true if the user is authenticated.
     */
    public boolean isAuthenticated() {
        CheckAuthenticatedPayload payload = new CheckAuthenticatedPayload(
                PreferencesUtil.getInstance(context).getStatusUrl(), RegexpUtil.STATUS_MESSAGE);
        CheckAuthenticatedTask task = new CheckAuthenticatedTask(this.context);
        task.execute(payload);
        try {
            return task.get();
        } catch (InterruptedException | ExecutionException e) {
            Logger.log(this, e);
        }
        return false;
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