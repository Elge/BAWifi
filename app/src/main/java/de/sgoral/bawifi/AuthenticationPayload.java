package de.sgoral.bawifi;

/**
 * POJO for providing the {@link AsyncAuthTask} with all the information it needs to perform the
 * authentication. No setters, get it right the first time.
 */
class AuthenticationPayload {

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
    AuthenticationPayload(String ssid, String url, String username, String password) {
        this.ssid = ssid;
        this.url = url;
        this.username = username;
        this.password = password;
    }

    String getSsid() {
        return ssid;
    }

    String getUrl() {
        return url;
    }

    String getUsername() {
        return username;
    }

    String getPassword() {
        return password;
    }

}
