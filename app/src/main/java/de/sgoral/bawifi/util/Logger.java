package de.sgoral.bawifi.util;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.sgoral.bawifi.R;

/**
 * Logging utility.
 */
public class Logger {

    public static final char USERLOG_PARTS_SEPARATOR = ':';
    public static final String USERLOG_DATA_FORMAT = "%s" + USERLOG_PARTS_SEPARATOR + "%s";

    private static List<UserlogEntry> userlog = new ArrayList<>();
    private static List<UserlogChangeListener> listeners = new ArrayList<>();

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
     * Logs throwable to debug.
     *
     * @param c The class to prefix the log output with.
     * @param t The throwable to log.
     */
    public static void log(Class c, Throwable t) {
        Log.d(c.getCanonicalName(), t.getMessage(), t);
    }

    /**
     * Reads the userlog from the log file.
     *
     * @param context The application context.
     */
    public static void loadUserlog(Context context) {
        try {
            FileInputStream stream = context.openFileInput(context.getString(R.string.log_file));
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":", 2);
                userlog.add(new UserlogEntry(Long.valueOf(parts[0]), parts[1]));
            }
            reader.close();
        } catch (IOException e) {
            log(Logger.class, e);
        }

        for (UserlogChangeListener listener : listeners) {
            listener.onUserlogLoaded();
        }
    }

    /**
     * Writes the userlog to the log file.
     *
     * @param context       The application context.
     * @param fireListeners true to fire the UserlogChangeListeners
     */
    public static void saveUserlog(Context context, boolean fireListeners) {
        try {
            FileOutputStream stream = context.openFileOutput(context.getString(R.string.log_file), Context.MODE_PRIVATE);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stream));
            for (UserlogEntry entry : userlog) {
                String line = String.format(Locale.US, USERLOG_DATA_FORMAT, Long.toString(entry.getTime()), entry.getMessage());
                writer.write(line);
                writer.write(Character.LINE_SEPARATOR);
            }
            writer.flush();
            writer.close();
        } catch (IOException e) {
            log(Logger.class, e);
        }

        if (fireListeners) {
            for (UserlogChangeListener listener : listeners) {
                listener.onUserlogSaved();
            }
        }
    }

    /**
     * Adds an entry to the userlog list and saves it to the log file.
     *
     * @param context The application context.
     * @param message The message to log.
     */
    public static void userLog(Context context, String message) {
        long time = new Date().getTime();
        UserlogEntry entry = new UserlogEntry(time, message);
        userlog.add(entry);

        try {
            FileOutputStream stream = context.openFileOutput(context.getString(R.string.log_file), Context.MODE_APPEND);
            stream.write(String.format(Locale.US, USERLOG_DATA_FORMAT, Long.toString(time), message).getBytes());
            stream.write(Character.LINE_SEPARATOR);
            stream.flush();
            stream.close();
        } catch (IOException e) {
            log(Logger.class, e);
        }

        for (UserlogChangeListener listener : listeners) {
            listener.onEntryAdded(entry);
        }
    }

    /**
     * Adds a listener for userlog changes.
     *
     * @param listener The listener to add.
     */
    public static void addListener(UserlogChangeListener listener) {
        listeners.add(listener);
    }

    /**
     * Removes a listener.
     *
     * @param listener The listener to remove.
     */
    public static void removeListener(UserlogChangeListener listener) {
        listeners.remove(listener);
    }

    /**
     * Returns a copy of the userlog list.
     *
     * @return A copy of the userlog list.
     */
    public static List<UserlogEntry> getUserlog() {
        return new ArrayList<>(userlog);
    }

    /**
     * Deletes the log file and clears the list.
     *
     * @param context The application context.
     */
    public static void clearLogEntries(Context context) {
        userlog.clear();
        saveUserlog(context, false);

        for (UserlogChangeListener listener : listeners) {
            listener.onUserlogCleared();
        }
    }
}
