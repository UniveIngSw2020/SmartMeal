package it.unive.quadcore.smartmeal.sensor;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

    private GeofencingClient geofencingClient;
    private List<Geofence> geofenceList;

    private Runnable onShakeDetectedCallback;
    static Runnable onEntranceCallback;
    private PendingIntent geofencePendingIntent;

    @Nullable
    private static Sensor instance;

    public synchronized static Sensor getInstance() {
        if (instance == null) {
            instance = new Sensor();
        }

        return instance;
    }

    private Sensor() { }

    public void startShakeDetection(Runnable onShakeDetectedCallback){
        // TODO
    }

    public void endShakeDetection(){
        // TODO
    }


    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(geofenceList);
        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent(Activity activity) {
        // Reuse the PendingIntent if we already have it.
        if (geofencePendingIntent != null) {
            return geofencePendingIntent;
        }
        Intent intent = new Intent(activity, GeofenceBroadcastReceiver.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when
        // calling addGeofences() and removeGeofences().
        geofencePendingIntent = PendingIntent.getBroadcast(activity, 0, intent, PendingIntent.
                FLAG_UPDATE_CURRENT);
        return geofencePendingIntent;
    }

    public void startEntranceDetection(Runnable onEntranceCallback, Activity activity) {

        Sensor.onEntranceCallback = onEntranceCallback;

        geofencingClient = LocationServices.getGeofencingClient(activity);

        geofenceList = new ArrayList<>();

        Location location = CustomerStorage.getLocalDescription().getLocation();

        float radius = 20;

        geofenceList.add(new Geofence.Builder()
                // Id geofence
                .setRequestId("mygeofence")
                //
                .setCircularRegion(
                        location.getLatitude(),
                        location.getLongitude(),
                        radius//geofence radius precision
                )
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                .build());

        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        geofencingClient.addGeofences(getGeofencingRequest(), getGeofencePendingIntent(activity))
                .addOnSuccessListener(activity, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.e(TAG,"Geofence client added to the list");
                    }
                })
                .addOnFailureListener(activity, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG,"It isn't possible add a geofence");
                    }
                });

    }

    public void endEntranceDetection(Activity activity){
        geofencingClient.removeGeofences(getGeofencePendingIntent(activity))
                .addOnSuccessListener(activity, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Geofences removed
                        // ...
                    }
                })
                .addOnFailureListener(activity, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Failed to remove geofences
                        // ...
                    }
                });
    }
}
