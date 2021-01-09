package it.unive.quadcore.smartmeal.sensor;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import it.unive.quadcore.smartmeal.R;
import it.unive.quadcore.smartmeal.storage.CustomerStorage;

public class Sensor {
    private final static String TAG = "Sensor";

    private List<Geofence> geofenceList;

    private Runnable onShakeDetectedCallback;
    static Runnable onEntranceCallback;
    private PendingIntent geofencePendingIntent;

    public Sensor(){}

    /*
    public abstract void startShakeDetection(Runnable onShakeDetectedCallback);

    public abstract void endShakeDetection();
    */


    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(Geofence.GEOFENCE_TRANSITION_ENTER);
        builder.addGeofences(geofenceList);
        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent(Activity activity) {
        // Reuse the PendingIntent if we already have it.
        if (geofencePendingIntent != null) {
            return geofencePendingIntent;
        }
        Intent intent = new Intent(activity, GeofenceBroadcastReceiver.class);
        intent.setAction("ACTION_GEOFENCE_TRANSITION");
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when
        // calling addGeofences() and removeGeofences().
        geofencePendingIntent = PendingIntent.getBroadcast(activity, 0, intent, PendingIntent.
                FLAG_UPDATE_CURRENT);
        return geofencePendingIntent;
    }

    public void startEntranceDetection(Runnable onEntranceCallback, Activity activity) {

        Sensor.onEntranceCallback = onEntranceCallback;

        GeofencingClient geofencingClient = LocationServices.getGeofencingClient(activity);

        geofenceList = new ArrayList<>();

        Location location = CustomerStorage.getLocalDescription().getLocation();

        geofenceList.add(new Geofence.Builder()
                // Id geofence
                .setRequestId("geofence")
                .setCircularRegion(
                        location.getLatitude(),
                        location.getLongitude(),
                        20
                )
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                        Geofence.GEOFENCE_TRANSITION_EXIT)
                .build());

        System.out.println("Geofence aggiunto alla lista");
        System.out.println(geofenceList.get(0));

        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        geofencingClient.addGeofences(getGeofencingRequest(), getGeofencePendingIntent(activity))
                .addOnSuccessListener(activity, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Geofences added
                        // ...
                        System.out.println("Geofence client aggiunto alla lista");
                    }
                })
                .addOnFailureListener(activity, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG,"Non è stato possibile aggiungere una geofencing");
                    }
                });

    }
}
