package it.unive.quadcore.smartmeal.ui.manager.bottomnavigation.tableList;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import it.unive.quadcore.smartmeal.R;
import it.unive.quadcore.smartmeal.local.Local;
import it.unive.quadcore.smartmeal.local.RoomStateException;
import it.unive.quadcore.smartmeal.local.TableException;
import it.unive.quadcore.smartmeal.ui.manager.addTable.AddTableActivity;
import it.unive.quadcore.smartmeal.ui.manager.bottomnavigation.EmptyListDialogFragment;

public class TableListFragment extends Fragment {

    private RecyclerView tableListRecyclerView;
    private TableListAdapter tableListAdapter;

    private TableListViewModel tableListViewModel;

    private Button reloadButton;
    private FloatingActionButton floatingButton;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        tableListViewModel =
                new ViewModelProvider(this).get(TableListViewModel.class);
        View root = inflater.inflate(R.layout.fragment_table_list, container, false);

        setupTableListRecyclerView(root);

        reloadButton = root.findViewById(R.id.reload_button_table_list);
        reloadButton.setOnClickListener(v -> {
            Local.getInstance().testingUI_2(); // TODO : togliere, solo per testing

            //setupTableListRecyclerView(root);
            tableListAdapter.reload();
        });

        floatingButton = root.findViewById(R.id.floating_button_add_table);
        floatingButton.setOnClickListener(v -> {
            // avvia l'activity principale del Gestore
            Intent intent = new Intent(v.getContext(), AddTableActivity.class);
            startActivity(intent);
        });

        return root;
    }

    private void setupTableListRecyclerView(View root) {
        // TODO aggiungere sezioni a RecyclerView

        tableListRecyclerView = root.findViewById(R.id.table_list_recycler_view);
        RecyclerView.LayoutManager recyclerViewLayoutManager = new LinearLayoutManager(
                getActivity(),
                RecyclerView.VERTICAL,
                false
        );
        tableListRecyclerView.setLayoutManager(recyclerViewLayoutManager);

        /*try {
            tableListAdapter = new TableListAdapter(getActivity(), Local.getInstance().getAssignedTableList());
            tableListRecyclerView.setAdapter(tableListAdapter);
        } catch (RoomStateException e) {
            e.printStackTrace();
        } catch (TableException e) { // TODO : tenere ?
            // Non ci sono notifiche : mostro un dialog
            //new EmptyListDialogFragment(getString(R.string.empty_table_list_alert))
             //       .show(requireFragmentManager(), "noTablesAssigned"); // TODO : deprecato
        }*/

        tableListAdapter = new TableListAdapter(getActivity(), Local.getInstance().getAssignedTableList());
        tableListRecyclerView.setAdapter(tableListAdapter);
    }
}