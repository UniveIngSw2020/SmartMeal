package it.unive.quadcore.smartmeal.ui.manager.bottomnavigation.waiterNotifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import it.unive.quadcore.smartmeal.R;
import it.unive.quadcore.smartmeal.local.Local;
import it.unive.quadcore.smartmeal.local.RoomStateException;
import it.unive.quadcore.smartmeal.local.WaiterNotificationException;
import it.unive.quadcore.smartmeal.storage.CustomerStorage;
import it.unive.quadcore.smartmeal.ui.customer.bottomnavigation.menu.MenuAdapter;
import it.unive.quadcore.smartmeal.ui.customer.bottomnavigation.menu.MenuViewModel;


public class WaiterNotificationsFragment extends Fragment {
    private RecyclerView waiterNotificationRecyclerView;
    private WaiterNotificationAdapter waiterNotificationAdapter;

    private WaiterNotificationsViewModel waiterNotificationsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {

        waiterNotificationsViewModel =
                new ViewModelProvider(this).get(WaiterNotificationsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_menu, container, false);

        setupWaiterNotificationRecyclerView(root);
        return root;
    }

    private void setupWaiterNotificationRecyclerView(View root) {
        // TODO aggiungere sezioni a RecyclerView

        waiterNotificationRecyclerView = root.findViewById(R.id.waiter_notification_recycler_view);
        RecyclerView.LayoutManager recyclerViewLayoutManager = new LinearLayoutManager(
                getActivity(),
                RecyclerView.VERTICAL,
                false
        );
        waiterNotificationRecyclerView.setLayoutManager(recyclerViewLayoutManager);
        try {
            waiterNotificationAdapter = new WaiterNotificationAdapter(getActivity(), Local.getInstance().getWaiterNotificationList());
            waiterNotificationRecyclerView.setAdapter(waiterNotificationAdapter);
        } catch (RoomStateException e) {
            e.printStackTrace();
        } catch (WaiterNotificationException e) {
            e.printStackTrace();
        }
    }
}