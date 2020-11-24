package it.unive.quadcore.smartmeal.comunication;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class ManagerCommunication {
    public static ManagerCommunication getInstance() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    // eventualmente prendere callback con costruttore


    public abstract void startRoom();
    public abstract void onNotifyWaiter(Consumer<WaiterNotification> consumer);
    public abstract void onSelectTable(Consumer<Table> consumer);
    public abstract void onRequestFreeTableList(Supplier<Collection<Table>> supplier);
    public abstract void closeRoom();
}
