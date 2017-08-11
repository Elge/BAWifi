package de.sgoral.bawifi.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;

import java.util.concurrent.ExecutionException;

import de.sgoral.bawifi.R;
import de.sgoral.bawifi.asynctasks.CheckAuthenticatedTask;
import de.sgoral.bawifi.asynctasks.LoginPayload;
import de.sgoral.bawifi.asynctasks.LoginTask;
import de.sgoral.bawifi.asynctasks.LogoutTask;

/**
 * Checks if we are connected to the correct WiFi network and starts an {@link LoginTask} using
 * the preferences data.
 */
public class NetworkUtil {

    // Static class, hide constructor
    private NetworkUtil() {
    }

    /**
     * Starts the {@link LoginTask} to authenticate the user in the BA WiFi network.
     *
     * @param context
     */
    public static void performLogin(Context context) {
        WifiInfo wifiInfo = getWifiInfo(context);
        if (wifiInfo != null) {
            PreferencesUtil prefUtil = PreferencesUtil.getInstance(context);
            String ssid = prefUtil.getSSID();

            @SuppressLint("HardwareIds")
            String url = parseUrl(context, prefUtil.getLoginUrl(), translateMacAddress(wifiInfo.getMacAddress()),
                    translateIpAddress(wifiInfo.getIpAddress()));
            String username = prefUtil.getUsername();
            String password = prefUtil.getPassword();
            LoginPayload payload = new LoginPayload(ssid, url, username, password);

            new LoginTask(context).execute(payload);
        }
    }

    /**
     * Starts the {@link LogoutTask} to de-authenticate the user from the BA WiFi network.
     *
     * @param context
     */
    public static void performLogout(Context context) {
        String url = PreferencesUtil.getInstance(context).getLogoutUrl();
        new LogoutTask(context).execute(url);
    }

    /**
     * Replaces tokens in the url with appropriate information.
     *
     * @param context
     * @param url        The string in which the tokens will be replaced.
     * @param macAddress The MAC address to replace the {mac} token with.
     * @param ipAddress  The IP address to replace the {ip} token with.
     * @return The string with the tokens replaced.
     */
    private static String parseUrl(Context context, String url, String macAddress, String ipAddress) {
        url = url.replace(context.getString(R.string.mac_address_token), macAddress);
        url = url.replace(context.getString(R.string.ip_address_token), ipAddress);
        return url;
    }

    /**
     * Loads the WiFi connection information from the {@link WifiManager} system service.
     *
     * @param context
     * @return The {@link WifiInfo} for the current network, or null if not connected.
     */
    public static WifiInfo getWifiInfo(Context context) {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager != null) {
            return wifiManager.getConnectionInfo();
        }

        return null;
    }

    /**
     * Checks if the device is connected to the configured WiFi network using the currently active network.
     *
     * @param context
     * @return true if the device is connected to the correct network.
     */
    public static boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        return isConnected(context, cm.getActiveNetworkInfo());
    }

    /**
     * Checks if the device is connected to the configured WiFi network.
     *
     * @param context
     * @param networkInfo the network info to check the state of
     * @return true if the device is connected to the correct network.
     */
    public static boolean isConnected(Context context, NetworkInfo networkInfo) {
        if (networkInfo == null) {
            return false;
        }
        if (!networkInfo.getState().equals(NetworkInfo.State.CONNECTED)) {
            return false;
        }
        WifiInfo wifiInfo = getWifiInfo(context);
        String ssid = PreferencesUtil.getInstance(context).getSSID();

        return wifiInfo != null && (wifiInfo.getSSID().equals(ssid) || wifiInfo.getSSID().equals('"' + ssid + '"'));
    }

    /**
     * Checks if the user is authenticated in the BA WiFi.
     *
     * @param context
     * @return true if the user is authenticated.
     */
    public static boolean isAuthenticated(Context context) {
        CheckAuthenticatedTask task = new CheckAuthenticatedTask(context);
        task.execute(PreferencesUtil.getInstance(context).getStatusUrl());
        try {
            return task.get();
        } catch (InterruptedException | ExecutionException e) {
            Logger.log(NetworkUtil.class, e);
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
    private static String translateIpAddress(int ip) {
        // Bit shift magic
        return String.valueOf(ip & 0xFF) +
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
     * Uses {@link ConnectivityManager#bindProcessToNetwork(Network)} to disable automatic captive
     * portal handling for all current networks.
     *
     * @param context
     */
    public static void bypassCaptivePortal(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            for (Network network : cm.getAllNetworks()) {
                cm.bindProcessToNetwork(network);
            }
        }
    }
}