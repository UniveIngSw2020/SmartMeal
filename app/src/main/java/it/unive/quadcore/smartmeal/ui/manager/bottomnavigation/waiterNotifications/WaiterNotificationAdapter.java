package it.unive.quadcore.smartmeal.ui.manager.bottomnavigation.waiterNotifications;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;

import it.unive.quadcore.smartmeal.R;
import it.unive.quadcore.smartmeal.local.Local;
import it.unive.quadcore.smartmeal.local.RoomStateException;
import it.unive.quadcore.smartmeal.local.TableException;
import it.unive.quadcore.smartmeal.local.WaiterNotificationException;
import it.unive.quadcore.smartmeal.model.WaiterNotification;
import it.unive.quadcore.smartmeal.ui.manager.ManagerRoomBottomNavigationActivity;
import it.unive.quadcore.smartmeal.ui.manager.bottomnavigation.EmptyListDialogFragment;

public class WaiterNotificationAdapter extends RecyclerView.Adapter<WaiterNotificationAdapter.NotificationViewHolder>{
    public static final class NotificationViewHolder extends RecyclerView.ViewHolder {
        private TextView tableTextView;
        private TextView dateHourTextView;
        private Button deleteButton;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);

            this.tableTextView = itemView.findViewById(R.id.table_notification_text_view);
            this.dateHourTextView = itemView.findViewById(R.id.date_hour_text_view);
            this.deleteButton = itemView.findViewById(R.id.waiter_notification_delete_button);

        }
    }

    private final Activity activity;
    private final List<WaiterNotification> waiterNotifications;

    public WaiterNotificationAdapter(Activity activity, SortedSet<WaiterNotification> waiterNotifications) {
        this.activity = activity;
        this.waiterNotifications = new ArrayList<>(waiterNotifications);
    }

    @NonNull
    @Override
    public WaiterNotificationAdapter.NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.waiter_notification_row, parent, false);
        return new WaiterNotificationAdapter.NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WaiterNotificationAdapter.NotificationViewHolder holder, int position) {
        WaiterNotification notification =  waiterNotifications.get(position);

        try {
            String prefix = activity.getString(R.string.table_prefix);
            holder.tableTextView.setText(String.format("%s %s", prefix, Local.getInstance().getTable(notification.getCustomer()).getId()));
        } catch (RoomStateException | TableException e) { // TODO :gestire eccezioni
            e.printStackTrace();
        }
        holder.dateHourTextView.setText(notification.getPrettyTime());

        holder.deleteButton.setOnClickListener(view->{
            try {
                Local.getInstance().removeWaiterNotification(notification);

                int notificationToRemoveIndex = waiterNotifications.indexOf(notification);
                waiterNotifications.remove(notificationToRemoveIndex);
                notifyItemRemoved(notificationToRemoveIndex);

            } catch (RoomStateException | WaiterNotificationException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public int getItemCount() {
        return waiterNotifications.size();
    }

    public void reload() {
        try {
            waiterNotifications.clear();
            waiterNotifications.addAll(Local.getInstance().getWaiterNotificationList());
            notifyDataSetChanged();
        } catch (RoomStateException e) {
            e.printStackTrace();
        } catch (WaiterNotificationException e) {
            e.printStackTrace();
        }
    }
}
