package de.sgoral.bawifi.asynctasks;

import android.content.Context;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import de.sgoral.bawifi.appstate.ApplicationState;
import de.sgoral.bawifi.appstate.ApplicationStateManager;
import de.sgoral.bawifi.util.HttpUtil;
import de.sgoral.bawifi.util.Logger;
import de.sgoral.bawifi.util.NetworkUtil;
import de.sgoral.bawifi.util.PreferencesUtil;
import de.sgoral.bawifi.util.RegexpUtil;

/**
 * Opens the logout url to de-authenticate the user from the Wifi network.
 */
public class LogoutTask extends RetryEnabledAsyncTask<String, Void, Boolean> {

    private final Context context;

    /**
     * Creates a new task for deauthenticating the user from the network.
     *
     * @param context
     */
    public LogoutTask(Context context) {
        super(false);
        this.context = context;
        Logger.log(this, "Task created");
    }

    @Override
    protected Boolean doTask(String... urls) throws IOException {
        Logger.log(this, "Task running with ", urls.length, " url(s)");
        if (urls.length != 1) {
            throw new IllegalArgumentException("Unexpected number of parameters: " + urls.length + ", expected 1");
        }

        Logger.log(this, "Url: ", urls[0]);
        URL url = new URL(urls[0]);
        NetworkUtil.bypassCaptivePortal(context);
        HttpURLConnection connection = HttpUtil.openUrl(this.context, url, null);
        String statusMessage = HttpUtil.parseResponse(connection, RegexpUtil.STATUS_MESSAGE);

        Logger.log(this, "StatusMessage: ", statusMessage);

        PreferencesUtil.getInstance(context).setStatusMessage(statusMessage);

        return (connection.getResponseCode() == HttpURLConnection.HTTP_OK
                || connection.getResponseCode() == HttpURLConnection.HTTP_MOVED_TEMP);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        ApplicationStateManager.changeApplicationState(ApplicationState.STATE_DEAUTHENTICATING);
        Logger.log(this, "Task starting");
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);

        if (aBoolean) {
            ApplicationStateManager.changeApplicationState(ApplicationState.STATE_CONNECTED);
        } else {
            ApplicationStateManager.changeApplicationState(ApplicationState.STATE_AUTHENTICATED);
        }
        Logger.log(this, "Task finished, result: ", aBoolean);
    }
}
