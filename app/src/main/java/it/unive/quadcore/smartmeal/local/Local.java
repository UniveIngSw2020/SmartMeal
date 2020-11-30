package it.unive.quadcore.smartmeal.local;

import java.util.Set;
import java.util.SortedSet;

import it.unive.quadcore.smartmeal.model.Customer;
import it.unive.quadcore.smartmeal.model.ManagerTable;
import it.unive.quadcore.smartmeal.model.Table;
import it.unive.quadcore.smartmeal.model.WaiterNotification;

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
