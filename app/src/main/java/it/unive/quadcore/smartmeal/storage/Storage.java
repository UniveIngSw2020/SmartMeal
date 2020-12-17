package it.unive.quadcore.smartmeal.storage;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import android.preference.PreferenceManager;

import it.unive.quadcore.smartmeal.model.LocalDescription;

class Storage {

    // TODO : possibilità di fare Storage oggetto singletone

    // TODO aggiungere metodi per impostazioni

    protected static boolean initialized = false;

    protected static Activity activity ; //

    // Shared Preferences. La prima di deafult, ovvero si mettono i settings dell'applicazione. La seconda di uso generico.
    private static SharedPreferences defaultSharedPreferences;
    protected static SharedPreferences sharedPreferences;
    /**
     * Rende non instanziabile questa classe.
     */
    Storage() {}

    public static void initializeStorage(Activity activity){ // Alter ego di getInstance
        if(initialized)
            throw new StorageException("The storage has alredy been initialized");

        Storage.activity = activity;

        // Shared Preference di deafult. Usata per i settings dell'applicazione.
        defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);

        // Shared Preference di uso generico.
        sharedPreferences = activity.getSharedPreferences("SharedPreference" , Context.MODE_PRIVATE); // TODO : rimpiazzare con stringa di res

        initialized=true;
    }

    public static ApplicationMode getApplicationMode() {
        if(!initialized)
            throw new StorageException("The storage hasn't been initialize yet");

        String applicationModeString = defaultSharedPreferences.getString("ApplicationMode",null); // TODO : rimpiazzare con stringa di res
        if(applicationModeString==null)
            throw new StorageException("The application mode was not found in storage");

        ApplicationMode applicationMode ;
        try{
            applicationMode = ApplicationMode.valueOf(applicationModeString);
            return applicationMode;
        }catch(IllegalArgumentException e) {
            throw new StorageException("The storage contains an invalid application mode");
        }
    }

    public static void setApplicationMode(ApplicationMode applicationMode) {
        if(!initialized)
            throw new StorageException("The storage hasn't been initialize yet");

        SharedPreferences.Editor editor = defaultSharedPreferences.edit();

        String applicationModeString = applicationMode.name(); // toString in alternativa
        editor.putString("ApplicationMode",applicationModeString); // TODO : rimpiazzare con stringa di res
        editor.apply();
    }

    public static LocalDescription getLocalDescription() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public static String getName() {
        if(!initialized)
            throw new StorageException("The storage hasn't been initialize yet");

        String name = sharedPreferences.getString("Name",null); // TODO : rimpiazzare con stringa di res
        if(name==null)
            throw new StorageException("The name was not found in storage");

        return name;
    }

    public static void setName(String name) {
        if(!initialized)
            throw new StorageException("The storage hasn't been initialize yet");

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Name", name); // TODO : rimpiazzare con stringa di res
        editor.apply();

    }
}
