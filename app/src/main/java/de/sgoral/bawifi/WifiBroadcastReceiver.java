package de.sgoral.bawifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import de.sgoral.bawifi.appstate.ApplicationState;
import de.sgoral.bawifi.appstate.ApplicationStateManager;
import de.sgoral.bawifi.util.NotificationUtil;
import de.sgoral.bawifi.util.PreferencesUtil;
import de.sgoral.bawifi.util.WifiUtil;

/**
 * Broadcast receiver for WiFi state changed action.
 */
public class WifiBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {
            NetworkInfo networkInfo = (NetworkInfo) intent.getExtras().get(WifiManager.EXTRA_NETWORK_INFO);
            if (networkInfo.getState().equals(NetworkInfo.State.CONNECTED)) {
                WifiUtil handler = new WifiUtil(context);
                WifiInfo wifiInfo = handler.getWifiInfo();
                String ssid = PreferencesUtil.getInstance(context).getSSID();
                if (handler.isConnected()) {
                    ApplicationStateManager.changeApplicationStatus(ApplicationState.STATUS_CONNECTED);
                    if (handler.isAuthenticated()) {
                        ApplicationStateManager.changeApplicationStatus(ApplicationState.STATUS_AUTHENTICATED);
                    } else {
                        if (PreferencesUtil.getInstance(context).isValidConfiguration()) {
                            handler.performLogin();
                        } else {
                            NotificationUtil.addMissingPreferencesNotification(context);
                        }
                    }
                } else {
                    ApplicationStateManager.changeApplicationStatus(ApplicationState.STATUS_DISCONNECTED);
                }
            } else {
                ApplicationStateManager.changeApplicationStatus(ApplicationState.STATUS_DISCONNECTED);
            }
        }
    }
}
