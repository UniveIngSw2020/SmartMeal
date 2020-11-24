package it.unive.quadcore.smartmeal.comunication;

import java.util.SortedSet;

public abstract class WaiterNotificationHandler {
    public abstract void addNotification(WaiterNotification waiterNotification);
    public abstract void removeNotification(WaiterNotification waiterNotification);
    public abstract SortedSet<WaiterNotification> getNotificationList();
}
