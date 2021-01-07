package it.unive.quadcore.smartmeal.sensor;

public abstract class Sensor {
    private Runnable onShakeDetectedCallback;
    private Runnable onEntranceCallback;

    public abstract void startShakeDetection(Runnable onShakeDetectedCallback);
    public abstract void endShakeDetection();

    public abstract void startEntranceDetection(Runnable onEntranceCallback);
    public abstract void endEntranceDetection();
}
