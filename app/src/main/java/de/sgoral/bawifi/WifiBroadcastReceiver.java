package de.sgoral.bawifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

import de.sgoral.bawifi.util.Logger;
import de.sgoral.bawifi.util.WifiHandler;

/**
 * Broadcast receiver for WiFi state changed action.
 */
public class WifiBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Logger.log(this.getClass(), "onReceive " + intent.getAction());
        for (String key : intent.getExtras().keySet()) {
            Object value = intent.getExtras().get(key);
            Logger.log(this.getClass(), key + ":" + value.toString() + "  [" + value.getClass().getCanonicalName() + ']');
        }
        if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {
            NetworkInfo networkInfo = (NetworkInfo) intent.getExtras().get(WifiManager.EXTRA_NETWORK_INFO);
            if (networkInfo.getState().equals(NetworkInfo.State.CONNECTED)) {
                new WifiHandler(context).performLogin();

            }
        }
    }
}
