package de.sgoral.bawifi.asynctasks;

import android.content.Context;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import de.sgoral.bawifi.R;
import de.sgoral.bawifi.util.HttpUtil;
import de.sgoral.bawifi.util.Logger;
import de.sgoral.bawifi.util.RegexpUtil;

/**
 * Checks if the user is authenticated on the BA Leipzig WiFi network.
 */
public class CheckAuthenticatedTask extends RetryEnabledAsyncTask<String, Void, Boolean> {

    private final Context context;

    /**
     * Creates a new async task for checking if the user is authenticated.
     *
     * @param context
     */
    public CheckAuthenticatedTask(Context context) {
        super(false);
        this.context = context;
        Logger.log(this, "Task created");
    }

    protected Boolean doTask(String... urls) throws IOException {
        Logger.log(this, "Task running with ", urls.length, " url(s)");
        if (urls.length != 1) {
            throw new IllegalArgumentException("Unexpected number of parameters: " + urls.length + ", expected 1");
        }

        String url = urls[0];
        Logger.log(this, "Url: ", url);

        HttpURLConnection connection = HttpUtil.openUrl(context, new URL(url), null);
        String status = HttpUtil.parseResponse(connection, RegexpUtil.STATUS_MESSAGE);
        Logger.log(this, "Response message: ", status);
        return context.getString(R.string.status_message_authenticated).equals(status);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Logger.log(this, "Task starting");
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        Logger.log(this, "Task finished, result: ", aBoolean);
    }
}
