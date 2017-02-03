package de.ba_leipzig.cs16_2.sg1.helloworld;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.preference.PreferenceManager;

public class WifiHandler {
    private WifiHandler() {
    }

    public static void handleWifi(Context context) {
        Logger.log(WifiHandler.class, "onReceive");
        WifiInfo wifiInfo = getWifiInfo(context);
        LoginData loginData = getLoginData(context);
        if (isConnectedToCorrectNetwork(wifiInfo, loginData)) {
            loginData.setUrl(parseUrl(loginData.getUrl(), wifiInfo));
            new LoginTask().execute(loginData);
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

    public static boolean isConnectedToCorrectNetwork(WifiInfo wifiInfo, LoginData loginData) {
        if (wifiInfo == null) {
            return false;
        }
        if (wifiInfo.getSSID().equals('"' + loginData.getSsid() + '"')) {
            return true;
        }
        return false;
    }

    private static String translateIpAddress(int ip) {
        // Bit shift magic
        StringBuilder builder = new StringBuilder();
        builder.append((ip & 0xFF));
        builder.append('.');
        builder.append((ip >> 8 & 0xFF));
        builder.append('.');
        builder.append((ip >> 16 & 0xFF));
        builder.append('.');
        builder.append((ip >> 24 & 0xFF));
        return builder.toString();
    }

    public static LoginData getLoginData(Context context) {
        return new LoginData(getSSID(context), getURL(context), getUsername(context), getPassword(context));
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