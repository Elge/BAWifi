package de.sgoral.bawifi.asynctasks;

import android.content.Context;
import android.os.AsyncTask;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import de.sgoral.bawifi.util.HttpUtil;
import de.sgoral.bawifi.util.Logger;
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
            return false;
        }

        try {
            URL url = new URL(urls[0]);
            HttpURLConnection connection = HttpUtil.openUrl(this.context, url, null);
            HttpUtil.parseResponse(connection, RegexpUtil.META_REDIRECT, false);

            return connection.getResponseCode() == HttpURLConnection.HTTP_OK;
        } catch (IOException e) {
            Logger.printStackTrace(this.getClass(), e);
        }

        return false;
    }
}
