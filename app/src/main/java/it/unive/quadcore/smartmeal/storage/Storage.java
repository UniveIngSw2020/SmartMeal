package it.unive.quadcore.smartmeal.storage;

import it.unive.quadcore.smartmeal.model.LocalDescription;

public final class Storage {

    /**
     * Rende non instanziabile questa classe.
     */
    private Storage() {}

    public static ApplicationMode getApplicationMode() {
        throw new UnsupportedOperationException("Not implemented yet");
    }
    public static void setApplicationMode(ApplicationMode applicationMode) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public static LocalDescription getLocalDescription() {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
