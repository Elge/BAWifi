package de.sgoral.bawifi.appstatus;

/**
 * Allows the component to listen for changes to the application status.
 */
public abstract class ApplicationStatusListener {

    /**
     * General change in application status.
     *
     * @param newStatus  The new application status.
     * @param prevStatus The old application status.
     */
    public void onApplicationStatusChanged(ApplicationStatus newStatus, ApplicationStatus prevStatus) {
    }

    /**
     * The authentication process was started.
     */
    public void onAuthenticationStarted() {
    }

    /**
     * The authentication has been completed successfully.
     */
    public void onAuthenticationSuccessful() {
    }

    /**
     * The authentication has failed.
     */
    public void onAuthenticationFailed() {
    }

    /**
     * The de-authentication process was started.
     */
    public void onDeAuthenticationStarted() {
    }

    /**
     * The user was de-authenticated from the network.
     */
    public void onDeAuthenticationSuccessful() {
    }

    /**
     * The de-authentication process failed.
     */
    public void onDeAuthenticationFailed() {}

    /**
     * The connection to the BA WiFi network was established.
     */
    public void onNetworkConnected() {
    }

    /**
     * The network connection to the BA WiFi was lost.
     */
    public void onNetworkDisconnected() {
    }
}
