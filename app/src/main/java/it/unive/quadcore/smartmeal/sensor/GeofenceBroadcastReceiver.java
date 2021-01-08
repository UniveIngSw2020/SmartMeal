package it.unive.quadcore.smartmeal.sensor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;


public class GeofenceBroadcastReceiver extends BroadcastReceiver {

    // Ricevuto evento geofence
    public void onReceive(Context context, Intent intent) {
        // Geofence event
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        if (geofencingEvent.hasError()) { // Errore evento geofence
            // TODO : implementare
            System.out.println("Si è verificato un errore nell'evento geofence");
            return;
        }

        System.out.println("Evento ricevuto dal geofence");

        // Geofence transition type
        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        // Testo geofence transition type:  vedo se è quella che mi interessa. A me interessa solo entrata.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {

            // CHIAMARE ONENTRANCECALLBACK
            Sensor.onEntranceCallback.run();


        } else { // Geofence transition type non è quella di mio interesse
            // TODO : implementare
        }
    }
}