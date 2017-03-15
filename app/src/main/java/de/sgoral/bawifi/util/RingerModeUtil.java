package de.sgoral.bawifi.util;

import android.app.NotificationManager;
import android.content.Context;
import android.media.AudioManager;
import android.os.Build;

import de.sgoral.bawifi.R;
import de.sgoral.bawifi.util.userlog.UserlogUtil;

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
     *
     * @return true if the there were no problems, false if we lack the required permission
     */
    public boolean changeRingerMode(RingerModeSetting mode) {
        UserlogUtil.log(context, "Changing ringer mode to " + mode);
        switch (mode) {
            case LOUD:
                return setRingerMode(AudioManager.RINGER_MODE_NORMAL);
            case VIBRATE:
                return setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
            case MUTE:
                return setRingerMode(AudioManager.RINGER_MODE_SILENT);
        }

        return true;
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

    /**
     * Enum representation of the volume control setting.
     */
    public enum RingerModeSetting {
        OFF,
        LOUD,
        VIBRATE,
        MUTE;

        public static RingerModeSetting valueOf(Context context, String setting) {
            if (context.getString(R.string.list_item_key_volume_control_off).equals(setting)) {
                return OFF;
            }
            if (context.getString(R.string.list_item_key_volume_control_on).equals(setting)) {
                return LOUD;
            }
            if (context.getString(R.string.list_item_key_volume_control_vibrate).equals(setting)) {
                return VIBRATE;
            }
            if (context.getString(R.string.list_item_key_volume_control_mute).equals(setting)) {
                return MUTE;
            }
            throw new RuntimeException("Unexpected volume control setting: " + setting);
        }
    }
}
