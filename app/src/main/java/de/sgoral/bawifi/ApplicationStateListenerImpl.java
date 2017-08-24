package de.sgoral.bawifi;

import android.content.Context;

import de.sgoral.bawifi.appstate.ApplicationStateListener;
import de.sgoral.bawifi.appstate.ApplicationStateManager;
import de.sgoral.bawifi.util.Logger;
import de.sgoral.bawifi.util.NotificationUtil;
import de.sgoral.bawifi.util.PreferencesUtil;
import de.sgoral.bawifi.util.RingerModeUtil;
import de.sgoral.bawifi.util.RingerModeUtil.RingerModeSetting;

/**
 * Listens for application state changes and sends notifications and user log entries.
 */
public class ApplicationStateListenerImpl extends ApplicationStateListener {

    private final Context context;
    private ApplicationStateListener listener;

    /**
     * Initialises the listener.
     *
     * @param context
     */
    public ApplicationStateListenerImpl(Context context) {
        this.context = context;
        initialise();
        Logger.log(context, this, "Initialised");
    }

    /**
     * Initialise the listener.
     */
    private void initialise() {
        listener = new ApplicationStateListener() {
            @Override
            public void onAuthenticationStarted() {
                Logger.log(context, ApplicationStateListenerImpl.class, context.getString(R.string.log_authenticating));
            }

            @Override
            public void onAuthenticationSuccessful() {
                NotificationUtil.addAuthenticationSuccessfulNotification(context);
                Logger.log(context, ApplicationStateListenerImpl.class, context.getString(R.string.log_authenticated));
            }

            @Override
            public void onAuthenticationFailed() {
                NotificationUtil.addAuthenticationFailedNotification(context);
                Logger.log(context, ApplicationStateListenerImpl.class, context.getString(R.string.log_authentication_failed));
                Logger.log(context, ApplicationStateListenerImpl.class, PreferencesUtil.getInstance(context).getStatusMessage());
            }

            @Override
            public void onDeauthenticationStarted() {
                Logger.log(context, ApplicationStateListenerImpl.class, context.getString(R.string.log_deauthenticating));
            }

            @Override
            public void onDeauthenticationSuccessful() {
                NotificationUtil.addDeauthenticationSuccessfulNotification(context);
                Logger.log(context, ApplicationStateListenerImpl.class, context.getString(R.string.log_deauthenticated));
            }

            @Override
            public void onDeauthenticationFailed() {
                NotificationUtil.addDeauthenticationFailedNotification(context);
                Logger.log(context, ApplicationStateListenerImpl.class, context.getString(R.string.log_deauthentication_failed));
                Logger.log(context, ApplicationStateListenerImpl.class, PreferencesUtil.getInstance(context).getStatusMessage());
            }

            @Override
            public void onNetworkConnected() {
                NotificationUtil.addConnectedNotification(context);
                Logger.log(context, ApplicationStateListenerImpl.class, context.getString(R.string.log_connected));

                RingerModeSetting setting = PreferencesUtil.getInstance(context).getVolumeControlOnConnect();
                RingerModeUtil util = RingerModeUtil.getInstance(context);
                if (setting != RingerModeSetting.OFF && !util.canChangeRingerMode()) {
                    NotificationUtil.addDndPermissionRequiredNotification(context);
                } else {
                    util.changeRingerMode(setting);
                }
            }

            @Override
            public void onNetworkDisconnected() {
                NotificationUtil.addDisconnectedNotification(context);
                Logger.log(context, ApplicationStateListenerImpl.class, context.getString(R.string.log_disconnected));

                RingerModeSetting setting = PreferencesUtil.getInstance(context).getVolumeControlOnDisconnect();
                RingerModeUtil util = RingerModeUtil.getInstance(context);
                if (setting != RingerModeSetting.OFF && !util.canChangeRingerMode()) {
                    NotificationUtil.addDndPermissionRequiredNotification(context);
                } else {
                    util.changeRingerMode(setting);
                }
            }

            @Override
            public void onAlreadyAuthenticated() {
                NotificationUtil.addAlreadyAuthenticatedNotification(context);
                Logger.log(context, ApplicationStateListenerImpl.class, context.getString(R.string.log_already_authenticated));
            }
        };

        ApplicationStateManager.addListener(listener);
    }

    /**
     * Pauses the listener.
     */
    public void pause() {
        ApplicationStateManager.removeListener(listener);
        Logger.log(context, this, "Listener paused");
    }

    /**
     * Resumes the listener.
     */
    public void resume() {
        ApplicationStateManager.addListener(listener);
        Logger.log(context, this, "Listener resumed");
    }

    /**
     * Destroys the listener.
     */
    public void destroy() {
        ApplicationStateManager.removeListener(listener);
        listener = null;
        Logger.log(context, this, "Listener destroyed");
    }
}
