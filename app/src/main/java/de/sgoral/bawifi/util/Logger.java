package de.sgoral.bawifi.util;

import android.util.Log;

/**
 * Logging utility.
 */
public class Logger {

    /**
     * Hidden because static utility class.
     */
    private Logger() {
    }

    /**
     * Logs to debug.
     *
     * @param c       The class to prefix the log output with.
     * @param message The message to log.
     */
    public static void log(Class c, String message) {
        Log.d(c.getCanonicalName(), message);
    }

    /**
     * Logging replacement for {@link System#out#printStackTrace(Class, Throwable)}.
     *
     * @param c The class to prefix the log output with.
     * @param t The throwable to log.
     */
    public static void printStackTrace(Class c, Throwable t) {
        Log.d(c.getCanonicalName(), t.getMessage(), t);

        // Old implementation kept for now
        // TODO remove old implementation once new one has been verified
//        StringBuilder builder = new StringBuilder();
//        builder.append(t.getClass().getCanonicalName());
//        builder.append(':');
//        builder.append(t.getMessage());
//        log(c, builder.toString());
//
//        for (StackTraceElement element : t.getStackTrace()) {
//            builder = new StringBuilder();
//            builder.append("\t");
//            builder.append(element.getClassName());
//            builder.append(':');
//            builder.append(element.getMethodName());
//            builder.append(" in ");
//            builder.append(element.getFileName());
//            builder.append(':');
//            builder.append(element.getLineNumber());
//            log(c, builder.toString());
//        }
//
//        if (t.getCause() != null) {
//            log(c, "Caused by:");
//            printStackTrace(c, t.getCause());
//        }
    }

}
