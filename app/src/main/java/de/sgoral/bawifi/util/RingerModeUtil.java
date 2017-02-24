package de.sgoral.bawifi.util;

import android.app.NotificationManager;
import android.content.Context;
import android.media.AudioManager;
import android.os.Build;

/**
 * Allows easy manipulation of the device's ringer mode.
 */
public class RingerModeUtil {

    private final Context context;

    private RingerModeUtil(Context context) {
        this.context = context;
    }

    public static RingerModeUtil getInstance(Context context) {
        return new RingerModeUtil(context);
    }

    /**
     * Changes the ringer mode to the specified mode.
     */
    public void changeRingerMode(VolumeControlSetting mode) {
        switch (mode) {
            case LOUD:
                setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                break;
            case VIBRATE:
                setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                break;
            case MUTE:
                setRingerMode(AudioManager.RINGER_MODE_SILENT);
                break;
        }
    }

    /**
     * Checks if the application is allowed to change the ringer mode.
     *
     * @return true if we can change the ringer mode, false otherwise.
     */
    public boolean canChangeRingerMode() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            return true;
        }

        NotificationManager manager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        return manager.isNotificationPolicyAccessGranted();
    }

    /**
     * Loads the AudioManager system service and changes the ringer mode.
     *
     * @param mode One of {@link AudioManager#RINGER_MODE_NORMAL}
     *             {@link AudioManager#RINGER_MODE_VIBRATE} {@link AudioManager#RINGER_MODE_SILENT}
     * @return true if the change was successful, false if we lack the permissions
     */
    private boolean setRingerMode(int mode) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        try {
            audioManager.setRingerMode(mode);
            return true;
        } catch (SecurityException e) {
            return false;
        }
    }

}
