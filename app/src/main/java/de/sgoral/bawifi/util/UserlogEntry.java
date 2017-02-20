package de.sgoral.bawifi.util;

/**
 * Data for a userlog entry.
 */
public class UserlogEntry {

    private long time;
    private String message;

    /**
     * Creates a new userlog entry.
     *
     * @param time    The event time.
     * @param message The event message.
     */
    public UserlogEntry(long time, String message) {
        this.time = time;
        this.message = message;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "UserlogEntry{" +
                "time=" + time +
                ", message='" + message + '\'' +
                '}';
    }
}
