package de.sgoral.bawifi;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.security.KeyStore;
import java.util.HashMap;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;

import de.sgoral.bawifi.util.Logger;
import de.sgoral.bawifi.util.RegexpUtil;

/**
 * Performs authentication for BA Leipzig WiFi network using a custom, http based flow. Accepts the
 * insecure BA Leipzig SSL certificate.
 */
class AsyncAuthTask extends AsyncTask<AuthenticationPayload, Void, Boolean> {

    private final Context context;
    private SSLSocketFactory sslSocketFactory = null;


    /**
     * Creates a new async task for authenticating the user to the BA Leipzig network.
     *
     * @param context The application context.
     */
    AsyncAuthTask(Context context) {
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
            HttpURLConnection connection = openUrl(url, null);

            String redirectUrl = parseResponse(connection, RegexpUtil.META_REDIRECT, false);
            if (redirectUrl == null) {
                Logger.log(this.getClass(), "No redirect meta tag found");
                return false;
            }

            // Step 2
            connection = openUrl(new URL(url, redirectUrl), null);
            String location = connection.getHeaderField("Location");
            if (location == null) {
                Logger.log(this.getClass(), "No location header found");
                return false;
            }

            // Step 3
            url = new URL(location);
            connection = openUrl(url, null);

            HashMap<String, Pattern> map = new HashMap<>();
            map.put("action", RegexpUtil.FORM_ACTION);
            map.put("challenge", RegexpUtil.CHALLENGE_VALUE);
            map.put("uamip", RegexpUtil.UAMIP_VALUE);
            map.put("uamport", RegexpUtil.UAMPORT_VALUE);
            map.put("submit", RegexpUtil.SUBMIT_VALUE);
            HashMap<String, String> result = parseResponse(connection, map, false);

            if (result == null || result.size() != 5) {
                Logger.log(this.getClass(), "Unexpected result size: " + (result != null ? result.size() : "null"));
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
            connection = openUrl(new URL(url, result.get("action")), data);
            redirectUrl = parseResponse(connection, RegexpUtil.META_REDIRECT, false);

            // Step 5
            connection = openUrl(new URL(redirectUrl), null);
            String logoutUrl = parseResponse(connection, RegexpUtil.LOGOUT_URL, true);

            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
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

    /**
     * Shortcut for using {@link #parseResponse(HttpURLConnection, HashMap, boolean)} with only one
     * pattern.
     *
     * @return The result of the pattern, or null if no result is found.
     * @throws IOException
     * @see #parseResponse(HttpURLConnection, HashMap, boolean)
     */
    private String parseResponse(HttpURLConnection connection, Pattern pattern, boolean log) throws IOException {
        HashMap<String, Pattern> input = new HashMap<>();
        input.put("result", pattern);
        HashMap<String, String> result = parseResponse(connection, input, log);
        return result == null ? null : result.get("result");
    }

    /**
     * Reads the server response, applying the patterns on every line. If a match is found, the
     * result is added to the returned {@link HashMap} using the same key as the patterns map.
     *
     * @param connection The {@link HttpURLConnection} to read the response from.
     * @param patterns   A {@link HashMap} containing the regular expressions to match the response lines against. The keys are used to build the response map.
     * @param log        Set to true to log every line in the response.
     * @return A {@link HashMap} containing the matched groups using the same keys as the input patterns.
     * @throws IOException
     */
    private HashMap<String, String> parseResponse(HttpURLConnection connection, HashMap<String, Pattern> patterns, boolean log) throws IOException {
        Logger.log(this.getClass(), "RESPONSE:");

        Set<String> keys = patterns.keySet();
        HashMap<String, String> results = new HashMap<>();

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            for (String key : keys) {
                Pattern pattern = patterns.get(key);
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    results.put(key, matcher.group(1));
                }
                if (log) {
                    Logger.log(this.getClass(), line);
                }
            }
        }
        reader.close();
        return results;
    }

    /**
     * Opens a connection to the specified url.
     *
     * @param url  The url to connect to.
     * @param data The data to POST to the url. If null, a GET request will be sent.
     * @return The created {@link HttpURLConnection}.
     * @throws IOException
     */
    private HttpURLConnection openUrl(URL url, HashMap<String, String> data) throws IOException {
        Logger.log(this.getClass(), "Opening url '" + url.toString() + "'");

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        // Special handling for https connections
        if (connection instanceof HttpsURLConnection) {
            ((HttpsURLConnection) connection).setSSLSocketFactory(getSSLSocketFactory(this.context));
            ((HttpsURLConnection) connection).setHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    Logger.log(this.getClass(), hostname);
                    return "10.10.0.1".equals(hostname);
                }
            });
        }

        if (data == null) {
            connection.setRequestMethod("GET");
        } else {
            // data is delivered as a POST request
            StringBuilder content = new StringBuilder();
            for (String key : data.keySet()) {
                content.append(key);
                content.append('=');
                content.append(data.get(key));
                content.append('&');
            }

            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Content-Length", String.valueOf(content.length()));

            OutputStream writer = connection.getOutputStream();
            writer.write(content.toString().getBytes());
            writer.flush();
            writer.close();
        }

        Logger.log(this.getClass(), "Response: " + connection.getResponseCode());

        return connection;
    }

    /**
     * Initialises the SSL handler required to accept the bad SSL certificate for BA Leipzig.
     *
     * @param context The application context.
     * @return The {@link SSLSocketFactory} capable of accepting the SSL certificate.
     */
    private SSLSocketFactory getSSLSocketFactory(Context context) {
        if (sslSocketFactory == null) {
            try {
                final KeyStore ks = KeyStore.getInstance("BKS");

                // the bks file we generated above
                final InputStream in = context.getResources().openRawResource(R.raw.mystore);
                try {
                    // don't forget to put the password used above in strings.xml/mystore_password
                    ks.load(in, context.getString(R.string.mystore_password).toCharArray());
                } finally {
                    try {
                        in.close();
                    } catch (IOException e) {
                        // Ignore
                    }
                }

                sslSocketFactory = new AdditionalKeyStoresSSLSocketFactory(ks);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return sslSocketFactory;
    }

}