package de.sgoral.bawifi.asynctasks;

import android.content.Context;
import android.os.AsyncTask;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.URL;
import java.util.HashMap;
import java.util.regex.Pattern;

import javax.net.ssl.SSLSocketFactory;

import de.sgoral.bawifi.appstate.ApplicationState;
import de.sgoral.bawifi.appstate.ApplicationStateManager;
import de.sgoral.bawifi.util.HttpUtil;
import de.sgoral.bawifi.util.PreferencesUtil;
import de.sgoral.bawifi.util.RegexpUtil;

/**
 * Performs authentication for BA Leipzig WiFi network using a custom, http based flow. Accepts the
 * insecure BA Leipzig SSL certificate.
 */
public class LoginTask extends AsyncTask<LoginPayload, Void, Boolean> {

    private final Context context;
    private SSLSocketFactory sslSocketFactory = null;


    /**
     * Creates a new async task for authenticating the user to the BA Leipzig network.
     *
     * @param context The application environment.
     */
    public LoginTask(Context context) {
        this.context = context;
    }

    @Override
    protected Boolean doInBackground(LoginPayload... payloads) {
        if (payloads.length != 1) {
            throw new IllegalArgumentException("Unexpected number of parameters: " + payloads.length + ", expected 1");
        }

        LoginPayload payload = payloads[0];
        try {
            // Step 1
            URL url = new URL(payload.getUrl());
            HttpURLConnection connection = HttpUtil.openUrl(this.context, url, null);

            String redirectUrl = HttpUtil.parseResponse(connection, RegexpUtil.META_REDIRECT, false);
            if (redirectUrl == null) {
                return false;
            }

            // Step 2
            connection = HttpUtil.openUrl(this.context, new URL(url, redirectUrl), null);
            String location = connection.getHeaderField("Location");
            if (location == null) {
                return false;
            }

            // Step 3
            url = new URL(location);
            connection = HttpUtil.openUrl(this.context, url, null);

            HashMap<String, Pattern> map = new HashMap<>();
            map.put("action", RegexpUtil.FORM_ACTION);
            map.put("challenge", RegexpUtil.CHALLENGE_VALUE);
            map.put("uamip", RegexpUtil.UAMIP_VALUE);
            map.put("uamport", RegexpUtil.UAMPORT_VALUE);
            map.put("submit", RegexpUtil.SUBMIT_VALUE);
            HashMap<String, String> result = HttpUtil.parseResponse(connection, map);

            if (result == null || result.size() != 5) {
                return false;
            }

            // Step 4
            HashMap<String, String> data = new HashMap<>();
            data.put("UserName", payload.getUsername());
            data.put("Password", payload.getPassword());
            data.put("challenge", result.get("challenge"));
            data.put("uamip", result.get("uamip"));
            data.put("uamport", result.get("uamport"));
            data.put("button", result.get("submit"));
            connection = HttpUtil.openUrl(this.context, new URL(url, result.get("action")), data);
            redirectUrl = HttpUtil.parseResponse(connection, RegexpUtil.META_REDIRECT);

            // Step 5
            connection = HttpUtil.openUrl(this.context, new URL(redirectUrl), null);
            map.clear();
            map.put("logouturl", RegexpUtil.LOGOUT_URL);
            map.put("statusurl", RegexpUtil.STATUS_URL);
            map.put("statusmessage", RegexpUtil.STATUS_MESSAGE);
            result = HttpUtil.parseResponse(connection, map);

            String logoutUrl = result.get("logouturl");
            String statusUrl = result.get("statusurl");
            String statusMessage = result.get("statusmessage");

            PreferencesUtil prefUtil = PreferencesUtil.getInstance(context);
            prefUtil.setLogoutUrl(logoutUrl);
            prefUtil.setStatusUrl(statusUrl);
            prefUtil.setStatusMessage(statusMessage);

            if (statusUrl != null && logoutUrl != null) {
                return true;
            }
        } catch (SocketException e) {
            // Socket exception probably means we timed out, retry.
            return doInBackground(payloads);
        } catch (IOException e) {
            // Ignore
        }

        return false;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        ApplicationStateManager.changeApplicationStatus(ApplicationState.STATUS_AUTHENTICATING);
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);

        if (aBoolean == true) {
            ApplicationStateManager.changeApplicationStatus(ApplicationState.STATUS_AUTHENTICATED);
        } else {
            ApplicationStateManager.changeApplicationStatus(ApplicationState.STATUS_CONNECTED);
        }
    }
}
