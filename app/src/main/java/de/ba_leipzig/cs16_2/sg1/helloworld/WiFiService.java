package de.ba_leipzig.cs16_2.sg1.helloworld;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by cs16sg1 on 27.01.17.
 */

public class WiFiService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        WifiReceiver listener = new WifiReceiver();
        this.getApplicationContext().registerReceiver(listener, null);

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
