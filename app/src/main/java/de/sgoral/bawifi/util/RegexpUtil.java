package de.sgoral.bawifi.util;

import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Utility for regular expressions. Provide patterns to extract information from HTML code.
 */
public class RegexpUtil {

    /**
     * Regexp for extracting the logout url from xml code.
     */
    public static final Pattern LOGOUT_URL =
            Pattern.compile("<LogoutUrl>([^\"]+)</LogoutUrl>", Pattern.CASE_INSENSITIVE);
    /**
     * Shortcut for [^>]*, i.e. match any number of characters except the closing angle bracket.
     */
    private static final String REGEXP_NOGT = "[^>]*";
    /**
     * Shortcut for [^"]*, i.e. match any number of characters except for quotation marks.
     */
    private static final String REGEXP_NOQUOT = "[^\"]*";
    /**
     * Regexp for extracting redirect url from a http meta refresh tag.
     */
    public static final Pattern META_REDIRECT = generateHtmlElementPattern("meta",
            "http-equiv", "refresh", "content", "\\d+;\\s?url=(" + REGEXP_NOQUOT + ")");
    /**
     * Regexp for extracting the POST url from a html form tag.
     */
    public static final Pattern FORM_ACTION = generateHtmlElementPattern("form",
            "name", "form1", "method", "post", "action", '(' + REGEXP_NOQUOT + ')');
    /**
     * Regexp for extracting the challenge value from a formular.
     */
    public static final Pattern CHALLENGE_VALUE = generateInputElementPattern("hidden", "challenge");
    /**
     * Regexp for extracting the UAM IP value from a formular.
     */
    public static final Pattern UAMIP_VALUE = generateInputElementPattern("hidden", "uamip");
    /**
     * Regexp for extracting the UAM Port value from a formular.
     */
    public static final Pattern UAMPORT_VALUE = generateInputElementPattern("hidden", "uamport");
    /**
     * Regexp for extracting the submit button value from a formular.
     */
    public static final Pattern SUBMIT_VALUE = generateInputElementPattern("submit", "button");

    /**
     * Hidden because static utility class.
     */
    private RegexpUtil() {

    }

    /**
     * Generates a regular expression to match the given html tag with the given attributes
     * using lookaheads.
     *
     * @param tag        The HTML tag to find, without angle brackets.
     * @param attributes The attributes to check for. Expects values in groups of two:
     *                   First element is the attribute name, second is the attribute value.
     * @return The {@link Pattern} for the generated regexp.
     */
    private static Pattern generateHtmlElementPattern(String tag, String... attributes) {
        if (attributes.length % 2 != 0) {
            throw new IllegalArgumentException("Number of attributes must be n times 2");
        }

        StringBuilder builder = new StringBuilder();
        builder.append('<');
        builder.append(tag);
        builder.append(' ');
        for (int i = 1; i < attributes.length; i = i + 2) {
            builder.append(String.format(Locale.US, "(?=%s%s=\"%s\")", REGEXP_NOGT, attributes[i - 1], attributes[i]));
        }
        builder.append(REGEXP_NOGT);
        builder.append('>');
        return Pattern.compile(builder.toString(), Pattern.CASE_INSENSITIVE);
    }

    /**
     * Generates a regular expression to match an html input tag with the given type and name.
     * Groups the value attribute data.
     *
     * @param type The input type to match.
     * @param name The input name to match.
     * @return The {@link Pattern} for the generated regexp.
     */
    private static Pattern generateInputElementPattern(String type, String name) {
        return generateHtmlElementPattern("input",
                "type", type, "name", name, "value", '(' + REGEXP_NOQUOT + ')');
    }
}
