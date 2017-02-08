package de.sgoral.bawifi.appstatus;

import java.util.ArrayList;
import java.util.List;

/**
 * Allows accessing information about the current status of the application.
 */
public class ApplicationStatusManager {

    private static List<ApplicationStatusListener> listeners = new ArrayList<>();
    private static ApplicationStatus status = ApplicationStatus.STATUS_DISCONNECTED;

    /**
     * Hidden constructor because we are fully static
     */
    private ApplicationStatusManager() {
    }

    /**
     * Adds a listener to receive notifications on application status changes.
     *
     * @param listener The listener to add.
     */
    public static void addListener(ApplicationStatusListener listener) {
        ApplicationStatusManager.listeners.add(listener);
    }

    /**
     * Sets a listener to no longer receive notifications on application status changes.
     *
     * @param listener The listener to remove.
     * @return true if the listener was present previously.
     */
    public static boolean removeListener(ApplicationStatusListener listener) {
        return ApplicationStatusManager.listeners.remove(listener);
    }

    /**
     * Changes the application status to a new value.
     *
     * @param newStatus The status to change to.
     * @return The previous application status.
     */
    public static ApplicationStatus changeApplicationStatus(ApplicationStatus newStatus) {
        ApplicationStatus prevStatus = status;
        status = newStatus;

        if (status != prevStatus) {
            // Trigger listeners
            for (ApplicationStatusListener listener : listeners) {
                listener.onApplicationStatusChanged(status, prevStatus);
            }

            switch (status) {
                case STATUS_DISCONNECTED:
                    for (ApplicationStatusListener listener : listeners) {
                        listener.onNetworkDisconnected();
                    }
                    break;
                case STATUS_CONNECTED:
                    if (prevStatus == ApplicationStatus.STATUS_AUTHENTICATING) {
                        for (ApplicationStatusListener listener : listeners) {
                            listener.onAuthenticationFailed();
                        }
                    } else if (prevStatus == ApplicationStatus.STATUS_DEAUTHENTICATING) {
                        for (ApplicationStatusListener listener : listeners) {
                            listener.onDeAuthenticationSuccessful();
                        }
                    } else {
                        for (ApplicationStatusListener listener : listeners) {
                            listener.onNetworkConnected();
                        }
                    }
                    break;
                case STATUS_AUTHENTICATING:
                    for (ApplicationStatusListener listener : listeners) {
                        listener.onAuthenticationStarted();
                    }
                    break;
                case STATUS_AUTHENTICATED:
                    for (ApplicationStatusListener listener : listeners) {
                        listener.onAuthenticationSuccessful();
                    }
                    break;
                case STATUS_DEAUTHENTICATING:
                    for (ApplicationStatusListener listener : listeners) {
                        listener.onDeAuthenticationStarted();
                    }
                    break;
                default:
                    throw new RuntimeException("Unknown application status: " + status);
            }
        }

        return prevStatus;
    }

    /**
     * Retrieves the current application status.
     *
     * @return The application status.
     */
    public static ApplicationStatus getApplicationStatus() {
        return status;
    }

}
