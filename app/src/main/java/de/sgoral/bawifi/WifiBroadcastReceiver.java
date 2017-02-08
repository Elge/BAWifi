package de.sgoral.bawifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import de.sgoral.bawifi.appstatus.ApplicationStatus;
import de.sgoral.bawifi.appstatus.ApplicationStatusManager;
import de.sgoral.bawifi.util.Logger;
import de.sgoral.bawifi.util.PreferencesUtil;
import de.sgoral.bawifi.util.WifiUtil;

/**
 * Broadcast receiver for WiFi state changed action.
 */
public class WifiBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Logger.log(this.getClass(), "onReceive " + intent.getAction(), context);
        for (String key : intent.getExtras().keySet()) {
            Object value = intent.getExtras().get(key);
            Logger.log(this.getClass(), key + ":" + value.toString() + "  [" + value.getClass().getCanonicalName() + ']', context);
        }
        if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {
            NetworkInfo networkInfo = (NetworkInfo) intent.getExtras().get(WifiManager.EXTRA_NETWORK_INFO);
            if (networkInfo.getState().equals(NetworkInfo.State.CONNECTED)) {
                WifiUtil handler = new WifiUtil(context);
                WifiInfo wifiInfo = handler.getWifiInfo();
                String ssid = PreferencesUtil.getInstance(context).getSSID();
                if (handler.isConnectedToCorrectNetwork()) {
                    ApplicationStatusManager.changeApplicationStatus(ApplicationStatus.STATUS_CONNECTED);
                    handler.performLogin();
                } else {
                    ApplicationStatusManager.changeApplicationStatus(ApplicationStatus.STATUS_DISCONNECTED);
                }
            } else {
                ApplicationStatusManager.changeApplicationStatus(ApplicationStatus.STATUS_DISCONNECTED);
            }
        }
    }
}
