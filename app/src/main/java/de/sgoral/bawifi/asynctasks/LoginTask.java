package de.sgoral.bawifi.asynctasks;

import android.content.Context;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.regex.Pattern;

import javax.net.ssl.SSLSocketFactory;

import de.sgoral.bawifi.appstate.ApplicationState;
import de.sgoral.bawifi.appstate.ApplicationStateManager;
import de.sgoral.bawifi.util.HttpUtil;
import de.sgoral.bawifi.util.Logger;
import de.sgoral.bawifi.util.PreferencesUtil;
import de.sgoral.bawifi.util.RegexpUtil;

/**
 * Performs authentication for BA Leipzig WiFi network using a custom, http based flow. Accepts the
 * insecure BA Leipzig SSL certificate.
 */
public class LoginTask extends RetryEnabledAsyncTask<LoginPayload, Void, Boolean> {

    private final Context context;
    private SSLSocketFactory sslSocketFactory = null;

    /**
     * Creates a new async task for authenticating the user to the BA Leipzig network.
     *
     * @param context The application environment.
     */
    public LoginTask(Context context) {
        super(false);
        this.context = context;
        Logger.log(this, "Task created");
    }

    @Override
    protected Boolean doTask(LoginPayload... payloads) throws IOException {
        Logger.log(this, "Task running with ", payloads.length, " payload(s)");
        if (payloads.length != 1) {
            throw new IllegalArgumentException("Unexpected number of parameters: " + payloads.length + ", expected 1");
        }

        LoginPayload payload = payloads[0];
        Logger.log(this, "Payload data: ", payload);

        // Step 1
        URL url = new URL(payload.getUrl());
        HttpURLConnection connection = HttpUtil.openUrl(this.context, url, null);

        String redirectUrl = HttpUtil.parseResponse(connection, RegexpUtil.META_REDIRECT, false);
        if (redirectUrl == null) {
            Logger.log(this, "No redirect url found, aborting");
            return false;
        }

        // Step 2
        connection = HttpUtil.openUrl(this.context, new URL(url, redirectUrl), null);
        String location = connection.getHeaderField("Location");
        if (location == null) {
            Logger.log(this, "No location header found, aborting");
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

        if (result == null) {
            Logger.log(this, "Result is null, aborting");
            return false;
        } else if (result.size() != 5) {
            Logger.log(this, "Unexpected result size: ", result.size());
            for (String key : result.keySet()) {
                Logger.log(this, '\t', key, '=', result.get(key));
            }
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

        if (redirectUrl == null) {
            Logger.log(this, "No redirect url found, aborting");
            return false;
        }

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

        Logger.log(this, "LogoutUrl: ", logoutUrl);
        Logger.log(this, "StatusUrl: ", statusUrl);
        Logger.log(this, "StatusMessage: ", statusMessage);

        PreferencesUtil prefUtil = PreferencesUtil.getInstance(context);
        prefUtil.setLogoutUrl(logoutUrl);
        prefUtil.setStatusUrl(statusUrl);
        prefUtil.setStatusMessage(statusMessage);

        if (statusUrl != null && logoutUrl != null) {
            return true;
        }

        return false;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        ApplicationStateManager.changeApplicationState(ApplicationState.STATE_AUTHENTICATING);
        Logger.log(this, "Task starting");
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);

        if (aBoolean == true) {
            ApplicationStateManager.changeApplicationState(ApplicationState.STATE_AUTHENTICATED);
        } else {
            ApplicationStateManager.changeApplicationState(ApplicationState.STATE_CONNECTED);
        }
        Logger.log(this, "Task finished, result: ", aBoolean);
    }
}
