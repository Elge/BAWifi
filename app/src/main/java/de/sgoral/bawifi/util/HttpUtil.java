package de.sgoral.bawifi.util;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
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

import de.sgoral.bawifi.R;

/**
 * Utility classes for HTTP and HTTPS connections.
 */
public class HttpUtil {

    /**
     * Shortcut for using {@link #parseResponse(HttpURLConnection, HashMap, boolean, Context)} with only one
     * pattern.
     *
     * @return The result of the pattern, or null if no result is found.
     * @throws IOException
     * @see #parseResponse(HttpURLConnection, HashMap, boolean, Context)
     */
    public static String parseResponse(HttpURLConnection connection, Pattern pattern, boolean log, Context context) throws IOException {
        HashMap<String, Pattern> input = new HashMap<>();
        input.put("result", pattern);
        HashMap<String, String> result = parseResponse(connection, input, log, context);
        return result == null ? null : result.get("result");
    }

    /**
     * Reads the server response, applying the patterns on every line. If a match is found, the
     * result is added to the returned {@link HashMap} using the same key as the patterns map.
     *
     * @param connection The {@link HttpURLConnection} to read the response from.
     * @param patterns   A {@link HashMap} containing the regular expressions to match the response lines against. The keys are used to build the response map.
     * @param log        Set to true to log every line in the response.
     * @param context    The application context to use while logging.
     * @return A {@link HashMap} containing the matched groups using the same keys as the input patterns.
     * @throws IOException
     */
    public static HashMap<String, String> parseResponse(HttpURLConnection connection, HashMap<String, Pattern> patterns, boolean log, Context context) throws IOException {
        Logger.log(HttpUtil.class, "RESPONSE:", context);

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
                    Logger.log(HttpUtil.class, line, context);
                }
            }
        }
        reader.close();
        return results;
    }

    /**
     * Opens a connection to the specified url.
     *
     * @param context The application environment.
     * @param url     The url to connect to.
     * @param data    The data to POST to the url. If null, a GET request will be sent.
     * @return The created {@link HttpURLConnection}.
     * @throws IOException
     */
    public static HttpURLConnection openUrl(final Context context, URL url, HashMap<String, String> data) throws IOException {
        Logger.log(HttpUtil.class, "Opening url '" + url.toString() + "'", context);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        // Special handling for https connections
        if (connection instanceof HttpsURLConnection) {
            ((HttpsURLConnection) connection).setSSLSocketFactory(getSSLSocketFactory(context));
            ((HttpsURLConnection) connection).setHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    Logger.log(this.getClass(), hostname, context);
                    // TODO failing hostname verification might point to wrong certificate? check again
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

        Logger.log(HttpUtil.class, "Response: " + connection.getResponseCode(), context);

        return connection;
    }

    /**
     * Initialises the SSL handler required to accept the bad SSL certificate for BA Leipzig.
     *
     * @param context The application environment.
     * @return The {@link SSLSocketFactory} capable of accepting the SSL certificate.
     */
    public static SSLSocketFactory getSSLSocketFactory(Context context) {
        try {
            final KeyStore ks = KeyStore.getInstance(context.getString(R.string.mystore_type));

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

            return new AdditionalKeyStoresSSLSocketFactory(ks);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

