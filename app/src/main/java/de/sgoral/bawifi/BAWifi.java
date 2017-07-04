package de.sgoral.bawifi;

import android.app.Application;

import de.sgoral.bawifi.appstate.ApplicationState;
import de.sgoral.bawifi.appstate.ApplicationStateManager;
import de.sgoral.bawifi.util.Logger;
import de.sgoral.bawifi.util.PreferencesUtil;
import de.sgoral.bawifi.util.userlog.UserlogUtil;
import de.sgoral.bawifi.util.NetworkUtil;

/**
 * Initialises the application.
 */
public class BAWifi extends Application {

    private ApplicationStateListenerImpl listener;

    @Override
    public void onCreate() {
        Logger.log(this, "Application created");

        PreferencesUtil.getInstance(this).initialisePreferences();

        if (NetworkUtil.isConnected(this)) {
            if (NetworkUtil.isAuthenticated(this)) {
                ApplicationStateManager.changeApplicationState(ApplicationState.STATE_AUTHENTICATED);
            } else {
                ApplicationStateManager.changeApplicationState(ApplicationState.STATE_CONNECTED);
            }
        } else {
            ApplicationStateManager.changeApplicationState(ApplicationState.STATE_DISCONNECTED);
        }

        UserlogUtil.loadFromFile(this);
        listener = new ApplicationStateListenerImpl(getApplicationContext());
    }

    @Override
    public void onTerminate() {
        listener.destroy();
        Logger.log(this, "Application terminated");
    }
}
