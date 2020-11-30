package it.unive.quadcore.smartmeal.local;

import java.util.Set;

import it.unive.quadcore.smartmeal.model.Customer;
import it.unive.quadcore.smartmeal.model.ManagerTable;
import it.unive.quadcore.smartmeal.model.Table;

public abstract class TableHandler {
    // Visibile solo alla classe Locale (private o package-private)

    // eventuale mappa tra Customer e Table (o viceversa)

    public abstract Set<ManagerTable> getFreeTableList();
    public abstract void changeCustomerTable(Customer customer, Table newTable);
    public abstract void assignTable(Customer customer, Table table);
    public abstract void freeTable(Table table);
    public abstract Table getTable(Customer customer);
}
