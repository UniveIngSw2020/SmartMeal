package it.unive.quadcore.smartmeal.ui.manager.addTable;

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
import it.unive.quadcore.smartmeal.model.ManagerTable;

// Lista di tavoli liberi, visibile dalla schermata di aggiunta tavolo
public class AddTableAdapter extends RecyclerView.Adapter<AddTableAdapter.TableViewHolder>{
    public static final class TableViewHolder extends RecyclerView.ViewHolder {
        private TextView tableTextView;
        private Button selectButton;

        public TableViewHolder(@NonNull View itemView) {
            super(itemView);

            this.tableTextView = itemView.findViewById(R.id.free_table_text_view);
            this.selectButton = itemView.findViewById(R.id.select_free_table_button);

        }
    }

    private final Activity activity;
    private final List<ManagerTable> tableList;

    public AddTableAdapter(Activity activity, Set<ManagerTable> freeTables) {
        this.activity = activity;
        this.tableList = new ArrayList<>(freeTables);
    }

    @NonNull
    @Override
    public AddTableAdapter.TableViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.select_free_table_row, parent, false);
        return new AddTableAdapter.TableViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddTableAdapter.TableViewHolder holder, int position) {
        ManagerTable table =  tableList.get(position);

        String prefix = activity.getString(R.string.table_prefix);
        holder.tableTextView.setText(String.format("%s %s", prefix, table.getId()));


        holder.selectButton.setOnClickListener(view->{ // Voglio aggiungere questo tavolo

            TextView customerTextView = activity.findViewById(R.id.insert_new_costumer_edit_text);
            String customerName = customerTextView.getText().toString().trim();

            if (customerName.isEmpty()) { // Non è stato inserito il nome cliente
                Snackbar.make(
                        activity.findViewById(android.R.id.content),
                        R.string.no_new_customer_name_snackbar,
                        BaseTransientBottomBar.LENGTH_LONG
                ).show();
                return;
            }

            try { // Provo ad aggiungere il tavolo
                Local.getInstance().assignTable(customerName,table);
                String message = activity.getString(R.string.added_table_alert);
                new AddedingTableDialogFragment(message)
                        .show(((FragmentActivity)view.getContext()).getSupportFragmentManager(),"addedTable");
            } catch (TableException e) { // Errore nell'aggiungere questo tavolo
                String message = activity.getString(R.string.error_add_table_alert);
                new AddedingTableDialogFragment(message)
                        .show(((FragmentActivity)view.getContext()).getSupportFragmentManager(),"addedTable");
            }
        });
    }

    @Override
    public int getItemCount() {
        return tableList.size();
    }
}
