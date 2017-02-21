package de.sgoral.bawifi.asynctasks;

import android.content.Context;
import android.os.AsyncTask;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import de.sgoral.bawifi.appstate.ApplicationState;
import de.sgoral.bawifi.appstate.ApplicationStateManager;
import de.sgoral.bawifi.util.HttpUtil;
import de.sgoral.bawifi.util.PreferencesUtil;
import de.sgoral.bawifi.util.RegexpUtil;

/**
 * Opens the logout url to de-authenticate the user from the Wifi network.
 */
public class LogoutTask extends AsyncTask<String, Void, Boolean> {

    private final Context context;

    public LogoutTask(Context context) {
        this.context = context;
    }

    @Override
    protected Boolean doInBackground(String... urls) {
        if (urls.length != 1) {
            throw new IllegalArgumentException("Unexpected number of parameters: " + urls.length + ", expected 1");
        }

        try {
            URL url = new URL(urls[0]);
            HttpURLConnection connection = HttpUtil.openUrl(this.context, url, null);
            String statusMessage = HttpUtil.parseResponse(connection, RegexpUtil.STATUS_MESSAGE);

            PreferencesUtil.getInstance(context).setStatusMessage(statusMessage);

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK
                    || connection.getResponseCode() == HttpURLConnection.HTTP_MOVED_TEMP) {
                return true;
            }
        } catch (IOException e) {
            // Ignore
        }

        return false;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        ApplicationStateManager.changeApplicationStatus(ApplicationState.STATUS_DEAUTHENTICATING);
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);

        if (aBoolean == true) {
            ApplicationStateManager.changeApplicationStatus(ApplicationState.STATUS_CONNECTED);
        } else {
            ApplicationStateManager.changeApplicationStatus(ApplicationState.STATUS_AUTHENTICATED);
        }
    }
}
