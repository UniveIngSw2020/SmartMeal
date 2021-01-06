package it.unive.quadcore.smartmeal.ui.manager.addTable;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import it.unive.quadcore.smartmeal.R;
import it.unive.quadcore.smartmeal.local.Local;
import it.unive.quadcore.smartmeal.model.ManagerTable;
import it.unive.quadcore.smartmeal.ui.manager.bottomnavigation.tableList.AssignedTableListAdapter;

public class AddTableDialogFragment extends DialogFragment {
    private final AssignedTableListAdapter assignedTableListAdapter;
    private final List<ManagerTable> freeTables;

    private AddTableAdapter addTableAdapter;
    private RecyclerView addTableRecyclerView;

    //private AddTableViewModel addTableViewModel;


    public AddTableDialogFragment(Set<ManagerTable> freeTables, AssignedTableListAdapter assignedTableListAdapter){
        super();
        this.assignedTableListAdapter = assignedTableListAdapter;
        this.freeTables = new ArrayList<>(freeTables);

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        String confirmLabel = getActivity().getString(R.string.confirm_label_alert);
        String closeLabel = getActivity().getString(R.string.close_label_alert);

        String title = getActivity().getString(R.string.add_table_alert);

        LayoutInflater inflater = requireActivity().getLayoutInflater();


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(title)
                .setView(inflater.inflate(R.layout.activity_add_table, null))
                .setPositiveButton(confirmLabel,new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //
                    }
                })
                .setNegativeButton(closeLabel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //
                    }
                });

        return builder.create();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        /*addTableViewModel =
                new ViewModelProvider(this).get(AddTableViewModel.class);*/
        View root = inflater.inflate(R.layout.activity_add_table, container, false);

       /* int width = 1000;//getResources().getDimensionPixelSize(R.dimen.popup_width);
        int height = 1000;//getResources().getDimensionPixelSize(R.dimen.popup_height);
        getDialog().getWindow().setLayout(width, height);*/

        setupAddTableRecyclerView(root);

        return root;
    }

    private void setupAddTableRecyclerView(View root) {
        // TODO aggiungere sezioni a RecyclerView

        addTableRecyclerView = root.findViewById(R.id.add_table_recycler_view);
        RecyclerView.LayoutManager recyclerViewLayoutManager = new LinearLayoutManager(
                getActivity(),
                RecyclerView.VERTICAL,
                false
        );
        addTableRecyclerView.setLayoutManager(recyclerViewLayoutManager);

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

        addTableAdapter = new AddTableAdapter(getActivity(), Local.getInstance().getFreeTableList());
        addTableRecyclerView.setAdapter(addTableAdapter);
    }

}
