package it.unive.quadcore.smartmeal.communication;

import java.io.Serializable;

class Message implements Serializable {
    private final RequestType requestType;
    private final Serializable content;

    Message(RequestType requestType, Serializable content) {
        this.requestType = requestType;
        this.content = content;
    }

    RequestType getRequestType() {
        return requestType;
    }
    Serializable getContent() {
        return content;
    };
}
