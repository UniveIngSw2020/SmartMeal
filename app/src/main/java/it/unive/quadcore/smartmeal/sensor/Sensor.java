package it.unive.quadcore.smartmeal.sensor;

import com.google.android.gms.location.GeofencingClient;

public abstract class Sensor {

    private GeofencingClient geofencingClient;

    private Runnable onShakeDetectedCallback;
    private Runnable onEntranceCallback;

    public abstract void startShakeDetection(Runnable onShakeDetectedCallback);
    public abstract void endShakeDetection();

    public abstract void startEntranceDetection(Runnable onEntranceCallback);
    public abstract void endEntranceDetection();
}
