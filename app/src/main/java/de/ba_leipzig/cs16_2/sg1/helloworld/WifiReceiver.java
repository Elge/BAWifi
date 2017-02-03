package de.ba_leipzig.cs16_2.sg1.helloworld;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Handles connectivity change events.
 */
public class WifiReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Logger.log(this.getClass(), "onReceive");
        WifiHandler.handleWifi(context);
    }
}
