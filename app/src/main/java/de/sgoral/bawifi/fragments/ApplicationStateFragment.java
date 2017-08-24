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
import de.sgoral.bawifi.util.Logger;

public class ApplicationStateFragment extends Fragment {

    private ApplicationStateListener listener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        listener = new ApplicationStateListener() {

            @Override
            public void onApplicationStateChanged(ApplicationState newState, ApplicationState prevState) {
                swapFragmentUsingApplicationState();
            }
        };

        Logger.log(this.getActivity(), this, "Fragment created");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Logger.log(this.getActivity(), this, "View created");
        return inflater.inflate(R.layout.fragment_application_state, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();

        swapFragmentUsingApplicationState();
        ApplicationStateManager.addListener(listener);
        Logger.log(this.getActivity(), this, "Fragment resumed");
    }

    @Override
    public void onPause() {
        super.onPause();

        ApplicationStateManager.removeListener(listener);
        Logger.log(this.getActivity(), this, "Fragment paused");
    }

    private void swapFragmentUsingApplicationState() {
        ApplicationState appState = ApplicationStateManager.getApplicationState();
        Fragment fragment = getFragmentByApplicationState(appState);
        swapFragment(fragment);
    }

    private void swapFragment(Fragment fragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.application_state_fragment_container, fragment);
        transaction.commit();
    }

    private Fragment getFragmentByApplicationState(ApplicationState applicationState) {
        switch (applicationState) {
            case STATE_DISCONNECTED:
                return new ApplicationStateDisconnectedFragment();
            case STATE_CONNECTED:
                return new ApplicationStateConnectedFragment();
            case STATE_AUTHENTICATING:
                return new ApplicationStateAuthenticatingFragment();
            case STATE_AUTHENTICATED:
                return new ApplicationStateAuthenticatedFragment();
            case STATE_DEAUTHENTICATING:
                return new ApplicationStateDeAuthenticatingFragment();
            default:
                throw new RuntimeException("Unexpected application state: " + applicationState);
        }
    }
}
