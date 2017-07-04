package de.sgoral.bawifi.util;

import android.util.Log;

import de.sgoral.bawifi.BuildConfig;

/**
 * Logging utility.
 */
public class Logger {

    private static final boolean LOG = BuildConfig.DEBUG;

    // Static class, hide constructor
    private Logger() {
    }

    /**
     * Logs to debug.
     *
     * @param c       The class name to prefix the log output with.
     * @param message The message to log.
     */
    public static void log(Class c, Object... message) {
        if (LOG) {
            StringBuilder builder = new StringBuilder();
            for (Object part : message) {
                if (part == null) {
                    builder.append("null");
                } else {
                    builder.append(part.toString());
                }
            }
            Log.d(c.getSimpleName(), builder.toString());
        }
    }

    /**
     * Logs to debug.
     *
     * @param obj     The object whose class name to prefix the log output with.
     * @param message The message to log.
     */
    public static void log(Object obj, Object... message) {
        log(obj.getClass(), message);
    }

    /**
     * Logs throwable to debug.
     *
     * @param c The class to prefix the log output with.
     * @param t The throwable to log.
     */
    public static void log(Class c, Throwable t) {
        if (LOG) {
            Log.d(c.getSimpleName(), t.getMessage(), t);
        }
    }

    /**
     * Logs throwable to debug.
     *
     * @param obj The object whose class name to prefix the log output with.
     * @param t   The throwable to log.
     */
    public static void log(Object obj, Throwable t) {
        log(obj.getClass(), t);
    }


}
