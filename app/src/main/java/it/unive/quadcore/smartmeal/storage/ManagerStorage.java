package it.unive.quadcore.smartmeal.storage;

import java.util.Set;
import java.util.TreeSet;

import it.unive.quadcore.smartmeal.model.ManagerTable;
import it.unive.quadcore.smartmeal.model.Table;

public final class ManagerStorage extends Storage {

    /**
     * Rende non instanziabile questa classe.
     */
    private ManagerStorage() {}

    // TODO : cambiare

    public static Set<ManagerTable> getTables() {
        //throw new UnsupportedOperationException("Not implemented yet");
        ManagerTable t1 = new ManagerTable("A1");
        ManagerTable t2 = new ManagerTable("A2");
        ManagerTable t3 = new ManagerTable("B7");
        ManagerTable t4 = new ManagerTable("C1");

        Set<ManagerTable> tables = new TreeSet<>();
        tables.add(t1);
        tables.add(t2);
        tables.add(t3);
        tables.add(t4);

        return tables;
    }

    // Ritorna il numero massimo di notifiche in coda di uno stesso utente
    public static int getMaxNotificationNumber(){
        //throw new UnsupportedOperationException("Not implemented yet");
        return 5;
    }
}
