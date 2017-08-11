package de.sgoral.bawifi.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;

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

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;

import de.sgoral.bawifi.R;
import de.sgoral.bawifi.util.ssl.AdditionalKeyStoresSSLSocketFactory;
import de.sgoral.bawifi.util.ssl.IpHostnameVerifier;

/**
 * Utility classes for HTTP and HTTPS connections.
 */
public class HttpUtil {

    // Static class, hide constructor
    private HttpUtil() {
    }

    /**
     * Shortcut for using {@link #parseResponse(HttpURLConnection, HashMap)} with only one
     * pattern.
     *
     * @return The result of the pattern, or null if no result is found.
     * @throws IOException
     * @see #parseResponse(HttpURLConnection, HashMap)
     */
    public static String parseResponse(HttpURLConnection connection, Pattern pattern, boolean log) throws IOException {
        HashMap<String, Pattern> input = new HashMap<>();
        input.put("result", pattern);
        HashMap<String, String> result = parseResponse(connection, input, log);
        return result == null ? null : result.get("result");
    }

    /**
     * Shortcut for using {@link #parseResponse(HttpURLConnection, HashMap, boolean)} with only one
     * pattern.
     *
     * @return The result of the pattern, or null if no result is found.
     * @throws IOException
     * @see #parseResponse(HttpURLConnection, HashMap, boolean)
     */
    public static String parseResponse(HttpURLConnection connection, Pattern pattern) throws IOException {
        return parseResponse(connection, pattern, false);
    }

    /**
     * Reads the server response, applying the patterns on every line. If a match is found, the
     * result is added to the returned {@link HashMap} using the same key as the patterns map.
     *
     * @param connection The {@link HttpURLConnection} to read the response from.
     * @param patterns   A {@link HashMap} containing the regular expressions to match the response
     *                   lines against. The keys are used to build the response map.
     * @return A {@link HashMap} containing the matched groups using the same keys as the input patterns.
     * @throws IOException
     */
    public static HashMap<String, String> parseResponse(HttpURLConnection connection, HashMap<String,
            Pattern> patterns) throws IOException {
        return parseResponse(connection, patterns, false);
    }

    /**
     * Reads the server response, applying the patterns on every line. If a match is found, the
     * result is added to the returned {@link HashMap} using the same key as the patterns map.
     *
     * @param connection The {@link HttpURLConnection} to read the response from.
     * @param patterns   A {@link HashMap} containing the regular expressions to match the response
     *                   lines against. The keys are used to build the response map.
     * @param dumpOutput true to log the output stream content
     * @return A {@link HashMap} containing the matched groups using the same keys as the input patterns.
     * @throws IOException
     */
    public static HashMap<String, String> parseResponse(HttpURLConnection connection, HashMap<String,
            Pattern> patterns, boolean dumpOutput) throws IOException {
        Set<String> keys = patterns.keySet();
        HashMap<String, String> results = new HashMap<>();

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            if (dumpOutput) {
                Logger.log(HttpUtil.class, line);
            }
            for (String key : keys) {
                Pattern pattern = patterns.get(key);
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    results.put(key, matcher.group(1));
                }
            }
        }
        reader.close();
        return results;
    }


    /**
     * Opens a connection to the specified url.
     *
     * @param context
     * @param url     The url to connect to.
     * @param data    The data to POST to the url. If null, a GET request will be sent.
     * @return The created {@link HttpURLConnection}.
     * @throws IOException
     */
    public static HttpURLConnection openUrl(final Context context, URL url, HashMap<String,
            String> data) throws IOException {
        Logger.log(HttpUtil.class, "Connecting to ", url);
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        Network[] allNetworks = cm.getAllNetworks();
        Network activeNetwork = null;
        for (Network network : allNetworks) {
            NetworkInfo networkInfo = cm.getNetworkInfo(network);
            Logger.log(HttpUtil.class, "Network info: ", networkInfo);
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI && networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                activeNetwork = network;
            }
        }

        Logger.log(HttpUtil.class, "Active network info: ", cm.getActiveNetworkInfo());

        HttpURLConnection connection = (HttpURLConnection) activeNetwork.openConnection(url);

        connection.setConnectTimeout(500);
        connection.setReadTimeout(500);

        if (connection instanceof HttpsURLConnection) {
            Logger.log(HttpUtil.class, "HTTPS connection, using custom ssl socket factory and hostname verifier");
            // Special handling for https connections is needed because of the self-signed certificate
            ((HttpsURLConnection) connection).setSSLSocketFactory(getSSLSocketFactory(context));
            ((HttpsURLConnection) connection).setHostnameVerifier(new IpHostnameVerifier());
        }

        if (data == null) {
            Logger.log(HttpUtil.class, "Using GET request");
            connection.setRequestMethod("GET");
        } else {
            Logger.log(HttpUtil.class, "Using POST request");
            // data is delivered as a POST request
            StringBuilder content = new StringBuilder();
            for (String key : data.keySet()) {
                content.append(key);
                content.append('=');
                content.append(data.get(key));
                content.append('&');
            }
            String postData = content.toString();
            Logger.log(HttpUtil.class, "POST data: ", postData);

            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Content-Length", String.valueOf(postData.length()));

            OutputStream writer = connection.getOutputStream();
            writer.write(postData.getBytes());
            writer.flush();
            writer.close();
        }

        Logger.log(HttpUtil.class, "Response: ", connection.getResponseCode(), connection.getResponseMessage());
        return connection;
    }

    /**
     * Initialises the SSL handler required to accept the bad SSL certificate for BA Leipzig.
     *
     * @param context
     * @return The {@link SSLSocketFactory} capable of accepting the SSL certificate.
     */
    public static SSLSocketFactory getSSLSocketFactory(Context context) {
        try {
            final KeyStore ks = KeyStore.getInstance(context.getString(R.string.keystore_type));

            // the bks file we generated above
            final InputStream in = context.getResources().openRawResource(R.raw.keystore);
            try {
                // don't forget to put the password used above in strings.xml/mystore_password
                Logger.log(HttpUtil.class, "Loading custom certificate");
                ks.load(in, context.getString(R.string.keystore_password).toCharArray());
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

