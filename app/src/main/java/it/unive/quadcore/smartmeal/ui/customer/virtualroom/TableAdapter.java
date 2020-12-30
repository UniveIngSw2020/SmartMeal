package it.unive.quadcore.smartmeal.ui.customer.virtualroom;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Consumer;
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
import it.unive.quadcore.smartmeal.communication.confirmation.Confirmation;
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

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Table item clicked: " + table.getId());

                CustomerCommunication customerCommunication = CustomerCommunication.getInstance();

                // TODO pensare alla cosa migliore da fare
                if (!customerCommunication.isConnected()) {
                    return;
                }

                customerCommunication.selectTable(table, new Consumer<Confirmation<? extends TableException>>() {
                    @Override
                    public void accept(Confirmation<? extends TableException> confirmation) {
                        try {
                            confirmation.obtain();

                            // TODO probabilmente si può fare meglio
                            CustomerVirtualRoomFragment customerVirtualRoomFragment = new CustomerVirtualRoomFragment();

                            FragmentManager fragmentManager = ((AppCompatActivity) activity).getSupportFragmentManager();
                            fragmentManager
                                    .beginTransaction()
                                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                                    .replace(R.id.customer_room_fragment_container, customerVirtualRoomFragment)
                                    .commit();
                        } catch (TableException e) {

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
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return tableList.size();
    }
}
