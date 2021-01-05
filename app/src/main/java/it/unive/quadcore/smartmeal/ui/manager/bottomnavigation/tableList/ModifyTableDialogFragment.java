package it.unive.quadcore.smartmeal.ui.manager.bottomnavigation.tableList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import it.unive.quadcore.smartmeal.R;
import it.unive.quadcore.smartmeal.local.Local;
import it.unive.quadcore.smartmeal.local.TableException;
import it.unive.quadcore.smartmeal.model.Customer;
import it.unive.quadcore.smartmeal.model.ManagerTable;
import it.unive.quadcore.smartmeal.ui.manager.ErrorDialogFragment;


// Dialog per la modifica di tavoli
public class ModifyTableDialogFragment extends DialogFragment {
    private final Customer customer;
    private final TableListAdapter adapter;
    private final List<ManagerTable> freeTables;


   public ModifyTableDialogFragment(Customer customer, Set<ManagerTable> freeTables, TableListAdapter adapter){
       super();
       this.customer = customer;
       this.adapter = adapter;
       this.freeTables = new ArrayList<>(freeTables);

   }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        String confirmLabel = getActivity().getString(R.string.confirm_label_alert);

        String title = getActivity().getString(R.string.modify_table_alert);

        // Lista di tavoli liberi. Lista di stringhe
        List<String> freeTablesStrings = new ArrayList<>();
        String prefix = getActivity().getString(R.string.table_prefix);
        for(ManagerTable table : freeTables){
            freeTablesStrings.add(String.format("%s %s",prefix,table.getId()));
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(String.format("%s %s", title , customer.getName()))
                // Setto la lista di tavoli liberi
                .setAdapter(new ArrayAdapter<>(getContext(),R.layout.table_dialog_row , freeTablesStrings),new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) { // Callback di selezione di un tavolo
                        try {

                           // throw new RoomStateException(true); // TEsting

                            ManagerTable newTable = freeTables.get(which);
                            Local.getInstance().changeCustomerTable(customer, newTable); // Modifico il tavolo associato al cliente

                            adapter.reload();

                        } catch (TableException e) { // Errore nel modificare il tavolo
                            new ErrorDialogFragment(getActivity().getString(R.string.modify_table_error_alert))
                                    .show(((FragmentActivity)getContext()).getSupportFragmentManager(),"errorSelectedTable");
                        }
                    }
                });

        return builder.create();
    }

}
