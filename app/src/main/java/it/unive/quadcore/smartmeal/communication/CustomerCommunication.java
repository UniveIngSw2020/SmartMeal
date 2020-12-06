package it.unive.quadcore.smartmeal.communication;

import androidx.core.util.Consumer;

import java.util.Collection;

import it.unive.quadcore.smartmeal.model.Customer;
import it.unive.quadcore.smartmeal.model.Table;

public abstract class CustomerCommunication {

    public static CustomerCommunication getInstance() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    // eventualmente prendere callback con costruttore


    public abstract void joinRoom(Customer customer); // TODO : prende una String (o anche nulla)
    public abstract void notifyWaiter();
    public abstract void selectTable(Table table);
    public abstract void requestFreeTableList(Consumer<Collection<Table>> consumer);
    public abstract void leaveRoom();
    public abstract boolean isConnected();
}
