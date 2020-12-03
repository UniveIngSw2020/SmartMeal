package it.unive.quadcore.smartmeal.model;

// E' comparable rispetto alla data-ora.
public abstract class WaiterNotification implements Comparable<WaiterNotification>{

//    public WaiterNotification(Customer, Ora);  TODO capire che oggetto usare per specificare l'ora

    public abstract Customer getCustomer();
    // Data e orario
    public abstract Object getTime();   // TODO cambiare tipo restituito
}
