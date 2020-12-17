package it.unive.quadcore.smartmeal.storage;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;

import it.unive.quadcore.smartmeal.model.LocalDescription;

class Storage {

    // TODO : possibilità di fare Storage oggetto singletone

    // TODO aggiungere metodi per impostazioni

    protected static boolean initialized = false;

    protected static Activity activity ; //

    // Shared Preferences
    private static SharedPreferences defSharedPref ;
    protected static SharedPreferences sharedPref;
    /**
     * Rende non instanziabile questa classe.
     */
    Storage() {}

    public static void initializeStorage(Activity activity){ // Alter ego di getInstance
        if(initialized)
            throw new StorageException("The storage has alredy been initialize");

        Storage.activity = activity;

        // Shared Preference di deafult. Usata per i settings dell'applicazione.
        defSharedPref = PreferenceManager.getDefaultSharedPreferences(activity);

        // Shared Preference per nome e tavoli.
        sharedPref = activity.getSharedPreferences("SharedPreference" , Context.MODE_PRIVATE);

        initialized=true;
    }

    public static ApplicationMode getApplicationMode() {
        if(!initialized)
            throw new StorageException("The storage hasn't been initialize yet");

        String applicationModeString = defSharedPref.getString("ApplicationMode",null); // TODO : rimpiazzare con stringa di res
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

        SharedPreferences.Editor editor = defSharedPref.edit();

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

        String name = sharedPref.getString("Name",null); // TODO : rimpiazzare con stringa di res
        if(name==null)
            throw new StorageException("The name was not found in storage");

        return name;
    }

    public static void setName(String name) {
        if(!initialized)
            throw new StorageException("The storage hasn't been initialize yet");

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("Name", name); // TODO : rimpiazzare con stringa di res
        editor.apply();

    }
}
