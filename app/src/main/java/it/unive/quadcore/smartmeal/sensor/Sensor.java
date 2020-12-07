package it.unive.quadcore.smartmeal.sensor;

public abstract class Sensor {
    public abstract void onShakeDetected(Runnable onShakeDetectedCallback);
    public abstract void onEntrance(Runnable onEntranceCallback);
}
