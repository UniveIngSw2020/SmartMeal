package it.unive.quadcore.smartmeal.sensor;

import androidx.annotation.Nullable;

public class Sensor {
    @Nullable
    private static Sensor instance;

    public synchronized static Sensor getInstance() {
        if (instance == null) {
            instance = new Sensor();
        }

        return instance;
    }

    private Sensor() { }


    private Runnable onShakeDetectedCallback;
    private Runnable onEntranceCallback;

    public void startShakeDetection(Runnable onShakeDetectedCallback) {
        // TODO
    }

    public void endShakeDetection() {
        // TODO
    }


    public void startEntranceDetection(Runnable onEntranceCallback) {
        // TODO
    }

    public void endEntranceDetection() {
        // TODO
    }
}
