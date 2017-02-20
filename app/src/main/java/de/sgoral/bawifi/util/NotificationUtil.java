package de.sgoral.bawifi.util;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import de.sgoral.bawifi.R;
import de.sgoral.bawifi.activities.MainMenuActivity;
import de.sgoral.bawifi.activities.PreferencesActivity;

/**
 * Allows creation of notifications.
 */
public class NotificationUtil {

    private static final int NOTIFICATION_ID_MISSINGPREFERENCES = 0;
    private static final int NOTIFICATION_ID_APPLICATIONSTATUS = 1;

    /**
     * Static class, no constructor please.
     */
    private NotificationUtil() {
    }

    /**
     * Prepare the notification builder.
     *
     * @param context The application context to build with.
     * @return The prepared builder.
     */
    private static NotificationCompat.Builder prepareNotificationBuilder(Context context) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        return builder;
    }

    /**
     * Prepare the intent to use for the notification.
     *
     * @param context       The application context.
     * @param activityClass The activity to call when clicking the notification.
     * @return The prepared intent.
     */
    private static PendingIntent prepareIntent(Context context, Class<? extends Activity> activityClass) {
        TaskStackBuilder stack = TaskStackBuilder.create(context);
        stack.addParentStack(activityClass);
        Intent resultIntent = new Intent(context, activityClass);
        stack.addNextIntent(resultIntent);
        return stack.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /**
     * Send the notification.
     *
     * @param context           The context to send with.
     * @param notificationId    The ID to identify the notification by.
     * @param builder           The prepared builder.
     * @param notificationIcon  The icon to display the notification with.
     * @param notificationTitle The title to display the notification with.
     * @param notificationText  The text to display the notification with.
     */
    private static void sendNotification(Context context, int notificationId,
                                         NotificationCompat.Builder builder, int notificationIcon, int notificationTitle,
                                         int notificationText) {

        if (notificationTitle != -1) {
            String title = context.getString(notificationTitle);
            if (!title.equals("")) {
                builder.setContentTitle(title);
            }
        }

        if (notificationText != -1) {
            String text = context.getString(notificationText);
            if (!text.equals("")) {
                builder.setContentText(text);
            }
        }

        builder.setSmallIcon(notificationIcon);

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(notificationId, builder.build());
    }

    /**
     * @see NotificationUtil#sendGenericNotification(Context, Class, int, int, int, boolean)
     */
    private static void sendGenericNotification(Context context, Class<? extends Activity> activityClass,
                                                int notificationId, int title) {
        sendGenericNotification(context, activityClass, notificationId, title, -1);
    }

    /**
     * @see NotificationUtil#sendGenericNotification(Context, Class, int, int, int, boolean)
     */
    private static void sendGenericNotification(Context context, Class<? extends Activity> activityClass,
                                                int notificationId, int title, int text) {
        sendGenericNotification(context, activityClass, notificationId, title, text, true);
    }

    /**
     * Creates a notification using a few default settings.
     *
     * @param context        The context to send from.
     * @param activityClass  The activity class to open when clicking the notification.
     * @param notificationId The ID to identify the notification with.
     * @param title          The title to display with the notification.
     * @param text           The text to display with the notification.
     * @param autoCancel     true to automatically delete the notification when clicking on it.
     */
    private static void sendGenericNotification(Context context, Class<? extends Activity> activityClass,
                                                int notificationId, int title, int text, boolean autoCancel) {
        NotificationCompat.Builder builder = prepareNotificationBuilder(context);
        builder.setContentIntent(prepareIntent(context, activityClass));
        builder.setAutoCancel(autoCancel);
        sendNotification(context, notificationId, builder, R.drawable.ic_notifications_black_24dp,
                title, text);
    }

    /**
     * A username and password must be entered.
     *
     * @param context The current application context.
     */
    public static void addMissingPreferencesNotification(Context context) {
        if (PreferencesUtil.getInstance(context).isMissingSettingsNotificationsEnabled()) {
            sendGenericNotification(context, PreferencesActivity.class,
                    NOTIFICATION_ID_MISSINGPREFERENCES,
                    R.string.notification_missingpreferences_title, R.string.notification_missingpreferences_text, false);
        }
    }

    /**
     * The required data has been entered.
     *
     * @param context The current application context.
     */
    public static void removeMissingPreferencesNotification(Context context) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(NOTIFICATION_ID_MISSINGPREFERENCES);
    }

    /**
     * The app has authenticated the device.
     *
     * @param context The current application context.
     */
    public static void addAuthenticationSuccessfulNotification(Context context) {
        if (PreferencesUtil.getInstance(context).isAuthenticatedNotificationsEnabled()) {
            sendGenericNotification(context, MainMenuActivity.class,
                    NOTIFICATION_ID_APPLICATIONSTATUS,
                    R.string.notification_authenticationsuccessful_title);
        }
    }

    /**
     * The app has failed to authenticated the device.
     *
     * @param context The current application context.
     */
    public static void addAuthenticationFailedNotification(Context context) {
        if (PreferencesUtil.getInstance(context).isAuthenticationFailedNotificationsEnabled()) {
            sendGenericNotification(context, MainMenuActivity.class,
                    NOTIFICATION_ID_APPLICATIONSTATUS,
                    R.string.notification_authenticationfailed_title);
        }
    }

    /**
     * The app has deauthenticated the device.
     *
     * @param context The current application context.
     */
    public static void addDeauthenticationSuccessfulNotification(Context context) {
        if (PreferencesUtil.getInstance(context).isDeauthenticationNotificationsEnabled()) {
            sendGenericNotification(context, MainMenuActivity.class,
                    NOTIFICATION_ID_APPLICATIONSTATUS,
                    R.string.notification_deauthenticationsuccessful_title);
        }
    }

    /**
     * The app has failed to deauthenticate the device.
     *
     * @param context The current application context.
     */
    public static void addDeauthenticationFailedNotification(Context context) {
        if (PreferencesUtil.getInstance(context).isDeauthenticationFailedNotificationsEnabled()) {
            sendGenericNotification(context, MainMenuActivity.class,
                    NOTIFICATION_ID_APPLICATIONSTATUS,
                    R.string.notification_deauthenticationfailed_title);
        }
    }

    /**
     * The device has connected to the WiFi network.
     *
     * @param context The current application context.
     */
    public static void addConnectedNotification(Context context) {
        if (PreferencesUtil.getInstance(context).isConnectedNotificationsEnabled()) {
            sendGenericNotification(context, MainMenuActivity.class,
                    NOTIFICATION_ID_APPLICATIONSTATUS,
                    R.string.notification_connected_title);
        }
    }

    /**
     * The device has disconnected from the WiFi network.
     *
     * @param context The current application context.
     */
    public static void addDisconnectedNotification(Context context) {
        if (PreferencesUtil.getInstance(context).isDisconnectedNotificationsEnabled()) {
            sendGenericNotification(context, MainMenuActivity.class,
                    NOTIFICATION_ID_APPLICATIONSTATUS,
                    R.string.notification_disconnected_title);
        }
    }

    /**
     * The user is already authenticated.
     *
     * @param context The current application context.
     */
    public static void addAlreadyAuthenticatedNotification(Context context) {
        if (PreferencesUtil.getInstance(context).isAlreadyAuthenticatedNotificationsEnabled()) {
            sendGenericNotification(context, MainMenuActivity.class,
                    NOTIFICATION_ID_APPLICATIONSTATUS,
                    R.string.notification_already_authenticated_title);
        }
    }
}
