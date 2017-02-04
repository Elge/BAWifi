package de.sgoral.bawifi;

/**
 *
 */
class AuthenticationPayload {

    private String ssid;
    private String url;
    private String username;
    private String password;

    AuthenticationPayload(String ssid, String url, String username, String password) {
        this.ssid = ssid;
        this.url = url;
        this.username = username;
        this.password = password;
    }

    String getSsid() {
        return ssid;
    }

    void setSsid(String ssid) {
        this.ssid = ssid;
    }

    String getUrl() {
        return url;
    }

    void setUrl(String url) {
        this.url = url;
    }

    String getUsername() {
        return username;
    }

    void setUsername(String username) {
        this.username = username;
    }

    String getPassword() {
        return password;
    }

    void setPassword(String password) {
        this.password = password;
    }
}
