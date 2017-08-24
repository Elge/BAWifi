package de.sgoral.bawifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;

import de.sgoral.bawifi.appstate.ApplicationState;
import de.sgoral.bawifi.appstate.ApplicationStateManager;
import de.sgoral.bawifi.asynctasks.DelayedDisconnectTask;
import de.sgoral.bawifi.util.Logger;
import de.sgoral.bawifi.util.NetworkUtil;
import de.sgoral.bawifi.util.NotificationUtil;
import de.sgoral.bawifi.util.PreferencesUtil;

/**
 * Broadcast receiver for WiFi state changed action.
 */
public class WifiBroadcastReceiver extends BroadcastReceiver {


    private static AsyncTask<Integer, Void, Void> disconnectTask = null;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {
            NetworkInfo networkInfo = (NetworkInfo) intent.getExtras().get(WifiManager.EXTRA_NETWORK_INFO);
            logNetworkInfo(context, networkInfo, NetworkUtil.getWifiInfo(context));

            if (networkInfo == null) {
                return;
            }

            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI
                    && "\"www.ba-leipzig.de\"".equals(networkInfo.getExtraInfo())
                    && (networkInfo.getState() == NetworkInfo.State.CONNECTING
                    || networkInfo.getState() == NetworkInfo.State.CONNECTED)) {
                cancelDisconnect();

                if (networkInfo.getDetailedState() == NetworkInfo.DetailedState.CONNECTED) {
                    if (NetworkUtil.isConnected(context, networkInfo)) {
                        ApplicationStateManager.changeApplicationState(context, ApplicationState.STATE_CONNECTED);
                        if (NetworkUtil.isAuthenticated(context)) {
                            ApplicationStateManager.changeApplicationState(context, ApplicationState.STATE_AUTHENTICATED);
                        } else {
                            if (PreferencesUtil.getInstance(context).isValidConfiguration()) {
                                NetworkUtil.performLogin(context);
                            } else {
                                NotificationUtil.addMissingPreferencesNotification(context);
                            }
                        }
                    }
                }
            } else if (networkInfo.getState() == NetworkInfo.State.DISCONNECTED) {
                SlowDisconnectDetection delay = PreferencesUtil.getInstance(context).getSlowDisconnectDetectionSetting();
                scheduleDisconnect(delay, context);
            }
        }
    }

    private void logNetworkInfo(Context context, NetworkInfo networkInfo, WifiInfo wifiInfo) {
        StringBuilder sb = new StringBuilder();
        sb.append("Network state:[");
        if (networkInfo == null) {
            sb.append("null");
        } else {
            sb.append("type:");
            sb.append(networkInfo.getTypeName());
            sb.append(", state:");
            sb.append(networkInfo.getDetailedState());
            sb.append(", extra:");
            sb.append(networkInfo.getExtraInfo());
        }
        sb.append("], WifiInfo:[");
        if (wifiInfo == null) {
            sb.append("null");
        } else {
            sb.append("SSID:");
            sb.append(wifiInfo.getSSID());
            sb.append(", supplicant state:");
            sb.append(wifiInfo.getSupplicantState());
        }
        sb.append("]");

        Logger.log(context, this, sb.toString());
    }

    private void scheduleDisconnect(SlowDisconnectDetection delay, Context context) {
        if (delay == SlowDisconnectDetection.OFF) {
            ApplicationStateManager.changeApplicationState(context, ApplicationState.STATE_DISCONNECTED);
        } else {
            cancelDisconnect();
            disconnectTask = new DelayedDisconnectTask(context).execute(delay.getValue());
        }
    }

    private void cancelDisconnect() {
        if (disconnectTask != null && !disconnectTask.isCancelled()) {
            disconnectTask.cancel(true);
        }
    }
}
