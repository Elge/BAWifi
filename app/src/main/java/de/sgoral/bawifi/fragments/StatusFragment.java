package de.sgoral.bawifi.fragments;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.sgoral.bawifi.R;
import de.sgoral.bawifi.appstate.ApplicationState;
import de.sgoral.bawifi.appstate.ApplicationStateListener;
import de.sgoral.bawifi.appstate.ApplicationStateManager;

/**
 * Created by sebastianprivat on 06.02.17.
 */

public class StatusFragment extends Fragment {

    private ApplicationStateListener listener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        listener = new ApplicationStateListener() {

            @Override
            public void onApplicationStatusChanged(ApplicationState newStatus, ApplicationState prevStatus) {
                swapFragmentUsingAppStatus();
            }
        };
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_status, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        swapFragmentUsingAppStatus();
        ApplicationStateManager.addListener(listener);
    }

    @Override
    public void onStop() {
        super.onStop();

        ApplicationStateManager.removeListener(listener);
    }

    private void swapFragmentUsingAppStatus() {
        ApplicationState appStatus = ApplicationStateManager.getApplicationStatus();
        Fragment fragment = getStatusFragment(appStatus);
        swapFragment(fragment);
    }

    private void swapFragment(Fragment fragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.status_fragment_container, fragment);
        transaction.commitAllowingStateLoss();
    }

    private Fragment getStatusFragment(ApplicationState applicationStatus) {
        switch (applicationStatus) {
            case STATUS_DISCONNECTED:
                return new StatusDisconnectedFragment();
            case STATUS_CONNECTED:
                return new StatusConnectedFragment();
            case STATUS_AUTHENTICATING:
                return new StatusAuthenticatingFragment();
            case STATUS_AUTHENTICATED:
                return new StatusAuthenticatedFragment();
            case STATUS_DEAUTHENTICATING:
                return new StatusDeAuthenticatingFragment();
            default:
                throw new RuntimeException("Unexpected application status: " + applicationStatus);
        }
    }
}
