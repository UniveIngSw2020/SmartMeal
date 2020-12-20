package it.unive.quadcore.smartmeal.storage;

import android.content.SharedPreferences;

public final class CustomerStorage extends Storage {

    /**
     * Rende non instanziabile questa classe.
     */
    private CustomerStorage() {}

    public static boolean getSensorMode() {
        if(!initialized)
            throw new StorageException("The storage hasn't been initialize yet");

        // Preference non esistente (prima creazione della preference). Metto valore di default
        if(!defaultSharedPreferences.contains("SensorMode")) { // TODO : rimpiazzare con stringa di res
            SharedPreferences.Editor editor = defaultSharedPreferences.edit();
            editor.putBoolean("SensorMode",true);
            editor.apply();
        }

        return defaultSharedPreferences.getBoolean("SensorMode",true);
    }

    public static void setSensorMode(boolean mode) {
        if(!initialized)
            throw new StorageException("The storage hasn't been initialize yet");

        SharedPreferences.Editor editor = defaultSharedPreferences.edit();

        // Se preference non esiste viene creata
        editor.putBoolean("SensorMode",mode); // TODO : rimpiazzare con stringa di res
        editor.apply();
    }

    public static boolean getNotificationMode() {
        if(!initialized)
            throw new StorageException("The storage hasn't been initialize yet");

        // Preference non esistente (prima creazione della preference). Metto valore di default
        if(!defaultSharedPreferences.contains("NotificationMode")) { // TODO : rimpiazzare con stringa di res
            SharedPreferences.Editor editor = defaultSharedPreferences.edit();
            editor.putBoolean("NotificationMode",true);
            editor.apply();
        }

        return defaultSharedPreferences.getBoolean("NotificationMode",true);
    }

    public static void setNotificationMode(boolean mode) {
        if(!initialized)
            throw new StorageException("The storage hasn't been initialize yet");

        SharedPreferences.Editor editor = defaultSharedPreferences.edit();

        // Se preference non esiste viene creata
        editor.putBoolean("NotificationMode",mode); // TODO : rimpiazzare con stringa di res
        editor.apply();
    }

}
