package it.unive.quadcore.smartmeal.communication.response;

import java.io.Serializable;

public class ErrorResponse<E extends Exception> implements Response<Serializable, E> {
    private final E exception;

    private ErrorResponse(E exception) {
        this.exception = exception;
    }

    @Override
    public Serializable getContent() throws E {
        throw exception;
    }
}
