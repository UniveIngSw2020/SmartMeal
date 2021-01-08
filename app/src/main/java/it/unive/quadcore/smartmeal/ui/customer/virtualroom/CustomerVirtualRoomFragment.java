package it.unive.quadcore.smartmeal.ui.customer.virtualroom;

import android.os.Bundle;

import androidx.core.util.Consumer;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import it.unive.quadcore.smartmeal.R;
import it.unive.quadcore.smartmeal.communication.CustomerCommunication;
import it.unive.quadcore.smartmeal.communication.confirmation.Confirmation;
import it.unive.quadcore.smartmeal.local.WaiterNotificationException;
import it.unive.quadcore.smartmeal.ui.customer.bottomnavigation.menu.MenuFragment;

public class CustomerVirtualRoomFragment extends Fragment {

    private static final String TAG = "CustomerVirtualRoomFrag";

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String TABLE_ID_PARAM = "tableId";


    private String tableId;


    private TextView tableNumberTextView;
    private Button menuButton;
    private Button callButton;
    private Button exitButton;


    public CustomerVirtualRoomFragment() {
        // Required empty public constructor

        this.tableId = "";
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param tableId Parameter 1.
     * @return A new instance of fragment CustomerVirtualRoomFragment.
     */
    public static CustomerVirtualRoomFragment newInstance(String tableId) {
        CustomerVirtualRoomFragment fragment = new CustomerVirtualRoomFragment();
        Bundle args = new Bundle();
        args.putString(TABLE_ID_PARAM, tableId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            tableId = getArguments().getString(TABLE_ID_PARAM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_customer_virtual_room, container, false);

        tableNumberTextView = root.findViewById(R.id.table_number_text_view);
        menuButton = root.findViewById(R.id.menu_button);
        callButton = root.findViewById(R.id.call_button);
        exitButton = root.findViewById(R.id.exit_customer_room_button);


        tableNumberTextView.setText(tableId);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO probabilmente si può fare meglio
                MenuFragment menuFragment = new MenuFragment();

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager
                        .beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .replace(R.id.customer_room_fragment_container, menuFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomerCommunication customerCommunication = CustomerCommunication.getInstance();


                // TODO eventualmente se dopo tot secondi non è arrivata la conferma, inviare nuovamente
//                Thread thread;
                customerCommunication.notifyWaiter(
                        (Consumer<Confirmation<? extends WaiterNotificationException>>) confirmation -> {
        //                        thread.interrupt();
                            int snackbarMessageId;

                            try {
                                confirmation.obtain();
                                snackbarMessageId = R.string.waiter_notification_confirmed;
                            } catch (WaiterNotificationException e) {
                                Log.i(TAG, "Waiter notification rejected: " + e.getMessage());
                                snackbarMessageId = R.string.waiter_notification_rejected;
                            }

                            final int snackbarMessageIdFinal = snackbarMessageId;
                            getActivity().runOnUiThread(() -> {
                                Snackbar.make(
                                        getActivity().findViewById(android.R.id.content),
                                        snackbarMessageIdFinal,
                                        BaseTransientBottomBar.LENGTH_LONG
                                ).show();
                            });
                        },
                        new CustomerNearbyTimeoutAction(getActivity())
                );
            }
        });

        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomerCommunication customerCommunication = CustomerCommunication.getInstance();
                customerCommunication.leaveRoom();
            }
        });

        return root;
    }
}
