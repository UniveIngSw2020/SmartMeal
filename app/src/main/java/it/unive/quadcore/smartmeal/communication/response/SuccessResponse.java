package it.unive.quadcore.smartmeal.communication.response;

import java.io.Serializable;

public class SuccessResponse<T extends Serializable> implements Response<T, Exception> {
    private final T content;

    private SuccessResponse(T content) {
        this.content = content;
    }

    @Override
    public T getContent() {
        return content;
    }
}
