package it.unive.quadcore.smartmeal.ui.manager.addTable;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import it.unive.quadcore.smartmeal.R;

// Dialog di messaggio dopo l'aggiunta tavolo : o conferma di successo o notifica di insuccesso
public class AddedingTableDialogFragment extends DialogFragment {

    private String message ;

    public AddedingTableDialogFragment(String message){
        this.message = message;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Activity activity = getActivity();
        String closeLabel = activity.getString(R.string.close_label_alert);

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(message)
                .setNegativeButton(closeLabel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Ritorno all'activity aggiunta tavolo, togliendo però l'ultima schermata (se torno indietro torno alla
                        // lista tavoli occupati nella stanza virtuale)
                        Intent intent = new Intent(activity, AddTableActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        activity.startActivity(intent);
                    }
                });

        return builder.create();
    }

}