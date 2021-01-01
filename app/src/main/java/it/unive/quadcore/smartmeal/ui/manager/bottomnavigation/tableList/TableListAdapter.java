package it.unive.quadcore.smartmeal.ui.manager.bottomnavigation.tableList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import it.unive.quadcore.smartmeal.R;
import it.unive.quadcore.smartmeal.local.Local;
import it.unive.quadcore.smartmeal.local.RoomStateException;
import it.unive.quadcore.smartmeal.local.TableException;
import it.unive.quadcore.smartmeal.model.ManagerTable;
import it.unive.quadcore.smartmeal.ui.manager.ManagerRoomBottomNavigationActivity;

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

        try {
            holder.customerTextView.setText(Local.getInstance().getCustomerByTable(table).getName());
        } catch (RoomStateException | TableException e) { // TODO : gestire eccezioni
            e.printStackTrace();
        }

        // TODO : realizzare callback

        holder.modifyButton.setOnClickListener(view->{

        });

        holder.deleteButton.setOnClickListener(view->{
            try {
                Local.getInstance().freeTable(table);

                int tableToRemoveIndex = tableList.indexOf(table);
                tableList.remove(tableToRemoveIndex);
                notifyItemRemoved(tableToRemoveIndex);
            } catch (RoomStateException | TableException e) {
                e.printStackTrace();
            }

        });
    }

    @Override
    public int getItemCount() {
        return tableList.size();
    }
}
