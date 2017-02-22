package de.sgoral.bawifi.appstate;

import android.content.Context;
import android.util.Log;

import de.sgoral.bawifi.util.Logger;
import de.sgoral.bawifi.util.NotificationUtil;

/**
 * Creates notifications when the application state changes.
 */
public class AppStateNotifications {

    private final Context context;
    private ApplicationStateListener listener;

    /**
     * Initialises the notifier.
     *
     * @param context The application context.
     */
    public AppStateNotifications(Context context) {
        this.context = context;
        initialise();
        Logger.log(this, "Initialised");
    }

    /**
     * Initialise the listener.
     */
    private void initialise() {
        listener = new ApplicationStateListener() {
            @Override
            public void onAuthenticationSuccessful() {
                NotificationUtil.addAuthenticationSuccessfulNotification(context);
            }

            @Override
            public void onAuthenticationFailed() {
                NotificationUtil.addAuthenticationFailedNotification(context);
            }

            @Override
            public void onDeauthenticationSuccessful() {
                NotificationUtil.addDeauthenticationSuccessfulNotification(context);
            }

            @Override
            public void onDeauthenticationFailed() {
                NotificationUtil.addDeauthenticationFailedNotification(context);
            }

            @Override
            public void onNetworkConnected() {
                NotificationUtil.addConnectedNotification(context);
            }

            @Override
            public void onNetworkDisconnected() {
                NotificationUtil.addDisconnectedNotification(context);
            }

            @Override
            public void onAlreadyAuthenticated() {
                NotificationUtil.addAlreadyAuthenticatedNotification(context);
            }
        };

        ApplicationStateManager.addListener(listener);
    }

    /**
     * Pauses the listener.
     */
    public void pause() {
        ApplicationStateManager.removeListener(listener);
        Logger.log(this, "Listener paused");
    }

    /**
     * Resumes the listener.
     */
    public void resume() {
        ApplicationStateManager.addListener(listener);
        Logger.log(this, "Listener resumed");
    }

    /**
     * Destroys the listener.
     */
    public void destroy() {
        ApplicationStateManager.removeListener(listener);
        listener = null;
        Logger.log(this, "Listener destroyed");
    }
}
