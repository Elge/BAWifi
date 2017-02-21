package de.sgoral.bawifi.asynctasks;

import android.content.Context;
import android.os.AsyncTask;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.URL;

import de.sgoral.bawifi.R;
import de.sgoral.bawifi.util.HttpUtil;

/**
 * Created by sebastianprivat on 10.02.17.
 */

public class CheckAuthenticatedTask extends AsyncTask<CheckAuthenticatedPayload, Void, Boolean> {

    private final Context context;

    public CheckAuthenticatedTask(Context context) {
        this.context = context;
    }

    @Override
    protected Boolean doInBackground(CheckAuthenticatedPayload... payloads) {
        if (payloads.length != 1) {
            throw new IllegalArgumentException("Unexpected number of parameters: " + payloads.length + ", expected 1");
        }

        CheckAuthenticatedPayload payload = payloads[0];

        try {
            HttpURLConnection connection = HttpUtil.openUrl(context, new URL(payload.getUrl()), null);
            String response = HttpUtil.parseResponse(connection, payload.getPattern());
            return context.getString(R.string.status_message_authenticated).equals(response);
        } catch (SocketException e) {
            // Probably timed out, retry
            return doInBackground(payloads);
        } catch (IOException e) {
            // Ignore
        }

        return false;
    }

}
