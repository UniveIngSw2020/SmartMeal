package it.unive.quadcore.smartmeal.sensor;

import com.google.android.gms.location.GeofencingClient;

public abstract class Sensor {

    private GeofencingClient geofencingClient;

    private Runnable onShakeDetectedCallback;
    private Runnable onEntranceCallback;

    public abstract void onShakeDetected(Runnable onShakeDetectedCallback);
    public abstract void onEntrance(Runnable onEntranceCallback);
}
