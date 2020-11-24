package it.unive.quadcore.smartmeal.comunication;

public abstract class WaiterNotification {

//    public WaiterNotification(Customer, Ora);  TODO capire che oggetto usare per specificare l'ora

    public abstract Customer getCustomer();
    public abstract Object getTime();   // TODO cambiare tipo restituito
}
