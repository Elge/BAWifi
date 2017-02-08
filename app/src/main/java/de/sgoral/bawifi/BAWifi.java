package de.sgoral.bawifi;

import android.app.Application;
import android.util.Log;

import de.sgoral.bawifi.appstatus.ApplicationStatus;
import de.sgoral.bawifi.appstatus.ApplicationStatusManager;
import de.sgoral.bawifi.util.Logger;
import de.sgoral.bawifi.util.WifiUtil;

/**
 * Initialises the application.
 */
public class BAWifi extends Application {

    @Override
    public void onCreate() {
        Logger.log(this.getClass(), "onCreate", this);
        if (new WifiUtil(this).isConnectedToCorrectNetwork()) {
            ApplicationStatusManager.changeApplicationStatus(ApplicationStatus.STATUS_CONNECTED);
        } else {
            ApplicationStatusManager.changeApplicationStatus(ApplicationStatus.STATUS_DISCONNECTED);
        }
    }
}
