package it.unive.quadcore.smartmeal.communication;

import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import it.unive.quadcore.smartmeal.model.Customer;
import it.unive.quadcore.smartmeal.model.Table;
import it.unive.quadcore.smartmeal.model.WaiterNotification;

public abstract class ManagerCommunication {
    public static ManagerCommunication getInstance() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    // eventualmente prendere callback con costruttore


    public abstract void startRoom();
    public abstract void onNotifyWaiter(Consumer<WaiterNotification> consumer);
    public abstract void onSelectTable(BiConsumer<Customer,Table> consumer); // TODO : dire agli altri
    public abstract void onRequestFreeTableList(Supplier<Collection<Table>> supplier);

    public abstract void reportException(Exception exception);

    public abstract void closeRoom();
}
