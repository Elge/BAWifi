package de.sgoral.bawifi.asynctasks;

import android.os.AsyncTask;

import java.net.SocketException;

import de.sgoral.bawifi.util.Logger;

/**
 * Async task that automatically retries the task if an Exception occurs. Normally only retries on
 * SocketException, but can retry on any exception class using instanceof. RuntimeExceptions are
 * never handled.
 */
abstract class RetryEnabledAsyncTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {

    private static final int DEFAULT_MAX_RETRIES = 3;

    protected final int maxRetries;
    protected final Class<? extends Throwable> retryOn;
    protected final Result returnOnFail;

    protected int retries;

    /**
     * Creates a new task that retries on SocketException up to {@value DEFAULT_MAX_RETRIES} times.
     */
    protected RetryEnabledAsyncTask() {
        this(SocketException.class, DEFAULT_MAX_RETRIES, null);
    }


    /**
     * Creates a new task that retries on SocketException up to the specified number of times.
     *
     * @param maxRetries The maximum number of retries before the task fails. 0 means no retries.
     *                   Negative value means infinite retries.
     */
    protected RetryEnabledAsyncTask(int maxRetries) {
        this(SocketException.class, maxRetries, null);
    }

    /**
     * Creates a new task that retries on the specified exception up to {@value DEFAULT_MAX_RETRIES} times.
     *
     * @param retryOn The exception class that must occur to trigger a retry. Is checked using instanceof.
     */
    protected RetryEnabledAsyncTask(Class<? extends Throwable> retryOn) {
        this(retryOn, DEFAULT_MAX_RETRIES, null);
    }

    /**
     * Creates a new task that retries on the specified exception up to the specified number of times.
     *
     * @param returnOnFail The value to return when the method fails due to triggering an uncaught
     *                     exception or exceeding the maximum number of retries.
     */
    protected RetryEnabledAsyncTask(Result returnOnFail) {
        this(SocketException.class, DEFAULT_MAX_RETRIES, returnOnFail);
    }

    /**
     * Creates a new task that retries on the specified exception up to the specified number of times.
     *
     * @param retryOn      The exception class that must occur to trigger a retry. Is checked using instanceof.
     * @param maxRetries   The maximum number of retries before the task fails. 0 means no retries.
     *                     Negative value means infinite retries.
     * @param returnOnFail The value to return when the method fails due to triggering an uncaught
     *                     exception or exceeding the maximum number of retries.
     */
    protected RetryEnabledAsyncTask(Class<? extends Throwable> retryOn,
                                    int maxRetries, Result returnOnFail) {
        super();

        this.maxRetries = maxRetries;
        this.retryOn = retryOn;
        this.returnOnFail = returnOnFail;

        retries = 0;
        Logger.log(this, "Task created");
    }

    @Override
    protected final Result doInBackground(Params[] params) {
        try {
            return doTask(params);
        } catch (Throwable t) {
            if (t instanceof RuntimeException) {
                throw (RuntimeException) t;
            }
            Logger.log(this, t);

            if (retryOn.isInstance(t)) {
                if (maxRetries >= 0 && retries >= maxRetries) {
                    Logger.log(this, "Maximum number of retries exceeded, aborting");
                } else {
                    // Probably timed out, retry
                    Logger.log(this, "Ignoring SocketException and retrying");
                    retries++;
                    return doInBackground(params);
                }
            }
        }

        return returnOnFail;
    }

    /**
     * Performs the actual computation. Moved to a separate method to allow proper retrying and
     * exception handling.
     *
     * @see AsyncTask#doInBackground(Object[])
     */
    protected abstract Result doTask(Params[] params) throws Throwable;
}
