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
import de.sgoral.bawifi.util.NotificationUtil;
import de.sgoral.bawifi.util.PreferencesUtil;
import de.sgoral.bawifi.util.WifiUtil;
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
            UserlogUtil.log(context, "networkInfo: " + networkInfo);
            if (networkInfo != null) {
                Logger.log(this, "NetworkState: ", networkInfo.getState());
                UserlogUtil.log(context, "NetworkState: " + networkInfo.getState());
                if (networkInfo.getState().equals(NetworkInfo.State.CONNECTED)) {
                    WifiUtil handler = new WifiUtil(context);
                    WifiInfo wifiInfo = handler.getWifiInfo();
                    String ssid = PreferencesUtil.getInstance(context).getSSID();
                    if (handler.isConnected()) {
                        ApplicationStateManager.changeApplicationState(ApplicationState.STATE_CONNECTED);
                        if (handler.isAuthenticated()) {
                            ApplicationStateManager.changeApplicationState(ApplicationState.STATE_AUTHENTICATED);
                        } else {
                            if (PreferencesUtil.getInstance(context).isValidConfiguration()) {
                                handler.performLogin();
                            } else {
                                NotificationUtil.addMissingPreferencesNotification(context);
                            }
                        }
                    } else {
                        ApplicationStateManager.changeApplicationState(ApplicationState.STATE_DISCONNECTED);
                    }
                } else {
                    ApplicationStateManager.changeApplicationState(ApplicationState.STATE_DISCONNECTED);
                }
            }
        }
    }
}
