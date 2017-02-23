package de.sgoral.bawifi.util;

import android.content.Context;
import android.media.AudioManager;

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
     * Loads the AudioManager system service and changes the ringer mode.
     *
     * @param mode One of {@link AudioManager#RINGER_MODE_NORMAL}
     *             {@link AudioManager#RINGER_MODE_VIBRATE} {@link AudioManager#RINGER_MODE_SILENT}
     */
    private void setRingerMode(int mode) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        audioManager.setRingerMode(mode);
    }

}
