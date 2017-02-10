package de.sgoral.bawifi.asynctasks;

import de.sgoral.bawifi.asynctasks.LoginTask;

/**
 * Payload for the {@link LoginTask}.
 */
public class LoginPayload {

    private final String ssid;
    private final String url;
    private final String username;
    private final String password;

    public LoginPayload(String ssid, String url, String username, String password) {
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
