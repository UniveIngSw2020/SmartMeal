package it.unive.quadcore.smartmeal.communication.response;

import java.io.Serializable;

public class SuccessResponse<T extends Serializable, E extends Exception> implements Response<T, E> {
    private final T content;

    public SuccessResponse(T content) {
        this.content = content;
    }

    @Override
    public T getContent() {
        return content;
    }
}
