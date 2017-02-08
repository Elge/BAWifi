package de.sgoral.bawifi.asynctasks;

import android.content.Context;
import android.os.AsyncTask;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.regex.Pattern;

import javax.net.ssl.SSLSocketFactory;

import de.sgoral.bawifi.appstatus.ApplicationStatus;
import de.sgoral.bawifi.appstatus.ApplicationStatusManager;
import de.sgoral.bawifi.util.AuthenticationPayload;
import de.sgoral.bawifi.util.HttpUtil;
import de.sgoral.bawifi.util.Logger;
import de.sgoral.bawifi.util.PreferencesUtil;
import de.sgoral.bawifi.util.RegexpUtil;

/**
 * Performs authentication for BA Leipzig WiFi network using a custom, http based flow. Accepts the
 * insecure BA Leipzig SSL certificate.
 */
public class LoginTask extends AsyncTask<AuthenticationPayload, Void, Boolean> {

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
    protected Boolean doInBackground(AuthenticationPayload... payloads) {
        if (payloads.length == 0) {
            return false;
        }

        Logger.log(this.getClass(), "Starting login action", context);
        ApplicationStatusManager.changeApplicationStatus(ApplicationStatus.STATUS_AUTHENTICATING);
        AuthenticationPayload payload = payloads[0];
        try {
            // Step 1
            URL url = new URL(payload.getUrl());
            HttpURLConnection connection = HttpUtil.openUrl(this.context, url, null);

            String redirectUrl = HttpUtil.parseResponse(connection, RegexpUtil.META_REDIRECT, false, context);
            if (redirectUrl == null) {
                Logger.log(this.getClass(), "No redirect meta tag found", context);
                return false;
            }

            // Step 2
            connection = HttpUtil.openUrl(this.context, new URL(url, redirectUrl), null);
            String location = connection.getHeaderField("Location");
            if (location == null) {
                Logger.log(this.getClass(), "No location header found", context);
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
            HashMap<String, String> result = HttpUtil.parseResponse(connection, map, false, context);

            if (result == null || result.size() != 5) {
                Logger.log(this.getClass(), "Unexpected result size: " + (result == null ? "null" : result.size()), context);
                return false;
            }
            Logger.log(this.getClass(), "Resultset: " + result.size(), context);

            // Step 4
            HashMap<String, String> data = new HashMap<>();
            data.put("UserName", payload.getUsername());
            data.put("Password", payload.getPassword());
            data.put("challenge", result.get("challenge"));
            data.put("uamip", result.get("uamip"));
            data.put("uamport", result.get("uamport"));
            data.put("button", result.get("submit"));
            connection = HttpUtil.openUrl(this.context, new URL(url, result.get("action")), data);
            redirectUrl = HttpUtil.parseResponse(connection, RegexpUtil.META_REDIRECT, false, context);

            // Step 5
            connection = HttpUtil.openUrl(this.context, new URL(redirectUrl), null);
            map.clear();
            map.put("logouturl", RegexpUtil.LOGOUT_URL);
            map.put("statusurl", RegexpUtil.STATUS_URL);
            result = HttpUtil.parseResponse(connection, map, false, context);

            String logoutUrl = result.get("logouturl");
            String statusUrl = result.get("statusurl");

            PreferencesUtil prefUtil = PreferencesUtil.getInstance(context);
            prefUtil.setLogoutUrl(logoutUrl);
            prefUtil.setStatusUrl(statusUrl);

            if (statusUrl != null && logoutUrl != null) {
                ApplicationStatusManager.changeApplicationStatus(ApplicationStatus.STATUS_AUTHENTICATED);
                return true;
            }
        } catch (MalformedURLException | UnsupportedEncodingException | ProtocolException e) {
            Logger.printStackTrace(this.getClass(), e);
        } catch (IOException e) {
            Logger.printStackTrace(this.getClass(), e);
        }

        ApplicationStatusManager.changeApplicationStatus(ApplicationStatus.STATUS_CONNECTED);
        return false;
    }

}
