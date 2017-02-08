package de.sgoral.bawifi.util;

import de.sgoral.bawifi.asynctasks.LoginTask;

/**
 * POJO for providing the {@link LoginTask} with all the information it needs to perform the
 * authentication. No setters, get it right the first time.
 */
public class AuthenticationPayload {

    private final String ssid;
    private final String url;
    private final String username;
    private final String password;

    /**
     * Boring old constructor.
     *
     * @param ssid     The network SSID to authenticate for.
     * @param url      The URL to start the authentication process on.
     * @param username The username to authenticate with.
     * @param password The password to authenticate with.
     */
    public AuthenticationPayload(String ssid, String url, String username, String password) {
        this.ssid = ssid;
        this.url = url;
        this.username = username;
        this.password = password;
    }

    String getSsid() {
        return ssid;
    }

    public String getUrl() {
        return url;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

}
