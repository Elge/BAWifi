package de.sgoral.bawifi.asynctasks;

import android.content.Context;
import android.os.AsyncTask;

import de.sgoral.bawifi.appstate.ApplicationState;
import de.sgoral.bawifi.appstate.ApplicationStateManager;
import de.sgoral.bawifi.util.Logger;

/**
 * Created by sebastianprivat on 14.08.17.
 */

public class DelayedDisconnectTask extends AsyncTask<Integer, Void, Void> {

    private Context context;

    public DelayedDisconnectTask(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(Integer... params) {
        for (Integer param : params) {
            Logger.log(context, this, "Starting ", param, " seconds sleep");
            long startTime = System.currentTimeMillis();
            try {
                Thread.sleep(param * 1000);
                if (!isCancelled()) {
                    Logger.log(context, this, "Completed sleep, triggering disconnect");
                    ApplicationStateManager.changeApplicationState(context, ApplicationState.STATE_DISCONNECTED);
                    continue;
                }
            } catch (InterruptedException e) {
            }
            Logger.log(context, this, "Sleep interrupted after ", (System.currentTimeMillis() - startTime) / 1000, " seconds");
        }

        return null;
    }
}
