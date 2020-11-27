package it.unive.quadcore.smartmeal.comunication;

import java.io.Serializable;

public abstract class Message {
    public abstract RequestType getRequestType();
    public abstract Serializable getContent();
}
