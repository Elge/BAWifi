package de.sgoral.bawifi.appstate;

import android.content.Context;

import de.sgoral.bawifi.R;
import de.sgoral.bawifi.util.Logger;
import de.sgoral.bawifi.util.PreferencesUtil;
import de.sgoral.bawifi.util.UserlogUtil;

/**
 * Creates userlog messages when the application state changes.
 */
public class AppStateUserlog {

    private final Context context;
    private ApplicationStateListener listener;

    /**
     * Initialises the logger.
     *
     * @param context The application context.
     */
    public AppStateUserlog(Context context) {
        this.context = context;
        initialise();
        Logger.log(this, "Initialised");
    }

    /**
     * Initialises the listener.
     */
    private void initialise() {
        listener = new ApplicationStateListener() {
            @Override
            public void onAuthenticationStarted() {
                UserlogUtil.log(context, context.getString(R.string.log_authenticating));
            }

            @Override
            public void onAuthenticationSuccessful() {
                UserlogUtil.log(context, context.getString(R.string.log_authenticated));
            }

            @Override
            public void onAuthenticationFailed() {
                UserlogUtil.log(context, context.getString(R.string.log_authentication_failed));
                UserlogUtil.log(context, PreferencesUtil.getInstance(context).getStatusMessage());
            }

            @Override
            public void onDeauthenticationStarted() {
                UserlogUtil.log(context, context.getString(R.string.log_deauthenticating));
            }

            @Override
            public void onDeauthenticationSuccessful() {
                UserlogUtil.log(context, context.getString(R.string.log_deauthenticated));
            }

            @Override
            public void onDeauthenticationFailed() {
                UserlogUtil.log(context, context.getString(R.string.log_deauthentication_failed));
                UserlogUtil.log(context, PreferencesUtil.getInstance(context).getStatusMessage());
            }

            @Override
            public void onNetworkConnected() {
                UserlogUtil.log(context, context.getString(R.string.log_connected));
            }

            @Override
            public void onNetworkDisconnected() {
                UserlogUtil.log(context, context.getString(R.string.log_disconnected));
            }

            @Override
            public void onAlreadyAuthenticated() {
                UserlogUtil.log(context, context.getString(R.string.log_already_authenticated));
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
