package de.sgoral.bawifi.asynctasks;

import android.content.Context;
import android.os.AsyncTask;

import java.net.SocketTimeoutException;

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
    protected final Context context;

    protected int retries;

    /**
     * Creates a new task that retries on SocketException up to {@value DEFAULT_MAX_RETRIES} times.
     */
    protected RetryEnabledAsyncTask(Context context) {
        this(SocketTimeoutException.class, DEFAULT_MAX_RETRIES, null, context);
    }


    /**
     * Creates a new task that retries on SocketTimeoutException up to the specified number of times.
     *
     * @param maxRetries The maximum number of retries before the task fails. 0 means no retries.
     *                   Negative value means infinite retries.
     */
    protected RetryEnabledAsyncTask(int maxRetries, Context context) {
        this(SocketTimeoutException.class, maxRetries, null, context);
    }

    /**
     * Creates a new task that retries on the specified exception up to {@value DEFAULT_MAX_RETRIES} times.
     *
     * @param retryOn The exception class that must occur to trigger a retry. Is checked using instanceof.
     */
    protected RetryEnabledAsyncTask(Class<? extends Throwable> retryOn, Context context) {
        this(retryOn, DEFAULT_MAX_RETRIES, null, context);
    }

    /**
     * Creates a new task that retries on the specified exception up to the specified number of times.
     *
     * @param returnOnFail The value to return when the method fails due to triggering an uncaught
     *                     exception or exceeding the maximum number of retries.
     */
    protected RetryEnabledAsyncTask(Result returnOnFail, Context context) {
        this(SocketTimeoutException.class, DEFAULT_MAX_RETRIES, returnOnFail, context);
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
                                    int maxRetries, Result returnOnFail, Context context) {
        super();

        this.maxRetries = maxRetries;
        this.retryOn = retryOn;
        this.returnOnFail = returnOnFail;
        this.context = context;

        retries = 0;
        Logger.log(context, this, "Task created");
    }

    @Override
    protected final Result doInBackground(Params[] params) {
        try {
            return doTask(params);
        } catch (Throwable t) {
            if (t instanceof RuntimeException) {
                throw (RuntimeException) t;
            }
            Logger.log(context, this, t);

            if (retryOn.isInstance(t)) {
                if (maxRetries >= 0 && retries >= maxRetries) {
                    Logger.log(context, this, "Maximum number of retries exceeded, aborting");
                } else {
                    // Probably timed out, retry
                    Logger.log(context, this, "Ignoring " + t.getClass().getSimpleName() + " and retrying");
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
