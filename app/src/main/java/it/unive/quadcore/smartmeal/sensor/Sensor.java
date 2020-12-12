package it.unive.quadcore.smartmeal.sensor;

public abstract class Sensor {
    private Runnable onShakeDetectedCallback;
    private Runnable onEntranceCallback;

    public abstract void onShakeDetected(Runnable onShakeDetectedCallback);
    public abstract void onEntrance(Runnable onEntranceCallback);
}
