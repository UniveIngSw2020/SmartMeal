package it.unive.quadcore.smartmeal.ui.manager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.Objects;

import it.unive.quadcore.smartmeal.R;
import it.unive.quadcore.smartmeal.storage.CustomerStorage;
import it.unive.quadcore.smartmeal.storage.ManagerStorage;
import it.unive.quadcore.smartmeal.ui.customer.bottomnavigation.menu.MenuAdapter;

public class MenuManagerActivity extends AppCompatActivity {

    private RecyclerView menuRecyclerView;
    private MenuAdapter menuAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_menu_manager);

        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.title_manager_menu);

        menuRecyclerView = findViewById(R.id.menu_recycler_view_manager);
        RecyclerView.LayoutManager recyclerViewLayoutManager = new LinearLayoutManager(
                this,
                RecyclerView.VERTICAL,
                false
        );
        menuRecyclerView.setLayoutManager(recyclerViewLayoutManager);
        menuAdapter = new MenuAdapter(this, ManagerStorage.getLocalDescription().getMenu());
        menuRecyclerView.setAdapter(menuAdapter);
    }
}