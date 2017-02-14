package de.sgoral.bawifi.util.ssl;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

/**
 * Checks the hostname directly against the peer hostname.
 * The default HostnameVerifier only checks the alternative names when the hostname is an IP.
 */
public class IpHostnameVerifier implements HostnameVerifier {

    @Override
    public boolean verify(String hostname, SSLSession session) {
        return hostname.equalsIgnoreCase(session.getPeerHost());
    }
}
