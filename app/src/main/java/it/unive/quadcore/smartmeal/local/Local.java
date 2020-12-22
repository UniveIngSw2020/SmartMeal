package it.unive.quadcore.smartmeal.local;

import android.app.Activity;

import java.util.Set;
import java.util.SortedSet;

import it.unive.quadcore.smartmeal.communication.ManagerCommunication;
import it.unive.quadcore.smartmeal.communication.ManagerCommunicationSTUB;
import it.unive.quadcore.smartmeal.model.ManagerTable;
import it.unive.quadcore.smartmeal.model.Table;
import it.unive.quadcore.smartmeal.model.WaiterNotification;

import static it.unive.quadcore.smartmeal.communication.CustomerHandler.Customer;


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
            throw new RoomStateException(true);

        // Creazione oggetti che gestiscono i tavoli e le notifiche cameriere
        tableHandler = new TableHandler();
        waiterNotificationHandler = new WaiterNotificationHandler();

        // Creazione oggetto comunicazione
        managerCommunication = ManagerCommunicationSTUB.getInstance(); // TODO : ricambiare a MAnagerCOmmunication

        // Passo le varie callback all gestore della comunicazione
        managerCommunication.onNotifyWaiter( // Callback da eseguire quando arriva notifica cameriere
                waiterNotification -> {
                    try {
                        waiterNotificationHandler.addNotification(waiterNotification);
                    } catch (WaiterNotificationException e) { // Eccezione : non si può aggiungere tale notifica cameriere
                        managerCommunication.reportException(e); // Forwardo l'eccezione al customer
                    }
                });
        managerCommunication.onRequestFreeTableList( () -> { // Callback da eseguire quando arriva richiesta lista tavoli liberi
            try {
                return tableHandler.getFreeTableList();
            } catch (TableException e) { // Eccezione : la lista di tavoli liberi è vuota
                // TODO : forwardare l'eccezione? in alternativa tale eccezione nel metodo getFreeTableList si potrebbe proprio
                // non mettere
                managerCommunication.reportException(e); // Forwardo l'eccezione al customer
                return null; // Oppure new TreeSet<>();
            }
        });
        managerCommunication.onSelectTable( (customer,table) -> { // Callback da eseguire quando arriva selezione di un tavolo
            try{
                tableHandler.assignTable(customer,table);
            }
            catch( TableException e){ // Eccezione : tale cliente non può prendere tale tavolo
                managerCommunication.reportException(e); // Forwardo l'eccezione al customer
            }
        });

        managerCommunication.onCustomerLeftRoom( customer -> {
            try{
                tableHandler.freeTable(tableHandler.getTable(customer));
            }catch( TableException e){
                managerCommunication.reportException(e);
            }
        });

        // Dico di avviare la stanza al gestore comunicazione
        managerCommunication.startRoom(activity);

        // Setto la stanza come aperta
        roomState = true;

    }

    // Ritorna la lista delle chiamate cameriere
    public SortedSet<WaiterNotification> getWaiterNotificationList() throws RoomStateException, WaiterNotificationException {
        if(!roomState) // La stanza non è aperta
            throw new RoomStateException(false);

        return waiterNotificationHandler.getNotificationList();
    }

    // Rimuove una notifica chiamata cameriere
    public void removeWaiterNotification(WaiterNotification waiterNotification) throws RoomStateException, WaiterNotificationException {
        if(!roomState) // La stanza non è aperta
            throw new RoomStateException(false);

        waiterNotificationHandler.removeNotification(waiterNotification);
    }

    // Ritorna la lista di tavoli liberi
    public Set<ManagerTable> getFreeTableList() throws RoomStateException, TableException { // TODO : SortedSet ?
        if(!roomState) // La stanza non è aperta
            throw new RoomStateException(false);

        return tableHandler.getFreeTableList();
    }

    // Ritorna la lista di tavoli occupati
    public Set<ManagerTable> getAssignedTableList() throws RoomStateException, TableException { // SortedSet ?
        if(!roomState) // La stanza non è aperta
            throw new RoomStateException(false);

        return tableHandler.getAssignedTableList();
    }

    // Cambia il tavolo associato ad un cliente
    public void changeCustomerTable(Customer customer, Table newTable) throws RoomStateException, TableException {
        if(!roomState) // La stanza non è aperta
            throw new RoomStateException(false);

        tableHandler.changeCustomerTable(customer,newTable);
    }

    // Assegna un tavolo ad un cliente
    public void assignTable(Customer customer, Table table) throws RoomStateException, TableException {
        if(!roomState) // La stanza non è aperta
            throw new RoomStateException(false);

        tableHandler.assignTable(customer,table);
    }

    // Libera il tavolo selezionato
    public void freeTable(Table table) throws RoomStateException, TableException {
        if(!roomState) // La stanza non è aperta
            throw new RoomStateException(false);

        tableHandler.freeTable(table);
    }

    // Ritorna il tavolo associato ad un cliente
    public Table getTable(Customer customer) throws RoomStateException, TableException {
        if(!roomState) // La stanza non è aperta
            throw new RoomStateException(false);

        return tableHandler.getTable(customer);
    }

    public Customer getCustomerByTable(Table table) throws RoomStateException, TableException {
        if(!roomState) // La stanza non è aperta
            throw new RoomStateException(false);

        return tableHandler.getCustomer(table);
    }

    // Chiude la stanza virtuale
    public void closeRoom() throws RoomStateException {
        if(!roomState) // La stanza non è aperta
            throw new RoomStateException(false);

        // Metto a null
        tableHandler = null;
        waiterNotificationHandler = null;

        // Chiudo stanza
        managerCommunication.closeRoom();

        // Metto a null (non sarebbe necessario)
        managerCommunication = null;

        roomState = false;
    }

    // TODO : rimuovere ciò. Solo per testing
    public void testingTableHandler(){
        ((ManagerCommunicationSTUB)managerCommunication).beginTableHandler();
    }

    public void testingWaiterNotificationHandler(){
        ((ManagerCommunicationSTUB)managerCommunication).beginWaiterNotificationHandler();
    }
}
