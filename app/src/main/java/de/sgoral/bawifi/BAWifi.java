package de.sgoral.bawifi;

import android.app.Application;
import android.widget.TabHost;

import de.sgoral.bawifi.appstatus.ApplicationStatus;
import de.sgoral.bawifi.appstatus.ApplicationStatusManager;
import de.sgoral.bawifi.util.Logger;
import de.sgoral.bawifi.util.PreferencesUtil;
import de.sgoral.bawifi.util.WifiUtil;

/**
 * Initialises the application.
 */
public class BAWifi extends Application {

    @Override
    public void onCreate() {
        Logger.log(this.getClass(), "onCreate", this);

        PreferencesUtil.getInstance(this).initialisePreferences();

        WifiUtil wifiUtil = new WifiUtil(this);
        if (wifiUtil.isConnected()) {
            if (wifiUtil.isAuthenticated()) {
                ApplicationStatusManager.changeApplicationStatus(ApplicationStatus.STATUS_AUTHENTICATED);
            } else {
                ApplicationStatusManager.changeApplicationStatus(ApplicationStatus.STATUS_CONNECTED);
            }
        } else {
            ApplicationStatusManager.changeApplicationStatus(ApplicationStatus.STATUS_DISCONNECTED);
        }
    }
}
