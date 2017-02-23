package de.sgoral.bawifi.util;

import android.content.Context;

import de.sgoral.bawifi.R;

/**
 * Enum representation of the volume control setting.
 */
public enum VolumeControlSetting {
    OFF,
    LOUD,
    VIBRATE,
    MUTE;

    public static VolumeControlSetting valueOf(Context context, String setting) {
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
