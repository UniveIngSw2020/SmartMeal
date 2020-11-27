package it.unive.quadcore.smartmeal.comunication;

import java.util.Set;
import java.util.SortedSet;

public abstract class Local {
    public static Local getInstance() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public abstract void createRoom();
    public abstract SortedSet<WaiterNotification> getWaiterNotificationList();
    public abstract void removeWaiterNotification(WaiterNotification waiterNotification);
    public abstract Set<ManagerTable> getFreeTableList();
    public abstract void changeCustomerTable(Customer customer, Table newTable);
    public abstract void assignTable(Customer customer, Table table);
    public abstract void freeTable(Table table);
    public abstract Table getTable(Customer customer);
    public abstract void closeRoom();
}
