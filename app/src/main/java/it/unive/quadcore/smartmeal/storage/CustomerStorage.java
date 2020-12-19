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

        return defaultSharedPreferences.getBoolean("SensorMode",true); // TODO : rimpiazzare con stringa di res
    }

    public static void setSensorMode(boolean mode) {
        if(!initialized)
            throw new StorageException("The storage hasn't been initialize yet");

        SharedPreferences.Editor editor = defaultSharedPreferences.edit();

        editor.putBoolean("SensorMode",mode); // TODO : rimpiazzare con stringa di res
        editor.apply();
    }

    public static boolean getNotificationMode() {
        if(!initialized)
            throw new StorageException("The storage hasn't been initialize yet");

        return defaultSharedPreferences.getBoolean("NotificationMode",true); // TODO : rimpiazzare con stringa di res
    }

    public static void setNotificationMode(boolean mode) {
        if(!initialized)
            throw new StorageException("The storage hasn't been initialize yet");

        SharedPreferences.Editor editor = defaultSharedPreferences.edit();

        editor.putBoolean("NotificationMode",mode); // TODO : rimpiazzare con stringa di res
        editor.apply();
    }

}
