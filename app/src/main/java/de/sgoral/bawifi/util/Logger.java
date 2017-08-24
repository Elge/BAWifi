package de.sgoral.bawifi.util;

import android.content.Context;
import android.util.Log;

import de.sgoral.bawifi.BuildConfig;
import de.sgoral.bawifi.util.userlog.UserlogEntry;
import de.sgoral.bawifi.util.userlog.UserlogUtil;

/**
 * Logging utility.
 */
public class Logger {

    public static final boolean LOG = BuildConfig.DEBUG;

    // Static class, hide constructor
    private Logger() {
    }

    /**
     * Logs to debug.
     *
     * @param c       The class name to prefix the log output with.
     * @param message The message to log.
     */
    public static void log(Context context, Class c, Object... message) {
        StringBuilder builder = new StringBuilder();
        for (Object part : message) {
            if (part == null) {
                builder.append("null");
            } else {
                builder.append(part.toString());
            }
        }

        if (LOG) {
            Log.d(c.getSimpleName(), builder.toString());
        }

        UserlogUtil.log(context, UserlogUtil.findType(c), c.getSimpleName(), ':', ' ', builder.toString());
    }

    /**
     * Logs to debug.
     *
     * @param obj     The object whose class name to prefix the log output with.
     * @param message The message to log.
     */
    public static void log(Context context, Object obj, Object... message) {
        log(context, obj.getClass(), message);
    }

    /**
     * Logs throwable to debug.
     *
     * @param c The class to prefix the log output with.
     * @param t The throwable to log.
     */
    public static void log(Context context, Class c, Throwable t) {
        if (LOG) {
            Log.d(c.getSimpleName(), t.getMessage(), t);
        }

        UserlogUtil.log(context, UserlogUtil.findType(c), c.getSimpleName(), ':', ' ', t.getMessage(), t);
    }

    /**
     * Logs throwable to debug.
     *
     * @param obj The object whose class name to prefix the log output with.
     * @param t   The throwable to log.
     */
    public static void log(Context context, Object obj, Throwable t) {
        log(context, obj.getClass(), t);
    }

}
