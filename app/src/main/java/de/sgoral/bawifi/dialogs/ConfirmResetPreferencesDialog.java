package de.sgoral.bawifi.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

import de.sgoral.bawifi.R;
import de.sgoral.bawifi.fragments.PreferencesFragment;
import de.sgoral.bawifi.util.Logger;
import de.sgoral.bawifi.util.PreferencesUtil;

public class ConfirmResetPreferencesDialog extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.dialog_confirm_reset_preferences);
        builder.setPositiveButton(R.string.button_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                PreferencesUtil.getInstance(getActivity()).resetPreferences();
                confirmReset();
                PreferencesFragment fragment = (PreferencesFragment) getFragmentManager().findFragmentById(R.id.fragment_preferences);
                if (fragment != null) {
                    fragment.redrawScreen();
                }
            }
        });

        builder.setNegativeButton(R.string.button_cancel, null);

        Logger.log(this.getActivity(), this, "Dialog created");
        return builder.create();
    }

    private void confirmReset() {
        Toast.makeText(getActivity(), R.string.toast_preferences_reset, Toast.LENGTH_SHORT).show();
        Logger.log(this.getActivity(), this, "Reset confirmed");
    }
}
