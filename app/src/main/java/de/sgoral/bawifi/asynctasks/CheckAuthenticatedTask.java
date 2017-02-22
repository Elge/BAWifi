package de.sgoral.bawifi.asynctasks;

import android.content.Context;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import de.sgoral.bawifi.R;
import de.sgoral.bawifi.util.HttpUtil;
import de.sgoral.bawifi.util.Logger;

/**
 * Checks if the user is authenticated on the BA Leipzig WiFi network.
 */
public class CheckAuthenticatedTask extends RetryEnabledAsyncTask<CheckAuthenticatedPayload, Void, Boolean> {

    private final Context context;

    /**
     * Creates a new async task for checking if the user is authenticated.
     *
     * @param context The application environment.
     */
    public CheckAuthenticatedTask(Context context) {
        super(false);
        this.context = context;
        Logger.log(this, "Task created");
    }

    protected Boolean doTask(CheckAuthenticatedPayload... payloads) throws IOException {
        Logger.log(this, "Task running with ", payloads.length, " payload(s)");
        if (payloads.length != 1) {
            throw new IllegalArgumentException("Unexpected number of parameters: " + payloads.length + ", expected 1");
        }

        CheckAuthenticatedPayload payload = payloads[0];
        Logger.log(this, "Payload data: ", payload);

        HttpURLConnection connection = HttpUtil.openUrl(context, new URL(payload.getUrl()), null);
        String status = HttpUtil.parseResponse(connection, payload.getPattern());
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
