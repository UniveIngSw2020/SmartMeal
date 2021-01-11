package it.unive.quadcore.smartmeal.sensor;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
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

import java.util.ArrayList;
import java.util.List;

import it.unive.quadcore.smartmeal.storage.CustomerStorage;

public class SensorDetector {
    private final static String TAG = "Sensor";

    @Nullable
    private static SensorDetector instance;


    // Callback da eseguire quando si rileva entrata
    static Runnable onEntranceCallback;
    // Booleano che mi dice se sto rilevando entrata oppure no
    private boolean isEntranceDetecting;
    // Activity a cui sono legate le geofence
    private Activity geofenceActivity ;

    private GeofencingClient geofencingClient;
    private List<Geofence> geofenceList;
    private PendingIntent geofencePendingIntent;


    // Callback da eseguire quando si rileva lo shake
    private Runnable onShakeDetectedCallback;
    // Booleano che mi dice se sto rilevando shake oppure no
    private boolean isShakeDetecting;

    // Accelerazione minima del telefono perchè si possa definire shake
    private static final float SHAKE_THRESHOLD = 6.0f; // m/s^2
    // Tempo minimo tra due shake successivi
    private static final int MIN_TIME_BETWEEN_SHAKES_MILLISECS = 2000; // 2 secondi
    // Tempo dell'ultimo shake
    private long lastShakeTime;

    private SensorManager sensorManager;
    // Listener di un qualsiasi movimento del dispositivo
    private SensorEventListener sensorEventListener;


    public synchronized static SensorDetector getInstance() {
        if (instance == null) {
            instance = new SensorDetector();
        }

        return instance;
    }

    private SensorDetector() { }



    // METODI RILEVAZIONE SHAKE


    // Crea il listener di un qualsiasi movimento del dispositivo
    @NonNull
    private SensorEventListener createSensorEventListener(){
        return new SensorEventListener() {

            // Dispositivo si è mosso
            @Override
            public void onSensorChanged(SensorEvent event) {

                // Cerco di capire se si tratta di uno shake o no

                if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                    long curTime = System.currentTimeMillis();
                    if ((curTime - lastShakeTime) > MIN_TIME_BETWEEN_SHAKES_MILLISECS) {

                        float x = event.values[0];
                        float y = event.values[1];
                        float z = event.values[2];

                        double acceleration = Math.sqrt(Math.pow(x, 2) +
                                Math.pow(y, 2) +
                                Math.pow(z, 2)) - SensorManager.GRAVITY_EARTH;
                        Log.d(TAG, "Acceleration is " + acceleration + "m/s^2");

                        // Si tratta di uno shake
                        if (acceleration > SHAKE_THRESHOLD) { // SHAKE DETECTED
                            lastShakeTime = curTime;
                            Log.d(TAG, "Shake detected");
                            onShakeDetectedCallback.run(); // Eseguo la callback
                        }
                    }
                }

            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
                // EMPTY
            }


        };
    }

    // Inizio a rilevare shake, passando la callback da eseguire quando si rileva lo shake
    public void startShakeDetection(@NonNull Runnable onShakeDetectedCallback,@NonNull Activity activity){
        if(isShakeDetecting)
            throw new IllegalStateException("The shake has alredy been detecting");

        isShakeDetecting = true;

        this.onShakeDetectedCallback = onShakeDetectedCallback;

        sensorManager = (SensorManager) activity.getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager == null) {
            throw new UnsupportedOperationException("Sensors are not supported");
        }

        // Listener di un qualsiasi movimento del dispositivo.
        // E' in grado di capire se il movimento è uno shake oppure no. E in caso esegue la callback onShakeDetectedCallback
        sensorEventListener = createSensorEventListener();

        // Registro il listener
        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (accelerometer != null) {
            sensorManager.registerListener(sensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            Log.d(TAG, "SensorEventListener registered");
        }

        isShakeDetecting = true;

    }

    // Smetto di rilevare shake
    public void endShakeDetection(){
        if(!isShakeDetecting)
            throw new IllegalStateException("The shake hasn't been detecting yet");

        sensorManager.unregisterListener(sensorEventListener);
        Log.d(TAG, "SensorEventListener unregistered");

        isShakeDetecting = false;
    }



    // METODI RILEVAZIONE ENTRATA

    @NonNull
    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(geofenceList);
        return builder.build();
    }

    @NonNull
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

    public void startEntranceDetection(@NonNull Runnable onEntranceCallback,@NonNull Activity activity) {
        if(isShakeDetecting)
            throw new IllegalStateException("The entrance has alredy been detecting");

        SensorDetector.onEntranceCallback = onEntranceCallback;

        geofenceActivity = activity;

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
        geofencingClient.addGeofences(getGeofencingRequest(), getGeofencePendingIntent(geofenceActivity))
                .addOnSuccessListener(geofenceActivity, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.e(TAG,"Geofence client added to the list");
                    }
                })
                .addOnFailureListener(geofenceActivity, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG,"It isn't possible add the geofence");
                    }
                });

        isEntranceDetecting = true;
    }

    public void endEntranceDetection(){
        if(isShakeDetecting)
            throw new IllegalStateException("The shake hasn't been detecting yet");

        geofencingClient.removeGeofences(getGeofencePendingIntent(geofenceActivity))
                .addOnSuccessListener(geofenceActivity, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.e(TAG,"Geofence client removed from the list");
                    }
                })
                .addOnFailureListener(geofenceActivity, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG,"It isn't possible remove the geofence");
                    }
                });

        isEntranceDetecting = false;
    }
}
