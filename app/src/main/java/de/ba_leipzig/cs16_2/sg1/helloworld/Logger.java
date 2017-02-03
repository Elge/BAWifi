package de.ba_leipzig.cs16_2.sg1.helloworld;

import android.util.Log;

/**
 * Created by sebastianprivat on 02.02.17.
 */

public class Logger {

    private Logger() {}

    public static void log(Class c, String msg) {
        Log.i(c.getCanonicalName(), msg);
    }

    public static void printStackTrace(Class c, Throwable t) {
        StringBuilder builder = new StringBuilder();
        builder.append(t.getClass().getCanonicalName());
        builder.append(':');
        builder.append(t.getMessage());
        log(c, builder.toString());
        for (StackTraceElement element : t.getStackTrace()) {
            builder = new StringBuilder();
            builder.append("\t");
            builder.append(element.getClassName());
            builder.append(':');
            builder.append(element.getMethodName());
            builder.append(" in ");
            builder.append(element.getFileName());
            builder.append(':');
            builder.append(element.getLineNumber());
            log(c, builder.toString());
        }
        if (t.getCause() != null) {
            log(c, "Caused by:");
            printStackTrace(c, t.getCause());
        }
    }

}
