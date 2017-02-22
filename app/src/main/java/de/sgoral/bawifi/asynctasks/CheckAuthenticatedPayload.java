package de.sgoral.bawifi.asynctasks;

import java.util.regex.Pattern;

/**
 * Payload for {@link CheckAuthenticatedTask},
 */
public class CheckAuthenticatedPayload {

    private final String url;
    private final Pattern pattern;

    public CheckAuthenticatedPayload(String url, Pattern pattern) {
        this.url = url;
        this.pattern = pattern;
    }

    public String getUrl() {
        return url;
    }

    public Pattern getPattern() {
        return pattern;
    }

    @Override
    public String toString() {
        return "CheckAuthenticatedPayload{" +
                "url='" + url + '\'' +
                ", pattern=" + pattern +
                '}';
    }
}
