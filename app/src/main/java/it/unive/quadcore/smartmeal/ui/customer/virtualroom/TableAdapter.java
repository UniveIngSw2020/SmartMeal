package it.unive.quadcore.smartmeal.ui.customer.virtualroom;

import android.app.Activity;
import android.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;

import it.unive.quadcore.smartmeal.R;
import it.unive.quadcore.smartmeal.communication.CustomerCommunication;
import it.unive.quadcore.smartmeal.local.AlreadyAssignedTableException;
import it.unive.quadcore.smartmeal.local.AlreadyOccupiedTableException;
import it.unive.quadcore.smartmeal.local.NoSuchTableException;
import it.unive.quadcore.smartmeal.local.TableException;
import it.unive.quadcore.smartmeal.model.Table;

public class TableAdapter extends RecyclerView.Adapter<TableAdapter.TableViewHolder> {
    private static final String TAG = "TableAdapter";

    public static final class TableViewHolder extends RecyclerView.ViewHolder {
        private TextView tableTextView;

        public TableViewHolder(@NonNull View itemView) {
            super(itemView);

            this.tableTextView = itemView.findViewById(R.id.table_text_view);
        }
    }

    private final Activity activity;
    private final List<Table> tableList;

    public TableAdapter(Activity activity, SortedSet<Table> tableSortedSet) {
        this.activity = activity;
        this.tableList = new ArrayList<>(tableSortedSet);
    }

    @NonNull
    @Override
    public TableViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.table_row_item, parent, false);
        return new TableViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TableViewHolder holder, int position) {
        Table table = tableList.get(position);

        String tableString = activity.getString(R.string.table);
        holder.tableTextView.setText(String.format("%s %s", tableString, table.getId()));

        // TODO migliorare formato codice, ci sono troppi blocchi innestati
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Table item clicked: " + table.getId());

                // mostra un Dialog di conferma
                TextView confirmTextView = new TextView(activity);
                String tableConfirmationText = activity.getString(R.string.table_confirmation_text);
                confirmTextView.setText(String.format("%s %s", tableConfirmationText, table.getId()));
                confirmTextView.setPadding(48, 0, 48, 0);

                new AlertDialog.Builder(activity)
                        .setTitle(R.string.select_table)
                        .setView(confirmTextView)
                        .setPositiveButton(
                                R.string.confirmation_button_text,
                                (dialog, which) -> {
                                    Log.i(TAG, "Table selection confirmed: " + table.getId());


                                    startCustomerVirtualRoomFragment(table);    // TODO da rimuovere (solo per testing)


                                    CustomerCommunication customerCommunication = CustomerCommunication.getInstance();

                                    // TODO pensare alla cosa migliore da fare
                                    if (customerCommunication.isNotConnected()) {
                                        new CustomerNearbyTimeoutAction(activity).run();
                                        return;
                                    }

                                    customerCommunication.selectTable(table,
                                            confirmation -> {
                                                try {
                                                    confirmation.obtain();
                                                    // mostra la pagina della virtual room
                                                    startCustomerVirtualRoomFragment(table);
                                                } catch (TableException e) {
                                                    // gestione eccezioni
                                                    if (e instanceof AlreadyOccupiedTableException) {
                                                        // 1. Tavolo già occupato
                                                        // avvisare cliente, aggiornare lista tavoli e far scegliere un altro tavolo
                                                        Log.i(TAG, "AlreadyOccupiedTableException happened: " + e.getMessage());
                                                        Snackbar.make(
                                                                v.findViewById(android.R.id.content),
                                                                R.string.already_occupied_table_snackbar,
                                                                BaseTransientBottomBar.LENGTH_LONG
                                                        ).show();
                                                    } else if (e instanceof NoSuchTableException) {
                                                        // 2. Tavolo non esiste
                                                        // avvisare il cliente con un errore generico e farlo ritentare
                                                        Log.i(TAG, "NoSuchTableException happened: " + e.getMessage());
                                                        Snackbar.make(
                                                                v.findViewById(android.R.id.content),
                                                                R.string.unexpected_table_error_snackbar,
                                                                BaseTransientBottomBar.LENGTH_LONG
                                                        ).show();
                                                    } else if (e instanceof AlreadyAssignedTableException) {
                                                        // TODO: needs to have priority in Local over AlreadyOccupiedException

                                                        // 3. Il cliente ha già il tavolo che ha richiesto
                                                        // 4. Il cliente ha già un tavolo (diverso da quello richiesto)

                                                        Log.i(TAG, "AlreadyAssignedTableException happened: " + e.getMessage());
                                                    } else {
                                                        // errore inaspettato
                                                        Log.i(TAG, "TableException happened: " + e.getMessage());
                                                        Snackbar.make(
                                                                v.findViewById(android.R.id.content),
                                                                R.string.unexpected_table_error_snackbar,
                                                                BaseTransientBottomBar.LENGTH_LONG
                                                        ).show();
                                                    }
                                                }
                                            },
                                            new CustomerNearbyTimeoutAction(activity)
                                    );
                                }
                        )
                        .setNegativeButton(
                                R.string.cancellation_button_text,
                                (dialog, which) -> dialog.cancel()
                        )
                        .show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return tableList.size();
    }

    private void startCustomerVirtualRoomFragment(Table table) {
        // TODO probabilmente si può fare meglio (ma va bene anche così)
        CustomerVirtualRoomFragment customerVirtualRoomFragment =
                CustomerVirtualRoomFragment.newInstance(table.getId());

        FragmentManager fragmentManager =
                ((AppCompatActivity) activity).getSupportFragmentManager();

        fragmentManager
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .replace(
                        R.id.customer_room_fragment_container,
                        customerVirtualRoomFragment
                )
                .commit();
    }
}
