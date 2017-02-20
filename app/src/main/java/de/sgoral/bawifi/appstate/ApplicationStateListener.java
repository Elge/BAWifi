package de.sgoral.bawifi.appstate;

/**
 * Allows the component to listen for changes to the application state.
 */
public abstract class ApplicationStateListener {

    /**
     * General change in application state.
     *
     * @param newStatus  The new application state.
     * @param prevStatus The old application state.
     */
    public void onApplicationStatusChanged(ApplicationState newStatus, ApplicationState prevStatus) {
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
    public void onDeauthenticationStarted() {
    }

    /**
     * The user was de-authenticated from the network.
     */
    public void onDeauthenticationSuccessful() {
    }

    /**
     * The de-authentication process failed.
     */
    public void onDeauthenticationFailed() {
    }

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

    /**
     * The user was still authenticated after connecting.
     */
    public void onAlreadyAuthenticated() {
    }
}
