package it.unive.quadcore.smartmeal.ui.customer.virtualroom;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;

import it.unive.quadcore.smartmeal.R;
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
    }

    @Override
    public int getItemCount() {
        return tableList.size();
    }
}
