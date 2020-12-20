package it.unive.quadcore.smartmeal.communication.confirmation;

import java.io.Serializable;

public class Confirmation<E extends Exception> implements Serializable {
    public Confirmation() {}

    public void obtain() throws E {}
}
