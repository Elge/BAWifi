package de.sgoral.bawifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import de.sgoral.bawifi.appstate.ApplicationState;
import de.sgoral.bawifi.appstate.ApplicationStateManager;
import de.sgoral.bawifi.util.Logger;
import de.sgoral.bawifi.util.NetworkUtil;
import de.sgoral.bawifi.util.NotificationUtil;
import de.sgoral.bawifi.util.PreferencesUtil;
import de.sgoral.bawifi.util.userlog.UserlogUtil;

/**
 * Broadcast receiver for WiFi state changed action.
 */
public class WifiBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Logger.log(this, "OnReceive, intent: ", intent);
        UserlogUtil.log(context, "onReceive action: " + intent.getAction());

        if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())
                || WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())) {
            NetworkInfo networkInfo = (NetworkInfo) intent.getExtras().get(WifiManager.EXTRA_NETWORK_INFO);
            logNetworkInfo(context, networkInfo, NetworkUtil.getWifiInfo(context));

            if (NetworkUtil.isConnected(context, networkInfo)) {
                ApplicationStateManager.changeApplicationState(ApplicationState.STATE_CONNECTED);
                if (NetworkUtil.isAuthenticated(context)) {
                    ApplicationStateManager.changeApplicationState(ApplicationState.STATE_AUTHENTICATED);
                } else {
                    if (PreferencesUtil.getInstance(context).isValidConfiguration()) {
                        NetworkUtil.performLogin(context);
                    } else {
                        NotificationUtil.addMissingPreferencesNotification(context);
                    }
                }
            } else {
                ApplicationStateManager.changeApplicationState(ApplicationState.STATE_DISCONNECTED);
            }
        }
    }

    private void logNetworkInfo(Context context, NetworkInfo networkInfo, WifiInfo wifiInfo) {
        if (networkInfo != null) {
            Logger.log(this, "NetworkState: ", networkInfo);
            UserlogUtil.log(context, "NetworkState: ", networkInfo.getState());
        }
        if (wifiInfo != null) {
            Logger.log(this, "WifiInfo: ", wifiInfo);
        }

    }
}
