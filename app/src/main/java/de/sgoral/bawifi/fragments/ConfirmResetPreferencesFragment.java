package de.sgoral.bawifi.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

import de.sgoral.bawifi.R;
import de.sgoral.bawifi.util.PreferencesUtil;

/**
 * Created by sebastianprivat on 21.02.17.
 */

public class ConfirmResetPreferencesFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.message_confirm_reset_preferences);
        builder.setPositiveButton(R.string.button_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                PreferencesUtil.getInstance(getActivity()).resetPreferences();
                confirmReset();
            }
        });

        builder.setNegativeButton(R.string.button_cancel, null);

        return builder.create();
    }

    private void confirmReset() {
        Toast.makeText(getActivity(), R.string.toast_preferences_reset, Toast.LENGTH_SHORT).show();
    }
}
