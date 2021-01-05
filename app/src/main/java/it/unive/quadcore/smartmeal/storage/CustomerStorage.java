package it.unive.quadcore.smartmeal.storage;

import android.content.SharedPreferences;

public final class CustomerStorage extends Storage {

    private static final String SENSOR_MODE_SHARED_PREFERENCE_KEY = "SensorMode";
    private static final String NOTIFICATION_MODE_SHARED_PREFERENCE_KEY = "NotificationMode";
    /**
     * Rende non instanziabile questa classe.
     */
    private CustomerStorage() {}

    public static boolean getSensorMode() {
        if(!initialized)
            throw new StorageException("The storage hasn't been initialize yet");

        // Preference non esistente (prima creazione della preference). Metto valore di default
        if(!defaultSharedPreferences.contains(SENSOR_MODE_SHARED_PREFERENCE_KEY)) {
            SharedPreferences.Editor editor = defaultSharedPreferences.edit();
            editor.putBoolean(SENSOR_MODE_SHARED_PREFERENCE_KEY,true);
            editor.apply();
        }

        return defaultSharedPreferences.getBoolean(SENSOR_MODE_SHARED_PREFERENCE_KEY,true);
    }

    public static void setSensorMode(boolean mode) {
        if(!initialized)
            throw new StorageException("The storage hasn't been initialize yet");

        SharedPreferences.Editor editor = defaultSharedPreferences.edit();

        // Se preference non esiste viene creata
        editor.putBoolean(SENSOR_MODE_SHARED_PREFERENCE_KEY,mode);
        editor.apply();
    }

    public static boolean getNotificationMode() {
        if(!initialized)
            throw new StorageException("The storage hasn't been initialize yet");

        // Preference non esistente (prima creazione della preference). Metto valore di default
        if(!defaultSharedPreferences.contains(NOTIFICATION_MODE_SHARED_PREFERENCE_KEY)) {
            SharedPreferences.Editor editor = defaultSharedPreferences.edit();
            editor.putBoolean(NOTIFICATION_MODE_SHARED_PREFERENCE_KEY,true);
            editor.apply();
        }

        return defaultSharedPreferences.getBoolean(NOTIFICATION_MODE_SHARED_PREFERENCE_KEY,true);
    }

    public static void setNotificationMode(boolean mode) {
        if(!initialized)
            throw new StorageException("The storage hasn't been initialize yet");

        SharedPreferences.Editor editor = defaultSharedPreferences.edit();

        // Se preference non esiste viene creata
        editor.putBoolean(NOTIFICATION_MODE_SHARED_PREFERENCE_KEY,mode);
        editor.apply();
    }

}
