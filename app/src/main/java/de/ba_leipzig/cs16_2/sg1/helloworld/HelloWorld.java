package de.ba_leipzig.cs16_2.sg1.helloworld;

import android.app.Application;
import android.content.Context;

/**
 * Created by sebastianprivat on 02.02.17.
 */

public class HelloWorld extends Application {

    private static Context context;

    public void onCreate() {
        super.onCreate();
        HelloWorld.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return context;
    }

}
