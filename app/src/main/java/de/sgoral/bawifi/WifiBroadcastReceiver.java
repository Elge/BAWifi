package de.sgoral.bawifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import de.sgoral.bawifi.util.Logger;

/**
 * Handles connectivity change events.
 */
public class WifiBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Logger.log(this.getClass(), "onReceive");
        WifiHandler.handleWifi(context);
    }
}
