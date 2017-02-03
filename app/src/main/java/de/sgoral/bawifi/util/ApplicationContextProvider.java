package de.sgoral.bawifi.util;

import android.app.Application;
import android.content.Context;

/**
 * Created by sebastianprivat on 02.02.17.
 */

public class ApplicationContextProvider extends Application {

    private static Context context;

    public static Context getAppContext() {
        return context;
    }

    public void onCreate() {
        super.onCreate();
        ApplicationContextProvider.context = super.getApplicationContext();
    }

}
