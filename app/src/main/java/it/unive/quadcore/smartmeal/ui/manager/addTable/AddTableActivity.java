package it.unive.quadcore.smartmeal.ui.manager.addTable;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.Set;

import it.unive.quadcore.smartmeal.R;
import it.unive.quadcore.smartmeal.local.Local;
import it.unive.quadcore.smartmeal.local.RoomStateException;
import it.unive.quadcore.smartmeal.local.TableException;
import it.unive.quadcore.smartmeal.model.ManagerTable;
import it.unive.quadcore.smartmeal.ui.manager.bottomnavigation.EmptyListDialogFragment;
import it.unive.quadcore.smartmeal.ui.manager.bottomnavigation.tableList.TableListAdapter;

public class AddTableActivity extends AppCompatActivity {

    private RecyclerView addTableRecyclerView;
    private AddTableAdapter addTableAdapter;
    private TextView customerHint;
    private TextView tableHint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_table);
        customerHint = findViewById(R.id.insert_new_costumer_hint_text_view);
        tableHint = findViewById(R.id.select_table_hint_text_view);
        setupAddTableRecyclerView();

    }

    private void setupAddTableRecyclerView() {
        // TODO aggiungere sezioni a RecyclerView
        addTableRecyclerView = findViewById(R.id.add_table_recycler_view);
        RecyclerView.LayoutManager recyclerViewLayoutManager = new LinearLayoutManager(
                this,
                RecyclerView.VERTICAL,
                false
        );
        addTableRecyclerView.setLayoutManager(recyclerViewLayoutManager);

        /*try {
            addTableAdapter = new AddTableAdapter(this, Local.getInstance().getFreeTableList());
            addTableRecyclerView.setAdapter(addTableAdapter);
        } catch (RoomStateException | TableException e) {
            e.printStackTrace();
        }*/

        Set<ManagerTable> freeTables = Local.getInstance().getFreeTableList();

        if(freeTables.size()==0){ // TODO : sistemare ciò
            Snackbar.make(
                    findViewById(android.R.id.content),
                    R.string.error_add_table_snackbar,
                    BaseTransientBottomBar.LENGTH_LONG
            ).show();
        }
        else {
            addTableAdapter = new AddTableAdapter(this, Local.getInstance().getFreeTableList());
            addTableRecyclerView.setAdapter(addTableAdapter);
        }
    }
}