package it.unive.quadcore.smartmeal.ui.manager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import it.unive.quadcore.smartmeal.R;
import it.unive.quadcore.smartmeal.local.Local;
import it.unive.quadcore.smartmeal.local.RoomStateException;

public class ConfirmDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        String confirmLabel = getString(R.string.confirm_label_alert);
        String closeLabel = getString(R.string.close_label_alert);
        String message = getString(R.string.confirm_text_alert);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(message)
                .setPositiveButton(confirmLabel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try {
                            Local.getInstance().closeRoom();
                        } catch (RoomStateException e) {
                            e.printStackTrace();
                        }
                        Intent intent = new Intent(getActivity(), ManagerHomeActivity.class);
                        // svuota il backstack
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                })
        .setNegativeButton(closeLabel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        return builder.create();
    }
}

