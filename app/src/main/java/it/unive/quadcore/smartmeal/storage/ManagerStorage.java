package it.unive.quadcore.smartmeal.storage;

import java.util.Set;

import it.unive.quadcore.smartmeal.model.ManagerTable;

public final class ManagerStorage {

    /**
     * Rende non instanziabile questa classe.
     */
    private ManagerStorage() {}

    public static Set<ManagerTable> getTables() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    // Ritorna il numero massimo di notifiche in coda di uno stesso utente
    public static int getMaxNotificationNumber(){
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
