package de.sgoral.bawifi;

import android.content.Context;

/**
 * Enum representation of the slow disconnect detection setting.
 */
public enum SlowDisconnectDetection {

    OFF(0),
    TEN_SECONDS(10),
    THIRTY_SECONDS(30),
    ONE_MINUTE(60),
    FIVE_MINUTES(300);

    private final int value;

    SlowDisconnectDetection(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
