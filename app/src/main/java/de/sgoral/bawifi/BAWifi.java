package de.sgoral.bawifi;

import android.app.Application;

import de.sgoral.bawifi.appstate.AppStateUserlog;
import de.sgoral.bawifi.appstate.ApplicationState;
import de.sgoral.bawifi.appstate.ApplicationStateManager;
import de.sgoral.bawifi.appstate.AppStateNotifications;
import de.sgoral.bawifi.util.Logger;
import de.sgoral.bawifi.util.PreferencesUtil;
import de.sgoral.bawifi.util.WifiUtil;

/**
 * Initialises the application.
 */
public class BAWifi extends Application {

    private AppStateNotifications notifier;
    private AppStateUserlog userlogger;

    @Override
    public void onCreate() {
        Logger.log(this, "Application created");

        PreferencesUtil.getInstance(this).initialisePreferences();

        WifiUtil wifiUtil = new WifiUtil(this);
        if (wifiUtil.isConnected()) {
            if (wifiUtil.isAuthenticated()) {
                ApplicationStateManager.changeApplicationState(ApplicationState.STATE_AUTHENTICATED);
            } else {
                ApplicationStateManager.changeApplicationState(ApplicationState.STATE_CONNECTED);
            }
        } else {
            ApplicationStateManager.changeApplicationState(ApplicationState.STATE_DISCONNECTED);
        }

        Logger.loadUserlog(this);
        notifier = new AppStateNotifications(this);
        userlogger = new AppStateUserlog(this);
    }

    @Override
    public void onTerminate() {
        notifier.destroy();
        userlogger.destroy();
        Logger.log(this, "Application terminated");
    }
}
