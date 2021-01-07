package it.unive.quadcore.smartmeal.ui.customer.virtualroom;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.SortedSet;
import java.util.TreeSet;

import it.unive.quadcore.smartmeal.R;
import it.unive.quadcore.smartmeal.communication.CustomerCommunication;
import it.unive.quadcore.smartmeal.local.TableException;
import it.unive.quadcore.smartmeal.model.ManagerTable;
import it.unive.quadcore.smartmeal.model.Table;
import it.unive.quadcore.smartmeal.ui.customer.bottomnavigation.CustomerBottomNavigationActivity;

public class ChooseTableFragment extends Fragment {

    private static final String TAG = "ChooseTableFragment";

    private RecyclerView tableRecyclerView;
    private TableAdapter tableAdapter;

    private Button cancelButton;

    // TODO remove (solo per testing)
    private SortedSet<Table> fakeTableSortedSet;

    public ChooseTableFragment() {
        // Required empty public constructor
    }

//    /**
//     * Use this factory method to create a new instance of
//     * this fragment using the provided parameters.
//     *
//     * @return A new instance of fragment ChooseTableFragment.
//     */
//    public static ChooseTableFragment newInstance() {
//        ChooseTableFragment fragment = new ChooseTableFragment();
//        return fragment;
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_choose_table, container, false);

        tableRecyclerView = root.findViewById(R.id.table_recycler_view);

        joinRoom(root);

        // TODO remove (solo per testing)
        fakeTableSortedSet = new TreeSet<>();
        fakeTableSortedSet.add(new ManagerTable("a"));
        fakeTableSortedSet.add(new ManagerTable("b"));
        fakeTableSortedSet.add(new ManagerTable("c"));
        setupTableRecyclerView(root, fakeTableSortedSet);

        cancelButton = root.findViewById(R.id.cancellation_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomerCommunication.getInstance().leaveRoom();
                getActivity().finish();
            }
        });
        return root;
    }

    private void joinRoom(View root) {
        CustomerCommunication customerCommunication = CustomerCommunication.getInstance();

        // se il cliente non è connesso al gestore con nearby
        if (customerCommunication.isNotConnected()) {

            // imposta la callback da eseguire nel caso il gestore chiuda la stanza
            customerCommunication.onCloseRoom(() -> getActivity().runOnUiThread(() -> {
                startActivity(new Intent(
                        getActivity(),
                        CustomerBottomNavigationActivity.class
                ));

                Snackbar.make(
                        root.findViewById(android.R.id.content),
                        R.string.manager_closed_virtual_room,
                        BaseTransientBottomBar.LENGTH_LONG
                ).show();
            }));

            // TODO progress bar ?
//            ProgressBar connectionProgressBar = new ProgressBar(getContext());
//            connectionProgressBar.setIndeterminate(true);
//            connectionProgressBar.setVisibility(View.VISIBLE);

            Log.i(TAG, "join room");

            customerCommunication.joinRoom(
                    getActivity(),
                    () -> requestFreeTableList(root),
                    new CustomerNearbyTimeoutAction(getActivity())
            );
        }
    }

    private void requestFreeTableList(View root) {
        CustomerCommunication customerCommunication = CustomerCommunication.getInstance();

        customerCommunication.requestFreeTableList(
                response -> {
                    try {
                        TreeSet<Table> tableSet = response.getContent();

                        getActivity().runOnUiThread(() -> setupTableRecyclerView(root, tableSet));

                    } catch (TableException e) {
                        e.printStackTrace();
                    }
                },
                new CustomerNearbyTimeoutAction(getActivity())
        );
    }

    private void setupTableRecyclerView(View root, SortedSet<Table> tableSortedSet) {
        tableRecyclerView = root.findViewById(R.id.table_recycler_view);
        RecyclerView.LayoutManager recyclerViewLayoutManager = new LinearLayoutManager(
                getActivity(),
                RecyclerView.VERTICAL,
                false
        );
        tableRecyclerView.setLayoutManager(recyclerViewLayoutManager);
        tableAdapter = new TableAdapter(getActivity(), tableSortedSet);
        tableRecyclerView.setAdapter(tableAdapter);
    }
}
