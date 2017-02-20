package de.sgoral.bawifi.appstate;

import java.util.ArrayList;
import java.util.List;

/**
 * Allows accessing information about the current state of the application.
 */
public class ApplicationStateManager {

    private static List<ApplicationStateListener> listeners = new ArrayList<>();
    private static ApplicationState state = ApplicationState.STATUS_DISCONNECTED;

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
     * @param newStatus The state to change to.
     * @return The previous application state.
     */
    public static ApplicationState changeApplicationStatus(ApplicationState newStatus) {
        ApplicationState prevStatus = state;
        state = newStatus;

        if (state != prevStatus) {
            // Trigger listeners
            for (ApplicationStateListener listener : listeners) {
                listener.onApplicationStatusChanged(state, prevStatus);
            }

            switch (state) {
                case STATUS_DISCONNECTED:
                    for (ApplicationStateListener listener : listeners) {
                        listener.onNetworkDisconnected();
                    }
                    break;
                case STATUS_CONNECTED:
                    if (prevStatus == ApplicationState.STATUS_AUTHENTICATING) {
                        for (ApplicationStateListener listener : listeners) {
                            listener.onAuthenticationFailed();
                        }
                    } else if (prevStatus == ApplicationState.STATUS_DEAUTHENTICATING) {
                        for (ApplicationStateListener listener : listeners) {
                            listener.onDeauthenticationSuccessful();
                        }
                    } else {
                        for (ApplicationStateListener listener : listeners) {
                            listener.onNetworkConnected();
                        }
                    }
                    break;
                case STATUS_AUTHENTICATING:
                    for (ApplicationStateListener listener : listeners) {
                        listener.onAuthenticationStarted();
                    }
                    break;
                case STATUS_AUTHENTICATED:
                    if (prevStatus == ApplicationState.STATUS_CONNECTED) {
                        for (ApplicationStateListener listener : listeners) {
                            listener.onAlreadyAuthenticated();
                        }
                    } else if (prevStatus == ApplicationState.STATUS_DEAUTHENTICATING) {
                        for (ApplicationStateListener listener : listeners) {
                            listener.onDeauthenticationFailed();
                        }
                    } else {
                        for (ApplicationStateListener listener : listeners) {
                            listener.onAuthenticationSuccessful();
                        }
                    }
                    break;
                case STATUS_DEAUTHENTICATING:
                    for (ApplicationStateListener listener : listeners) {
                        listener.onDeauthenticationStarted();
                    }
                    break;
                default:
                    throw new RuntimeException("Unknown application state: " + state);
            }
        }

        return prevStatus;
    }

    /**
     * Retrieves the current application state.
     *
     * @return The application state.
     */
    public static ApplicationState getApplicationStatus() {
        return state;
    }

}
