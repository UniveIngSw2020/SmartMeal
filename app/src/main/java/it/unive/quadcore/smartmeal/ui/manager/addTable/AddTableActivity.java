package it.unive.quadcore.smartmeal.ui.manager.addTable;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;
import java.util.Set;

import it.unive.quadcore.smartmeal.R;
import it.unive.quadcore.smartmeal.local.Local;
import it.unive.quadcore.smartmeal.model.ManagerTable;
import it.unive.quadcore.smartmeal.ui.manager.ConfirmLeavingRoomDialogFragment;
import it.unive.quadcore.smartmeal.ui.manager.bottomnavigation.tableList.TableListFragment;

// Activity per l'aggiunta tavolo
public class AddTableActivity extends AppCompatActivity {

    // Recycler view lista tavoli liberi
    private RecyclerView addTableRecyclerView;
    // Adapter recycler view lista tavoli liberi
    private AddTableAdapter addTableAdapter;
    private TextView customerHint;
    private TextView tableHint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_table);

        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.title_add_table);

        customerHint = findViewById(R.id.insert_new_costumer_hint_text_view);
        tableHint = findViewById(R.id.select_table_hint_text_view);
        // Setta recycler view
        setupAddTableRecyclerView();

    }

    // Setta recycler view
    private void setupAddTableRecyclerView() {
        // TODO aggiungere sezioni a RecyclerView
        addTableRecyclerView = findViewById(R.id.add_table_recycler_view);
        RecyclerView.LayoutManager recyclerViewLayoutManager = new LinearLayoutManager(
                this,
                RecyclerView.VERTICAL,
                false
        );
        addTableRecyclerView.setLayoutManager(recyclerViewLayoutManager);


        Set<ManagerTable> freeTables = Local.getInstance().getFreeTableList();

        if(freeTables.size()==0){ // Non ci sono tavoli liberi
            Snackbar.make(
                    findViewById(android.R.id.content),
                    R.string.error_add_table_snackbar,
                    BaseTransientBottomBar.LENGTH_LONG
            ).show();
        }
        else { // Ci sono tavoli liberi da poter selezionare
            addTableAdapter = new AddTableAdapter(this, Local.getInstance().getFreeTableList());
            addTableRecyclerView.setAdapter(addTableAdapter);
        }
    }
}