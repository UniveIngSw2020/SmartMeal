package it.unive.quadcore.smartmeal.ui.manager.addTable;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import it.unive.quadcore.smartmeal.R;

public class AddedTableDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Activity activity = getActivity();
        String closeLabel = activity.getString(R.string.close_label_alert);

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(R.string.added_table_alert)
                /*.setPositiveButton("", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //
                    }
                })*/
                .setNegativeButton(closeLabel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(activity, AddTableActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        activity.startActivity(intent);
                    }
                });

        return builder.create();
    }

}