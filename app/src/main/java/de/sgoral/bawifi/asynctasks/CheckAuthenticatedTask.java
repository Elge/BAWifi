package de.sgoral.bawifi.asynctasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.URL;

import de.sgoral.bawifi.R;
import de.sgoral.bawifi.util.HttpUtil;
import de.sgoral.bawifi.util.Logger;

/**
 * Created by sebastianprivat on 10.02.17.
 */

public class CheckAuthenticatedTask extends AsyncTask<CheckAuthenticatedPayload, Void, Boolean> {

    private final Context context;

    public CheckAuthenticatedTask(Context context) {
        this.context = context;
        Logger.log(this, "Task created");
    }

    @Override
    protected Boolean doInBackground(CheckAuthenticatedPayload... payloads) {
        Logger.log(this, "Task running with ", payloads.length, " payload(s)");
        if (payloads.length != 1) {
            throw new IllegalArgumentException("Unexpected number of parameters: " + payloads.length + ", expected 1");
        }

        CheckAuthenticatedPayload payload = payloads[0];
        Logger.log(this, "Payload data: ", payload);

        try {
            HttpURLConnection connection = HttpUtil.openUrl(context, new URL(payload.getUrl()), null);
            String status = HttpUtil.parseResponse(connection, payload.getPattern());
            Logger.log(this, "Response message: ", status);
            return context.getString(R.string.status_message_authenticated).equals(status);
        } catch (SocketException e) {
            // Probably timed out, retry
            Logger.log(this, e);
            Logger.log(this, "Ignoring SocketException and retrying");
            return doInBackground(payloads);
        } catch (IOException e) {
            // Ignore
            Logger.log(this, e);
            Logger.log(this, "Ignoring IOException");
        }

        return false;
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
