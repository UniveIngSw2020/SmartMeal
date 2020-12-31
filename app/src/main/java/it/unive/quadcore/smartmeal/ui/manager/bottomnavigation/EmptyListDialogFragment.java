package it.unive.quadcore.smartmeal.ui.manager.bottomnavigation;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import it.unive.quadcore.smartmeal.R;

public class EmptyListDialogFragment extends DialogFragment {

    String label ;

    public EmptyListDialogFragment(String label){
        super();
        this.label=label;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        String closeLabel = getActivity().getString(R.string.close_label_alert);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(label)
                /*.setPositiveButton("", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //
                    }
                })*/
                .setNegativeButton(closeLabel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // EMPTY
                    }
                });

        return builder.create();
    }
}
