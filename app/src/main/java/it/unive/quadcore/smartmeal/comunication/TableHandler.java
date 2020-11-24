package it.unive.quadcore.smartmeal.comunication;

import java.util.Set;

public abstract class TableHandler {
    // Visibile solo alla classe Locale (private o package-private)

    // eventuale mappa tra Customer e Table (o viceversa)

    public abstract Set<ManagerTable> getFreeTableList();
    public abstract void changeCustomerTable(Customer customer, Table newTable);
    public abstract void assignTable(Customer customer, Table table);
    public abstract void freeTable(Table table);
    public abstract Table getTable(Customer customer);
}
