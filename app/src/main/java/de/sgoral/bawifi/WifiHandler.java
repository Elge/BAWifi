package de.sgoral.bawifi;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.preference.PreferenceManager;

import de.sgoral.bawifi.util.Logger;

public class WifiHandler {

    private WifiHandler() {
    }

    public static void handleWifi(Context context) {
        Logger.log(WifiHandler.class, "onReceive");

        WifiInfo wifiInfo = getWifiInfo(context);
        String ssid = getSSID(context);

        if (isConnectedToCorrectNetwork(wifiInfo, ssid)) {
            String url = parseUrl(getURL(context), wifiInfo);
            String username = getUsername(context);
            String password = getPassword(context);
            AuthenticationPayload payload = new AuthenticationPayload(ssid, url, username, password);

            new AsyncLoginTask().execute(payload);
        }
    }

    private static String parseUrl(String url, WifiInfo wifiInfo) {
        url = url.replace("{mac}", wifiInfo.getMacAddress().replace(':', '-'));
        url = url.replace("{ip}", translateIpAddress(wifiInfo.getIpAddress()));
        return url;
    }

    public static WifiInfo getWifiInfo(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (wifiManager != null) {
            return wifiManager.getConnectionInfo();
        }

        return null;
    }

    public static boolean isConnectedToCorrectNetwork(WifiInfo wifiInfo, String ssid) {
        return wifiInfo != null && wifiInfo.getSSID().equals('"' + ssid + '"');
    }

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

    private static SharedPreferences getPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    private static String getSSID(Context context) {
        return getPreferences(context).getString("SSID", null);
    }

    private static String getURL(Context context) {
        return getPreferences(context).getString("URL", null);
    }

    private static String getUsername(Context context) {
        return getPreferences(context).getString("username", null);
    }

    private static String getPassword(Context context) {
        return getPreferences(context).getString("password", null);
    }
}