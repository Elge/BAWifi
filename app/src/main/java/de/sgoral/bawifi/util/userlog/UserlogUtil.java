package de.sgoral.bawifi.util.userlog;

import android.content.Context;

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
import de.sgoral.bawifi.util.Logger;

/**
 * Log utility for in-app log messages.
 */
public class UserlogUtil {

    public static final char USERLOG_PARTS_SEPARATOR = ':';
    public static final String USERLOG_DATA_FORMAT = "%s" + USERLOG_PARTS_SEPARATOR + "%s" + USERLOG_PARTS_SEPARATOR + "%s";
    private static final List<UserlogEntry> userlog = new ArrayList<>();
    private static final List<UserlogChangeListener> listeners = new ArrayList<>();

    /**
     * Private because static.
     */
    private UserlogUtil() {
    }

    /**
     * Reads the userlog from the log file.
     *
     * @param context
     */
    public static void loadFromFile(Context context) {
        try {
            FileInputStream stream = context.openFileInput(context.getString(R.string.log_file));
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":", 3);
                userlog.add(new UserlogEntry(Long.valueOf(parts[0]), UserlogEntry.Type.valueOf(parts[1]), parts[2]));
            }
            reader.close();
        } catch (IOException e) {
            Logger.log(context, UserlogUtil.class, e);
        }

        for (UserlogChangeListener listener : listeners) {
            listener.onUserlogLoaded();
        }
    }

    /**
     * Writes the userlog to the log file.
     *
     * @param context
     * @param fireListeners true to fire the UserlogChangeListeners
     */
    public static void saveToFile(Context context, boolean fireListeners) {
        try {
            FileOutputStream stream = context.openFileOutput(context.getString(R.string.log_file), Context.MODE_PRIVATE);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stream));
            for (UserlogEntry entry : userlog) {
                String line = String.format(Locale.US, USERLOG_DATA_FORMAT,
                        Long.toString(entry.getTime()), entry.getType().toString(), entry.getMessage());
                writer.write(line);
                writer.write(Character.LINE_SEPARATOR);
            }
            writer.flush();
            writer.close();
        } catch (IOException e) {
            Logger.log(context, UserlogUtil.class, e);
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
     * @param context
     * @param message The message to log.
     */
    public static void log(Context context, UserlogEntry.Type type, String message) {
        long time = new Date().getTime();
        UserlogEntry entry = new UserlogEntry(time, type, message);
        userlog.add(entry);

        try {
            FileOutputStream stream = context.openFileOutput(context.getString(R.string.log_file), Context.MODE_APPEND);
            stream.write(String.format(Locale.US, USERLOG_DATA_FORMAT,
                    Long.toString(time), type.toString(), message).getBytes());
            stream.write(Character.LINE_SEPARATOR);
            stream.flush();
            stream.close();
        } catch (IOException e) {
            Logger.log(context, UserlogUtil.class, e);
        }

        for (UserlogChangeListener listener : listeners) {
            listener.onEntryAdded(entry);
        }
    }

    /**
     * Adds an entry to the userlog list and saves it to the log file.
     *
     * @param context
     * @param parts The message parts to concat to a message. Writes null if the object is null,
     *              otherwise its toString method is called.
     */
    public static void log(Context context, UserlogEntry.Type type, Object... parts) {
        StringBuilder builder = new StringBuilder();
        for (Object part : parts) {
            if (part == null) {
                builder.append("null");
            } else {
                builder.append(part.toString());
            }
        }

        log(context, type, builder.toString());
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
    public static List<UserlogEntry> getLogEntries() {
        return new ArrayList<>(userlog);
    }

    /**
     * Deletes the log file and clears the list.
     *
     * @param context
     */
    public static void clearLogEntries(Context context) {
        userlog.clear();
        saveToFile(context, false);

        for (UserlogChangeListener listener : listeners) {
            listener.onUserlogCleared();
        }
    }

    public static UserlogEntry.Type findType(Class c) {
        String pkg = c.getPackage().getName();
        int index = pkg.lastIndexOf('.');
        pkg = pkg.substring(index + 1);

        switch (pkg) {
            case "activities":
            case "dialogs":
            case "fragments":
                return UserlogEntry.Type.UI;
            case "appstate":
                return UserlogEntry.Type.EVENTS;
            case "asynctasks":
            case "ssl":
                return UserlogEntry.Type.NETWORK;
            case "util":
            case "userlog":
            case "bawifi":
                return UserlogEntry.Type.SYSTEM;
            default:
                System.out.println("Hello");
                return null;
        }
    }
}
