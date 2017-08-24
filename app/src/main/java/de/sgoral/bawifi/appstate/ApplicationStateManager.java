package de.sgoral.bawifi.appstate;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import de.sgoral.bawifi.util.Logger;

/**
 * Allows accessing information about the current state of the application.
 */
public class ApplicationStateManager {

    private static final List<ApplicationStateListener> listeners = new ArrayList<>();
    private static ApplicationState state = ApplicationState.STATE_DISCONNECTED;

    /**
     * Hidden constructor because we are fully static
     */
    private ApplicationStateManager() {
    }

    /**
     * Adds a listener to receive notifications on application state changes.
     *
     * @param listener The listener to add.
     */
    public static void addListener(ApplicationStateListener listener) {
        ApplicationStateManager.listeners.add(listener);
    }

    /**
     * Sets a listener to no longer receive notifications on application state changes.
     *
     * @param listener The listener to remove.
     * @return true if the listener was present previously.
     */
    public static boolean removeListener(ApplicationStateListener listener) {
        return ApplicationStateManager.listeners.remove(listener);
    }

    /**
     * Changes the application state to a new value.
     *
     *
     * @param context
     * @param newState The state to change to.
     * @return The previous application state.
     */
    public static ApplicationState changeApplicationState(Context context, ApplicationState newState) {
        ApplicationState prevState = state;
        state = newState;

        if (state == prevState) {
            Logger.log(context, ApplicationStateManager.class, "Application state unchanged: ", newState);
        } else {
            Logger.log(context, ApplicationStateManager.class, "Application state changed from ", prevState, " to ", newState);
            // Trigger listeners
            for (ApplicationStateListener listener : listeners) {
                listener.onApplicationStateChanged(state, prevState);
            }

            switch (state) {
                case STATE_DISCONNECTED:
                    for (ApplicationStateListener listener : listeners) {
                        listener.onNetworkDisconnected();
                    }
                    break;
                case STATE_CONNECTED:
                    if (prevState == ApplicationState.STATE_AUTHENTICATING) {
                        for (ApplicationStateListener listener : listeners) {
                            listener.onAuthenticationFailed();
                        }
                    } else if (prevState == ApplicationState.STATE_DEAUTHENTICATING) {
                        for (ApplicationStateListener listener : listeners) {
                            listener.onDeauthenticationSuccessful();
                        }
                    } else {
                        for (ApplicationStateListener listener : listeners) {
                            listener.onNetworkConnected();
                        }
                    }
                    break;
                case STATE_AUTHENTICATING:
                    for (ApplicationStateListener listener : listeners) {
                        listener.onAuthenticationStarted();
                    }
                    break;
                case STATE_AUTHENTICATED:
                    if (prevState == ApplicationState.STATE_CONNECTED) {
                        for (ApplicationStateListener listener : listeners) {
                            listener.onAlreadyAuthenticated();
                        }
                    } else if (prevState == ApplicationState.STATE_DEAUTHENTICATING) {
                        for (ApplicationStateListener listener : listeners) {
                            listener.onDeauthenticationFailed();
                        }
                    } else {
                        for (ApplicationStateListener listener : listeners) {
                            listener.onAuthenticationSuccessful();
                        }
                    }
                    break;
                case STATE_DEAUTHENTICATING:
                    for (ApplicationStateListener listener : listeners) {
                        listener.onDeauthenticationStarted();
                    }
                    break;
                default:
                    throw new RuntimeException("Unknown application state: " + state);
            }
        }

        return prevState;
    }

    /**
     * Retrieves the current application state.
     *
     * @return The application state.
     */
    public static ApplicationState getApplicationState() {
        return state;
    }

}
