package it.unive.quadcore.smartmeal.local;

import android.app.Activity;

import java.util.Collection;
import java.util.Set;
import java.util.SortedSet;

import it.unive.quadcore.smartmeal.communication.ManagerCommunication;
import it.unive.quadcore.smartmeal.model.Customer;
import it.unive.quadcore.smartmeal.model.ManagerTable;
import it.unive.quadcore.smartmeal.model.Table;
import it.unive.quadcore.smartmeal.model.WaiterNotification;

// TODO : gestione RACE CONDITION
// In realtà nella versione base (un solo dispositivo gestore) non ce ne dovrebbero essere di tali problemi

// CLasse locale (usata dal codice di gestione dell'interfaccia grafica)
public class Local {
    // Singletone
    private static Local instance = null;

    // Oggetti gestori tavoli e notifiche cameriere
    private TableHandler tableHandler;
    private WaiterNotificationHandler waiterNotificationHandler;

    // Oggetto per la comunicazione
    private ManagerCommunication managerCommunication ;

    // Stato della stanza: roomState=true -> stanza aperta ; roomState=false -> stanza chiusa
    private boolean roomState;

    // Costruttore privato
    private Local() {
        roomState = false;
    }

    // Instanziamento singletone
    public static Local getInstance() {
        if(instance==null)
            instance = new Local();
        return instance;
    }

    // Creazione stanza virtuale
    public void createRoom(Activity activity) throws RoomStateException {

        if(roomState) // Stanza virtuale già aperta
            throw new RoomStateException(roomState);

        // Creazione oggetti che gestiscono i tavoli e le notifiche cameriere
        //tableHandler = new TableHandler(); // TODO : rimuovere commento quando si implementa TableHandler
        waiterNotificationHandler = new WaiterNotificationHandler();

        // Creazione oggetto comunicazione
        managerCommunication = ManagerCommunication.getInstance();

        // Passo le varie callback all gestore della comunicazione
        managerCommunication.onNotifyWaiter(
                waiterNotification -> {
                    try {
                        waiterNotificationHandler.addNotification(waiterNotification);
                    } catch (WaiterNotificationException e) { // Eccezione : non si può aggiungere tale notifica cameriere
                        managerCommunication.reportException(e); // Forwardo l'eccezione al customer
                    }
                });
        // TODO : pensare come risolvere problema generic
        managerCommunication.onRequestFreeTableList( () -> tableHandler.getFreeTableList());
        managerCommunication.onSelectTable( (customer,table) -> tableHandler.assignTable(customer,table));

        // Dico di avviare la stanza al gestore comunicazione
        managerCommunication.startRoom(activity);

        // Setto la stanza come aperta
        roomState = true;
    }


    public SortedSet<WaiterNotification> getWaiterNotificationList() throws RoomStateException, WaiterNotificationException {
        if(!roomState) // La stanza non è aperta
            throw new RoomStateException(roomState);

        return waiterNotificationHandler.getNotificationList();
    }
    public void removeWaiterNotification(WaiterNotification waiterNotification) throws RoomStateException, WaiterNotificationException {
        if(!roomState) // La stanza non è aperta
            throw new RoomStateException(roomState);

        waiterNotificationHandler.removeNotification(waiterNotification);
    }
    public Set<ManagerTable> getFreeTableList() throws RoomStateException {
        if(!roomState) // La stanza non è aperta
            throw new RoomStateException(roomState);

        return tableHandler.getFreeTableList();
    }
    public void changeCustomerTable(Customer customer, Table newTable) throws RoomStateException {
        if(!roomState) // La stanza non è aperta
            throw new RoomStateException(roomState);

        tableHandler.changeCustomerTable(customer,newTable);
    }
    public void assignTable(Customer customer, Table table) throws RoomStateException {
        if(!roomState) // La stanza non è aperta
            throw new RoomStateException(roomState);

        tableHandler.assignTable(customer,table);
    }
    public void freeTable(Table table) throws RoomStateException {
        if(!roomState) // La stanza non è aperta
            throw new RoomStateException(roomState);

        tableHandler.freeTable(table);
    }
    public Table getTable(Customer customer) throws RoomStateException {
        if(!roomState) // La stanza non è aperta
            throw new RoomStateException(roomState);

        return tableHandler.getTable(customer);
    }

    public void closeRoom() throws RoomStateException {
        if(!roomState) // La stanza non è aperta
            throw new RoomStateException(roomState);

        // Metto a null
        tableHandler = null;
        waiterNotificationHandler = null;

        // Chiudo stanza
        managerCommunication.closeRoom();

        // Metto a null
        managerCommunication = null;

        roomState = false;
    }
}
