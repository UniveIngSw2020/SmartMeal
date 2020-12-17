package it.unive.quadcore.smartmeal.storage;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Set;
import java.util.TreeSet;

import it.unive.quadcore.smartmeal.model.ManagerTable;
import it.unive.quadcore.smartmeal.model.Table;

public final class ManagerStorage extends Storage {

    // TODO : fare SharedPreference apposito per ManagerStorage
    //private static SharedPreferences managerSharedPref = null;

    /**
     * Rende non instanziabile questa classe.
     */
    private ManagerStorage() {}

    // TODO : cambiare

    public static Set<ManagerTable> getTables() {
        //throw new UnsupportedOperationException("Not implemented yet");
       /* ManagerTable t1 = new ManagerTable("A1");
        ManagerTable t2 = new ManagerTable("A2");
        ManagerTable t3 = new ManagerTable("B7");
        ManagerTable t4 = new ManagerTable("C1");

        Set<ManagerTable> tables = new TreeSet<>();
        tables.add(t1);
        tables.add(t2);
        tables.add(t3);
        tables.add(t4);*/

        if(!initialized)
            throw new StorageException("The storage hasn't been initialize yet");

        Set<String> tablesString = sharedPref.getStringSet("Tables",null);
        if(tablesString==null)
            throw new StorageException("The tables were not found in storage");

        Set<ManagerTable> tables = new TreeSet<>();
        for(String tableId : tablesString){
            ManagerTable table = new ManagerTable(tableId);
            tables.add(table);
        }

        return tables;
    }

    // Ritorna il numero massimo di notifiche in coda di uno stesso utente
    public static int getMaxNotificationNumber(){
        if(!initialized)
            throw new StorageException("The storage hasn't been initialize yet");

        int maxNotificationNumber = sharedPref.getInt("MaxNotificationNumber",-1);
        if(maxNotificationNumber<0)
            throw new StorageException("The max notification number was not found in storage");

        return maxNotificationNumber;
    }
}
