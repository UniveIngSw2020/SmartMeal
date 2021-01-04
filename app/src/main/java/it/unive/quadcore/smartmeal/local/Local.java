package it.unive.quadcore.smartmeal.local;

import android.app.Activity;
import android.util.Log;

import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedSet;

import it.unive.quadcore.smartmeal.communication.ManagerCommunication;
//import it.unive.quadcore.smartmeal.communication.ManagerCommunicationSTUB;
import it.unive.quadcore.smartmeal.communication.confirmation.Confirmation;
import it.unive.quadcore.smartmeal.communication.confirmation.ConfirmationDenied;
import it.unive.quadcore.smartmeal.communication.response.ErrorResponse;
import it.unive.quadcore.smartmeal.communication.response.SuccessResponse;
import it.unive.quadcore.smartmeal.model.Customer;
import it.unive.quadcore.smartmeal.model.ManagerTable;
import it.unive.quadcore.smartmeal.model.Table;
import it.unive.quadcore.smartmeal.model.WaiterNotification;

// TODO : gestione RACE CONDITION
// In realtà nella versione base (un solo dispositivo gestore) non ce ne dovrebbero essere di tali problemi

// CLasse locale (usata dal codice di gestione dell'interfaccia grafica)
public class Local {
    private static final String TAG = "Local";

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
    public void createRoom(Activity activity) {
        if(roomState) // Stanza virtuale già aperta
            throw new RoomStateException(true);

        // Creazione oggetti che gestiscono i tavoli e le notifiche cameriere
        tableHandler = new TableHandler();
        waiterNotificationHandler = new WaiterNotificationHandler();

        // Creazione oggetto comunicazione
//        managerCommunication = ManagerCommunicationSTUB.getInstance(); // TODO : ricambiare a MAnagerCOmmunication
        managerCommunication = ManagerCommunication.getInstance(); // TODO : ricambiare a MAnagerCOmmunication

        // Passo le varie callback all gestore della comunicazione
        managerCommunication.onNotifyWaiter( // Callback da eseguire quando arriva notifica cameriere
                waiterNotification -> {
                    try {
                        waiterNotificationHandler.addNotification(waiterNotification);
                        return new Confirmation<>();
                    } catch (WaiterNotificationException e) { // Eccezione : non si può aggiungere tale notifica cameriere
                        return new ConfirmationDenied<>(e); // Forwardo l'eccezione al customer
                    }
                });
        managerCommunication.onRequestFreeTableList( () -> { // Callback da eseguire quando arriva richiesta lista tavoli liberi
            return new SuccessResponse<>(tableHandler.getFreeTableList());
            /*try {
                return new SuccessResponse<>(tableHandler.getFreeTableList());
            } catch (TableException e) { // Eccezione : la lista di tavoli liberi è vuota
                // TODO : forwardare l'eccezione? in alternativa tale eccezione nel metodo getFreeTableList si potrebbe proprio
                // non mettere
                // Forwardo l'eccezione al customer
                return new ErrorResponse<>(e); // Oppure new TreeSet<>();
            }*/
        });
        managerCommunication.onSelectTable( (customer,table) -> { // Callback da eseguire quando arriva selezione di un tavolo
            try{
                tableHandler.assignTable(customer, table);
                return new Confirmation<>();
            }
            catch( TableException e){ // Eccezione : tale cliente non può prendere tale tavolo
                return new ConfirmationDenied<>(e); // Forwardo l'eccezione al customer
            }
        });

        managerCommunication.onCustomerLeftRoom( customer -> {
            try{
                tableHandler.freeTable(tableHandler.getTable(customer));
            }catch( TableException e){
                Log.e(TAG,"Customer that left the room didn't have a table");
            }
        });

        // Dico di avviare la stanza al gestore comunicazione
        managerCommunication.startRoom(activity);

        // Setto la stanza come aperta
        roomState = true;

    }

    // Ritorna la lista delle chiamate cameriere
    public SortedSet<WaiterNotification> getWaiterNotificationList()  {
        if(!roomState) // La stanza non è aperta
            throw new RoomStateException(false);

        return waiterNotificationHandler.getNotificationList();
    }

    // Rimuove una notifica chiamata cameriere
    public void removeWaiterNotification(WaiterNotification waiterNotification) throws WaiterNotificationException {
        if(!roomState) // La stanza non è aperta
            throw new RoomStateException(false);

        waiterNotificationHandler.removeNotification(waiterNotification);
    }

    // Ritorna la lista di tavoli liberi
    public Set<ManagerTable> getFreeTableList() { // TODO : SortedSet ?
        if(!roomState) // La stanza non è aperta
            throw new RoomStateException(false);

        return tableHandler.getFreeTableList();
    }

    // Ritorna la lista di tavoli occupati
    public Set<ManagerTable> getAssignedTableList() { // SortedSet ?
        if(!roomState) // La stanza non è aperta
            throw new RoomStateException(false);

        return tableHandler.getAssignedTableList();
    }

    // Cambia il tavolo associato ad un cliente
    public void changeCustomerTable(Customer customer, Table newTable) throws TableException {
        if(!roomState) // La stanza non è aperta
            throw new RoomStateException(false);

        tableHandler.changeCustomerTable(customer, newTable);
    }

    // Assegna un tavolo ad un cliente
    public void assignTable(Customer customer, Table table) throws TableException {
        if(!roomState) // La stanza non è aperta
            throw new RoomStateException(false);

        tableHandler.assignTable(customer, table);
    }

    // Libera il tavolo selezionato
    public void freeTable(Table table) throws TableException {
        if(!roomState) // La stanza non è aperta
            throw new RoomStateException(false);

        waiterNotificationHandler.removeCustomerNotifications(getCustomerByTable(table));

        tableHandler.freeTable(table);
    }

    // Ritorna il tavolo associato ad un cliente
    public Table getTable(Customer customer) throws TableException {
        if(!roomState) // La stanza non è aperta
            throw new RoomStateException(false);

        return tableHandler.getTable(customer);
    }

    public Customer getCustomerByTable(Table table) throws TableException {
        if(!roomState) // La stanza non è aperta
            throw new RoomStateException(false);

        return tableHandler.getCustomer(table);
    }

    // Chiude la stanza virtuale
    public void closeRoom() {
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
/*
    // TODO : rimuovere ciò. Solo per testing
    public void testingTableHandler(){
        ((ManagerCommunicationSTUB)managerCommunication).beginTableHandler();
    }

    public void testingWaiterNotificationHandler(){
        ((ManagerCommunicationSTUB)managerCommunication).beginWaiterNotificationHandler();
    }


 */

    public void testingUI() { // TODO : rimettere costruttore Customer a private
        Customer customer = new Customer("12334","Enrico");
        try {
            Table table = tableHandler.getFreeTableList().first();
            tableHandler.assignTable(customer,table);
        } catch (TableException | NoSuchElementException e ) {
            e.printStackTrace();
        }
        try {
            waiterNotificationHandler.addNotification(new WaiterNotification(customer));
        } catch (WaiterNotificationException e) {
            e.printStackTrace();
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Customer customer1 = new Customer("1","Matteo");
        try {
            Table table = tableHandler.getFreeTableList().first();
            tableHandler.assignTable(customer1,table);
        } catch (TableException | NoSuchElementException e) {
            e.printStackTrace();
        }
        try {
            waiterNotificationHandler.addNotification(new WaiterNotification(customer1));
        } catch (WaiterNotificationException e) {
            e.printStackTrace();
        }
    }

    public void testingUI_1() {
        Customer customer = new Customer("111111","Giacomo");
        try {
            Table table = tableHandler.getFreeTableList().last();
            tableHandler.assignTable(customer,table);
        } catch (TableException | NoSuchElementException e) {
            e.printStackTrace();
        }
        try {
            waiterNotificationHandler.addNotification(new WaiterNotification(customer));
        } catch (WaiterNotificationException e) {
            e.printStackTrace();
        }
    }

    public void testingUI_2() {
        Customer customer = new Customer("12345555555","Davide");
        try {
            Table table = tableHandler.getFreeTableList().last();
            tableHandler.assignTable(customer,table);
        } catch (TableException | NoSuchElementException e) {
            e.printStackTrace();
        }
        try {
            waiterNotificationHandler.addNotification(new WaiterNotification(customer));
        } catch (WaiterNotificationException e) {
            e.printStackTrace();
        }
    }
}
