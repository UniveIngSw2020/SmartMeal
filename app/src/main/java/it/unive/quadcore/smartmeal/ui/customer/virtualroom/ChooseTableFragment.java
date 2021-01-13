package it.unive.quadcore.smartmeal.ui.customer.virtualroom;

import android.app.Activity;
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
import it.unive.quadcore.smartmeal.model.Table;
import it.unive.quadcore.smartmeal.sensor.SensorDetector;
import it.unive.quadcore.smartmeal.ui.customer.bottomnavigation.CustomerBottomNavigationActivity;
import it.unive.quadcore.smartmeal.ui.customer.virtualroom.callback.CustomerLeaveRoomAction;

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
//        fakeTableSortedSet = new TreeSet<>();
//        fakeTableSortedSet.add(new ManagerTable("a"));
//        fakeTableSortedSet.add(new ManagerTable("b"));
//        fakeTableSortedSet.add(new ManagerTable("c"));
//        setupTableRecyclerView(root, fakeTableSortedSet);

        cancelButton = root.findViewById(R.id.cancellation_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomerCommunication.getInstance().leaveRoom();
                Intent returnIntent = new Intent();

                Activity activity = getActivity();
                if (activity != null) {
                    activity.setResult(Activity.RESULT_CANCELED, returnIntent);
                    activity.finish();
                }
            }
        });
        return root;
    }

    private void joinRoom(View root) {
        CustomerCommunication customerCommunication = CustomerCommunication.getInstance();

        Activity activity = getActivity();

        customerCommunication.onTableChanged(table -> {
            new CustomerLeaveRoomAction(activity, activity.getString(R.string.table_changed_snackbar)).run();
            customerCommunication.leaveRoom();
        });
        customerCommunication.onTableRemoved(() -> {
            new CustomerLeaveRoomAction(activity, activity.getString(R.string.table_removed_snackbar)).run();
            customerCommunication.leaveRoom();
        });

        // se il cliente non è connesso al gestore con nearby
        if (customerCommunication.isNotConnected()) {

            // imposta la callback da eseguire nel caso il gestore chiuda la stanza
            customerCommunication.onCloseRoom(() -> {
                try {
                    SensorDetector.getInstance().endShakeDetection();
                }
                catch (IllegalStateException e) {
                    Log.w(TAG, "tried to stop detection but not activated yet");
                }

                new CustomerLeaveRoomAction(activity, activity.getString(R.string.manager_closed_virtual_room)).run();
            });

            // TODO progress bar ?
//            ProgressBar connectionProgressBar = new ProgressBar(getContext());
//            connectionProgressBar.setIndeterminate(true);
//            connectionProgressBar.setVisibility(View.VISIBLE);

            Log.i(TAG, "join room");

            // TODO capire se crea problemi (fa sempre la join room ?)
            if (activity != null) {
                customerCommunication.joinRoom(
                        activity,
                        () -> requestFreeTableList(root),
                        new CustomerLeaveRoomAction(activity, getString(R.string.timeout_error_snackbar))
                );
            }
        }
    }

    private void requestFreeTableList(View root) {
        CustomerCommunication customerCommunication = CustomerCommunication.getInstance();

        customerCommunication.requestFreeTableList(
                response -> {       // callback eseguita quando arriva la risposta con la lista di tavoli dal manager
                    try {
                        TreeSet<Table> tableSet = response.getContent();

                        Activity activity = getActivity();
                        if (activity != null) {
                            activity.runOnUiThread(() -> setupTableRecyclerView(root, tableSet));
                        }

                    } catch (TableException e) {
                        e.printStackTrace();
                    }
                },
                new CustomerLeaveRoomAction(getActivity(), getString(R.string.timeout_error_snackbar))
        );
    }

    private void setupTableRecyclerView(View root, SortedSet<Table> tableSortedSet) {
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }

        tableRecyclerView = root.findViewById(R.id.table_recycler_view);
        RecyclerView.LayoutManager recyclerViewLayoutManager = new LinearLayoutManager(
                activity,
                RecyclerView.VERTICAL,
                false
        );
        tableRecyclerView.setLayoutManager(recyclerViewLayoutManager);
        tableAdapter = new TableAdapter(activity, tableSortedSet);
        tableRecyclerView.setAdapter(tableAdapter);
    }
}
