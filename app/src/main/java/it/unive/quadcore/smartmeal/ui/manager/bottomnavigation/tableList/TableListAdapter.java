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
import it.unive.quadcore.smartmeal.communication.CustomerHandler;
import it.unive.quadcore.smartmeal.local.Local;
import it.unive.quadcore.smartmeal.local.RoomStateException;
import it.unive.quadcore.smartmeal.local.TableException;
import it.unive.quadcore.smartmeal.model.ManagerTable;
import it.unive.quadcore.smartmeal.ui.manager.ManagerRoomBottomNavigationActivity;
import it.unive.quadcore.smartmeal.ui.manager.addTable.AddedTableDialogFragment;

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
            CustomerHandler.Customer customer = Local.getInstance().getCustomerByTable(table);
            holder.customerTextView.setText(customer.getName());

            holder.modifyButton.setOnClickListener(view->{
                /*try {
                    new ModifyTableDialogFragment(customer,Local.getInstance().getFreeTableList(), this)
                            .show(((FragmentActivity)view.getContext()).getSupportFragmentManager(),"modifyTable");
                } catch (RoomStateException e) {  // TODO : ECCEZIONI
                    e.printStackTrace();
                } catch (TableException e) {
                    e.printStackTrace();
                }*/
                new ModifyTableDialogFragment(customer,Local.getInstance().getFreeTableList(), this)
                        .show(((FragmentActivity)view.getContext()).getSupportFragmentManager(),"modifyTable");
            });
        } catch (TableException e) { // TODO : gestire eccezioni
            e.printStackTrace();
        }


        holder.deleteButton.setOnClickListener(view->{
            try {
                Local.getInstance().freeTable(table);

                int tableToRemoveIndex = tableList.indexOf(table);
                tableList.remove(tableToRemoveIndex);
                notifyItemRemoved(tableToRemoveIndex);
            } catch (TableException e) {
                e.printStackTrace();
            }

        });
    }

    @Override
    public int getItemCount() {
        return tableList.size();
    }


    public void changeTable(ManagerTable oldTable, ManagerTable newTable){
        tableList.remove(oldTable);

        tableList.add(newTable);

        notifyDataSetChanged();
    }

    public void reload(){
        /*try {
            tableList.clear();
            tableList.addAll(Local.getInstance().getAssignedTableList());
            notifyDataSetChanged();
        } catch (RoomStateException e) {
            e.printStackTrace();
        } catch (TableException e) {
            e.printStackTrace();
        }*/
        tableList.clear();
        tableList.addAll(Local.getInstance().getAssignedTableList());
        notifyDataSetChanged();

    }
}
