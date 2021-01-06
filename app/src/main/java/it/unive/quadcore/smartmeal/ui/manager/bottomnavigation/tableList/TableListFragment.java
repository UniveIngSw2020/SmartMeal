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
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import it.unive.quadcore.smartmeal.R;
import it.unive.quadcore.smartmeal.local.Local;
import it.unive.quadcore.smartmeal.ui.manager.bottomnavigation.tableList.addTable.AddTableActivity;

// Fragment della stanza virtuale gestore che mostra lista tavoli occupati
public class TableListFragment extends Fragment {

    // Recycler view lista tavoli
    private RecyclerView tableListRecyclerView;
    // Adapter della recycler view lista tavoli
    public AssignedTableListAdapter assignedTableListAdapter;

    private TableListViewModel tableListViewModel;

    private Button reloadButton;
    private FloatingActionButton floatingButton;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        tableListViewModel =
                new ViewModelProvider(this).get(TableListViewModel.class);
        View root = inflater.inflate(R.layout.fragment_table_list, container, false);

        // Setta recycler view
        setupTableListRecyclerView(root);

        reloadButton = root.findViewById(R.id.reload_button_table_list);
        reloadButton.setOnClickListener(v -> { // Ricarico la lista tavoli occupati
            Local.getInstance().testingUI_2(); // TODO : togliere, solo per testing

            //setupTableListRecyclerView(root);
            assignedTableListAdapter.reload();
        });

        floatingButton = root.findViewById(R.id.floating_button_add_table);
        floatingButton.setOnClickListener(v -> { // Voglio aggiungere un tavolo occupato
            // TODO : sistemare

            if(Local.getInstance().getFreeTableList().size()==0){ // Lista tavoli liberi è vuota : non si può aggiungere un tavolo
                Snackbar.make(
                        getActivity().findViewById(android.R.id.content),
                        R.string.error_add_table_snackbar,
                        BaseTransientBottomBar.LENGTH_LONG
                ).show();
                return ;
            }

            // Lista tavoli liberi non è vuota : vado all'activity aggiunta tavolo

            Intent intent = new Intent(v.getContext(), AddTableActivity.class);
            startActivity(intent);

            /*FragmentManager fragmentManager = ((FragmentActivity)v.getContext()).getSupportFragmentManager();

            AddTableDialogFragment newFragment = new AddTableDialogFragment(Local.getInstance().getFreeTableList(),tableListAdapter);
            newFragment.show(fragmentManager,"addTable");

            //newFragment.getWindow().setLayout(600, 400);

            /*FragmentTransaction transaction = fragmentManager.beginTransaction();
            // For a little polish, specify a transition animation
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            // To make it fullscreen, use the 'content' root view as the container
            // for the fragment, which is always the root view for the activity
            transaction.add(R.id., newFragment)
                    .addToBackStack(null).commit();*/

            //newFragment.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        });

        return root;
    }

    // Setto recycler view
    private void setupTableListRecyclerView(View root) {
        // TODO aggiungere sezioni a RecyclerView

        tableListRecyclerView = root.findViewById(R.id.table_list_recycler_view);
        RecyclerView.LayoutManager recyclerViewLayoutManager = new LinearLayoutManager(
                getActivity(),
                RecyclerView.VERTICAL,
                false
        );
        tableListRecyclerView.setLayoutManager(recyclerViewLayoutManager);


        assignedTableListAdapter = new AssignedTableListAdapter(getActivity(), Local.getInstance().getAssignedTableList());
        tableListRecyclerView.setAdapter(assignedTableListAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        assignedTableListAdapter.reload(); // Aggiorna lista tavoli occupati
    }
}