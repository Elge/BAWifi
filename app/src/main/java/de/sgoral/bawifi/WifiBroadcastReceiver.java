package de.sgoral.bawifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;

import de.sgoral.bawifi.util.Logger;

/**
 * Broadcast receiver for WiFi state changed action.
 */
public class WifiBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Logger.log(this.getClass(), "onReceive, action is " + intent.getAction());
        if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())) {
            WifiHandler.handleWifi(context);
        }
    }
}
