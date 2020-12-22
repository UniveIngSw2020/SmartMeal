package it.unive.quadcore.smartmeal.communication.response;

import java.io.Serializable;

public class ErrorResponse<T extends Serializable, E extends Exception> implements Response<T, E> {
    private final E exception;

    public ErrorResponse(E exception) {
        this.exception = exception;
    }

    @Override
    public T getContent() throws E {
        throw exception;
    }
}
