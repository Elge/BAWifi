package de.sgoral.bawifi.asynctasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.regex.Pattern;

import javax.net.ssl.SSLSocketFactory;

import de.sgoral.bawifi.AuthenticationPayload;
import de.sgoral.bawifi.util.HttpUtil;
import de.sgoral.bawifi.util.Logger;
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

        Logger.log(this.getClass(), "Starting login action");
        AuthenticationPayload payload = payloads[0];
        try {
            // Step 1
            URL url = new URL(payload.getUrl());
            HttpURLConnection connection = HttpUtil.openUrl(this.context, url, null);

            String redirectUrl = HttpUtil.parseResponse(connection, RegexpUtil.META_REDIRECT, false);
            if (redirectUrl == null) {
                Logger.log(this.getClass(), "No redirect meta tag found");
                return false;
            }

            // Step 2
            connection = HttpUtil.openUrl(this.context, new URL(url, redirectUrl), null);
            String location = connection.getHeaderField("Location");
            if (location == null) {
                Logger.log(this.getClass(), "No location header found");
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
            HashMap<String, String> result = HttpUtil.parseResponse(connection, map, false);

            if (result == null || result.size() != 5) {
                Logger.log(this.getClass(), "Unexpected result size: " + (result == null ? "null" : result.size()));
                return false;
            }
            Logger.log(this.getClass(), "Resultset: " + result.size());

            // Step 4
            HashMap<String, String> data = new HashMap<>();
            data.put("UserName", payload.getUsername());
            data.put("Password", payload.getPassword());
            data.put("challenge", result.get("challenge"));
            data.put("uamip", result.get("uamip"));
            data.put("uamport", result.get("uamport"));
            data.put("button", result.get("submit"));
            connection = HttpUtil.openUrl(this.context, new URL(url, result.get("action")), data);
            redirectUrl = HttpUtil.parseResponse(connection, RegexpUtil.META_REDIRECT, false);

            // Step 5
            connection = HttpUtil.openUrl(this.context, new URL(redirectUrl), null);
            String logoutUrl = HttpUtil.parseResponse(connection, RegexpUtil.LOGOUT_URL, true);

            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this.context).edit();
            editor.putString("logouturl", logoutUrl);
            editor.apply();

            return false;
        } catch (MalformedURLException | UnsupportedEncodingException | ProtocolException e) {
            Logger.printStackTrace(this.getClass(), e);
        } catch (IOException e) {
            Logger.printStackTrace(this.getClass(), e);
        }

        return false;
    }

}
