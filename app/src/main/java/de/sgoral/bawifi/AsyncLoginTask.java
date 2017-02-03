package de.sgoral.bawifi;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

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

import de.sgoral.bawifi.util.ApplicationContextProvider;
import de.sgoral.bawifi.util.Logger;

/**
 * Created by cs16sg1 on 27.01.17.
 */

public class AsyncLoginTask extends AsyncTask<AuthenticationPayload, Void, Boolean> {

    public static final Pattern META_REDIRECT =
            Pattern.compile("<meta http-equiv=\"refresh\" content=\"\\d+;\\s?url=([^\"]+)\">",
                    Pattern.CASE_INSENSITIVE);
    public static final Pattern FORM_ACTION =
            Pattern.compile("<form name=\"form1\" method=\"post\" action=\"([^\"]+)\">",
                    Pattern.CASE_INSENSITIVE);
    public static final Pattern CHALLENGE_VALUE = generateInputElementPattern("hidden", "challenge");
    public static final Pattern UAMIP_VALUE = generateInputElementPattern("hidden", "uamip");
    public static final Pattern UAMPORT_VALUE = generateInputElementPattern("hidden", "uamport");
    public static final Pattern USERURL_VALUE = generateInputElementPattern("hidden", "userurl");
    public static final Pattern SUBMIT_VALUE = generateInputElementPattern("submit", "button");
    public static final Pattern LOGOUT_URL =
            Pattern.compile("<LogoutUrl>([^\"]+)</LogoutUrl>", Pattern.CASE_INSENSITIVE);

    private static Pattern generateInputElementPattern(String type, String name) {
        return Pattern.compile(
                "<input type=\"" + type + "\"[^>]*name=\"" + name + "\" value=\"([^\"]+)\"[^>]*>",
                Pattern.CASE_INSENSITIVE);
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

            String redirectUrl = parseResponse(connection, META_REDIRECT, false);
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
            map.put("action", FORM_ACTION);
            map.put("challenge", CHALLENGE_VALUE);
            map.put("uamip", UAMIP_VALUE);
            map.put("uamport", UAMPORT_VALUE);
            map.put("submit", SUBMIT_VALUE);
            HashMap<String, String> result = parseResponse(connection, map, false);

            Logger.log(this.getClass(), "Resultset: " + result.size());
            if (result.size() != 5) {
                Logger.log(this.getClass(), "Unexpected result size: " + result.size());
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
            connection = openUrl(new URL(url, result.get("action")), data);
            redirectUrl = parseResponse(connection, META_REDIRECT, false);

            // Step 5
            connection = openUrl(new URL(redirectUrl), null);
            String logoutUrl = parseResponse(connection, LOGOUT_URL, true);

            return false;
        } catch (MalformedURLException e) {
            Logger.printStackTrace(this.getClass(), e);
        } catch (UnsupportedEncodingException e) {
            Logger.printStackTrace(this.getClass(), e);
        } catch (ProtocolException e) {
            Logger.printStackTrace(this.getClass(), e);
        } catch (IOException e) {
            Logger.printStackTrace(this.getClass(), e);
        }

        return false;
    }

    private String parseResponse(HttpURLConnection connection, Pattern pattern, boolean log) throws IOException {
        HashMap<String, Pattern> input = new HashMap<>();
        input.put("result", pattern);
        HashMap<String, String> result = parseResponse(connection, input, log);
        return result.get("result");
    }

    @Nullable
    private HashMap<String, String> parseResponse(HttpURLConnection connection, HashMap<String, Pattern> patterns, boolean log) throws IOException {
        Logger.log(this.getClass(), "RESPONSE:");

        Set<String> keys = patterns.keySet();
        HashMap<String, String> results = new HashMap<String, String>();

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


    @NonNull
    private HttpURLConnection openUrl(URL url, HashMap<String, String> data) throws IOException {
        Logger.log(this.getClass(), "Opening url '" + url.toString() + "'");

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        if (connection instanceof HttpsURLConnection) {
            ((HttpsURLConnection) connection).setHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    Logger.log(this.getClass(), hostname);
                    return true;
                }
            });
            ((HttpsURLConnection) connection).setSSLSocketFactory(getSSLSocketFactory(ApplicationContextProvider.getAppContext()));
        }

        if (data != null) {
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
        } else {
            connection.setRequestMethod("GET");
        }

        Logger.log(this.getClass(), "Response: " + connection.getResponseCode());

        return connection;
    }

    private SSLSocketFactory getSSLSocketFactory(Context context) {
        try {
            final KeyStore ks = KeyStore.getInstance("BKS");

            // the bks file we generated above
            final InputStream in = context.getResources().openRawResource(R.raw.mystore);
            try {
                // don't forget to put the password used above in strings.xml/mystore_password
                ks.load(in, context.getString(R.string.mystore_password).toCharArray());
            } finally {
                in.close();
            }

            return new AdditionalKeyStoresSSLSocketFactory(ks);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
