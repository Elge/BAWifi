package de.sgoral.bawifi.util.userlog;

/**
 * Data for a userlog entry.
 */
public class UserlogEntry {

    private long time;
    private String message;
    private Type type;

    /**
     * Creates a new userlog entry.
     *
     * @param time    The event time.
     * @param type    The event type.
     * @param message The event message.
     */
    public UserlogEntry(long time, Type type, String message) {
        this.time = time;
        this.type = type;
        this.message = message;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
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
                ", type=" + type +
                ", message='" + message + '\'' +
                '}';
    }

    public enum Type {
        UI("UI"),
        NETWORK("Network"),
        EVENTS("Events"),
        SYSTEM("System");

        private String value;

        private Type(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}
