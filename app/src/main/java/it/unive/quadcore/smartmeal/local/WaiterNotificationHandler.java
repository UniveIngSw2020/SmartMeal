package it.unive.quadcore.smartmeal.local;

import java.util.SortedSet;

import it.unive.quadcore.smartmeal.model.WaiterNotification;

public abstract class WaiterNotificationHandler {
    public abstract void addNotification(WaiterNotification waiterNotification);
    public abstract void removeNotification(WaiterNotification waiterNotification);
    public abstract SortedSet<WaiterNotification> getNotificationList();
}
