package de.sgoral.bawifi.util;

import android.content.Context;
import android.util.Log;

/**
 * Logging utility.
 */
public class Logger {

    /**
     * Number of elements of a stacktrace that will be printed to user log.
     */
    private static final int LOG_STACK_TRACE_ELEMENTS = 5;

    /**
     * Hidden because static utility class.
     */
    private Logger() {
    }

    /**
     * Logs to debug but not SharedPreferences.
     *
     * @param c       The class to prefix the log output with.
     * @param message The message to log.
     */
    public static void log(Class c, String message) {
        log(c, message, null);
    }

    /**
     * Logs to debug and SharedPreferences.
     *
     * @param c       The class to prefix the log output with.
     * @param message The message to log.
     * @param context The context to use while saving the log message.
     */
    public static void log(Class c, String message, Context context) {
        Log.d(c.getCanonicalName(), message);
        if (context != null) {
            PreferencesUtil.getInstance(context).addLogEntry(c.getName() + ": " + message);
        }
    }

    /**
     * Logging replacement for {@link System#out#printStackTrace(Class, Throwable)}. Doesn't print
     * the Throwable message to SharedPreferences.
     *
     * @param c The class to prefix the log output with.
     * @param t The throwable to log.
     */
    public static void printStackTrace(Class c, Throwable t) {
        printStackTrace(c, t, null);
    }

    /**
     * Logging replacement for {@link System#out#printStackTrace(Class, Throwable)}. Also prints
     * the Throwable to SharedPreferences.
     *
     * @param c       The class to prefix the log output with.
     * @param t       The throwable to log.
     * @param context The context to use while saving the log message.
     */
    public static void printStackTrace(Class c, Throwable t, Context context) {
        Log.d(c.getCanonicalName(), t.getMessage(), t);

        if (context != null) {
            PreferencesUtil prefUtil = PreferencesUtil.getInstance(context);
            prefUtil.addLogEntry(t.getClass().getCanonicalName() + ": \"" + t.getMessage() + '\"');
            StackTraceElement[] stackTrace = t.getStackTrace();
            for (int i = 0; i < LOG_STACK_TRACE_ELEMENTS && i < stackTrace.length; i++) {
                prefUtil.addLogEntry(
                        "   in " + stackTrace[i].getFileName() + ':' + stackTrace[i].getLineNumber());
            }
        }
    }

}
