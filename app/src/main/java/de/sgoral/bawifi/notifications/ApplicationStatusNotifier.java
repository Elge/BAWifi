package de.sgoral.bawifi.notifications;

import android.content.Context;

import de.sgoral.bawifi.appstatus.ApplicationStatusManager;

/**
 * Created by sebastianprivat on 16.02.17.
 */

public class ApplicationStatusNotifier {

    private final Context context;
    private de.sgoral.bawifi.appstatus.ApplicationStatusListener listener;

    public ApplicationStatusNotifier(Context context) {
        this.context = context;
        initialise();
    }

    public void initialise() {
        listener = new de.sgoral.bawifi.appstatus.ApplicationStatusListener() {
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
        };

        ApplicationStatusManager.addListener(listener);
    }

    public void pause() {
        ApplicationStatusManager.removeListener(listener);
    }

    public void resume() {
        ApplicationStatusManager.addListener(listener);
    }

    public void destroy() {
        ApplicationStatusManager.removeListener(listener);
        listener = null;
    }
}
