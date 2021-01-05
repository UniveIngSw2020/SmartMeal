package it.unive.quadcore.smartmeal.ui.manager.bottomnavigation.tableList;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import it.unive.quadcore.smartmeal.R;
import it.unive.quadcore.smartmeal.local.Local;
import it.unive.quadcore.smartmeal.local.TableException;
import it.unive.quadcore.smartmeal.model.Customer;
import it.unive.quadcore.smartmeal.model.ManagerTable;

// Lista tavoli occupati, visibile dalla schermata di gestione tavoli
public class TableListAdapter extends RecyclerView.Adapter<TableListAdapter.TableViewHolder>{
    public static final class TableViewHolder extends RecyclerView.ViewHolder {
        private TextView tableTextView;
        private TextView customerTextView;
        private Button modifyButton;
        private Button deleteButton;

        public TableViewHolder(@NonNull View itemView) {
            super(itemView);

            this.tableTextView = itemView.findViewById(R.id.table_text_view);
            this.customerTextView = itemView.findViewById(R.id.customer_table_text_view);
            this.modifyButton = itemView.findViewById(R.id.modify_table_button);
            this.deleteButton = itemView.findViewById(R.id.delete_table_button);

        }
    }

    private final Activity activity;
    private final List<ManagerTable> tableList;

    public TableListAdapter(Activity activity, Set<ManagerTable> tableSet) {
        this.activity = activity;
        this.tableList = new ArrayList<>(tableSet);
    }

    @NonNull
    @Override
    public TableListAdapter.TableViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.table_row, parent, false);
        return new TableListAdapter.TableViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TableListAdapter.TableViewHolder holder, int position) {
        ManagerTable table =  tableList.get(position);

        String prefix = activity.getString(R.string.table_prefix);
        holder.tableTextView.setText(String.format("%s %s", prefix, table.getId()));

        try { // Mostro la riga di questo tavolo
            Customer customer = Local.getInstance().getCustomerByTable(table);
            holder.customerTextView.setText(customer.getName());

            holder.modifyButton.setOnClickListener(view->{ // Voglio modificare un tavolo
                /*try {
                    new ModifyTableDialogFragment(customer,Local.getInstance().getFreeTableList(), this)
                            .show(((FragmentActivity)view.getContext()).getSupportFragmentManager(),"modifyTable");
                } catch (RoomStateException e) {
                    e.printStackTrace();
                } catch (TableException e) {
                    e.printStackTrace();
                }*/

                Set<ManagerTable> freeTables = Local.getInstance().getFreeTableList();

                if(freeTables.size()==0) { // Lista tavoli liberi è vuota : non si può modificare il tavolo
                    Snackbar.make(
                            activity.findViewById(android.R.id.content),
                            R.string.error_modify_table_snackbar,
                            BaseTransientBottomBar.LENGTH_LONG
                    ).show();

                }
                else { // Vado al dialog di modifica del tavolo
                    new ModifyTableDialogFragment(customer, freeTables, this)
                            .show(((FragmentActivity) view.getContext()).getSupportFragmentManager(), "modifyTable");
                }
            });

            // Setto la riga visibile
            holder.itemView.setVisibility(View.VISIBLE);
            holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        } catch (TableException e) { // Errore : non esiste cliente con questo tavolo. La riga di questo tavolo non viene mostrata
            holder.itemView.setVisibility(View.GONE);
            holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
        }


        holder.deleteButton.setOnClickListener(view->{ // Voglio eliminare il tavolo
            try {
                Local.getInstance().freeTable(table);

                int tableToRemoveIndex = tableList.indexOf(table);
                tableList.remove(tableToRemoveIndex);
                notifyItemRemoved(tableToRemoveIndex);
            } catch (TableException e) { // Errore nell'eliminare il tavolo
                Snackbar.make(
                        activity.findViewById(android.R.id.content),
                        R.string.error_delete_table_snackbar,
                        BaseTransientBottomBar.LENGTH_LONG
                ).show();
            }

        });
    }

    @Override
    public int getItemCount() {
        return tableList.size();
    }

    // Ricarica la lista tavoli
    public void reload(){
        tableList.clear();
        tableList.addAll(Local.getInstance().getAssignedTableList());
        notifyDataSetChanged();

    }
}
