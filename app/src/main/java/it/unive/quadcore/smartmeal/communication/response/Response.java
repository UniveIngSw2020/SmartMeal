package it.unive.quadcore.smartmeal.communication.response;

import java.io.Serializable;

public interface Response<T extends Serializable, E extends Exception> extends Serializable {
    T getContent() throws E;

    // TODO magari aggiungere metodo isSuccessful() -> boolean
}
