package it.unive.quadcore.smartmeal.storage;


import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;

import it.unive.quadcore.smartmeal.model.ManagerTable;


public final class ManagerStorage extends Storage {

    // TODO : fare SharedPreference apposito per ManagerStorage
    //private static SharedPreferences managerSharedPref = null;

    /**
     * Rende non instanziabile questa classe.
     */
    private ManagerStorage() {}

    // TODO : cambiare

    // Usato per settare la prima volta
    private static void setTables(){
        if(!initialized)
            throw new StorageException("The storage hasn't been initialize yet");

        Set<String> tables = new TreeSet<>();
        char supp = 'A';
        while(supp<'Z'+1){
            for(int i=0;i<=9;i++)
                tables.add(""+supp+i);
            supp+=1;
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet("Tables", tables); // TODO : rimpiazzare con stringa di res
        editor.apply();
    }

    // Possibilità di non tenere i tavoli in memoria secondaria ma generarli e basta
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

        Set<String> tablesString = sharedPreferences.getStringSet("Tables",null); // TODO : rimpiazzare con stringa di res
        if(tablesString==null)
            throw new StorageException("The tables were not found in storage");

        Set<ManagerTable> tables = new TreeSet<>();
        for(String tableId : tablesString){
            ManagerTable table = new ManagerTable(tableId);
            tables.add(table);
        }

        return tables;
    }

    // Usato per settare la prima volta
    private static void setMaxNotificationNumber(int n){
        if(!initialized)
            throw new StorageException("The storage hasn't been initialize yet");

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("MaxNotificationNumber", n); // TODO : rimpiazzare con stringa di res
        editor.apply();
    }

    // Ritorna il numero massimo di notifiche in coda di uno stesso utente
    // Possibilità di non teneretale numero in memoria secondaria ma generarlo e basta
    public static int getMaxNotificationNumber(){
        if(!initialized)
            throw new StorageException("The storage hasn't been initialize yet");

        int maxNotificationNumber = sharedPreferences.getInt("MaxNotificationNumber",-1); // TODO : rimpiazzare con stringa di res
        if(maxNotificationNumber<0)
            throw new StorageException("The max notification number was not found in storage");

        return maxNotificationNumber;
    }
}
