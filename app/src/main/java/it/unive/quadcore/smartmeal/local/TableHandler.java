package it.unive.quadcore.smartmeal.local;

import java.util.HashSet;

import it.unive.quadcore.smartmeal.model.Customer;
import it.unive.quadcore.smartmeal.model.ManagerTable;
import it.unive.quadcore.smartmeal.model.Table;

// TODO : gestione RACE CONDITION

public abstract class TableHandler {
    // Visibile solo alla classe Locale (private o package-private)

    // eventuale mappa tra Customer e Table (o viceversa)

    // TODO : pensare come risolvere problema generic
    //public abstract Set<ManagerTable> getFreeManagerTableList();
    public abstract HashSet<ManagerTable> getFreeTableList();
    public abstract void changeCustomerTable(Customer customer, Table newTable);
    public abstract void assignTable(Customer customer, Table table);
    public abstract void freeTable(Table table);
    public abstract Table getTable(Customer customer);
}
