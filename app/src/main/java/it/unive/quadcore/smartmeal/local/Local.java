package it.unive.quadcore.smartmeal.local;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Set;
import java.util.SortedSet;

import it.unive.quadcore.smartmeal.communication.ManagerCommunication;
//import it.unive.quadcore.smartmeal.communication.ManagerCommunicationSTUB;
import it.unive.quadcore.smartmeal.communication.confirmation.Confirmation;
import it.unive.quadcore.smartmeal.communication.confirmation.ConfirmationDenied;
import it.unive.quadcore.smartmeal.communication.response.SuccessResponse;
import it.unive.quadcore.smartmeal.model.Customer;
import it.unive.quadcore.smartmeal.model.ManagerTable;
import it.unive.quadcore.smartmeal.model.Table;
import it.unive.quadcore.smartmeal.model.WaiterNotification;

// Gestione RACE CONDITION
// In realtà nella versione base (un solo dispositivo gestore) non ce ne dovrebbero essere di tali problemi

// Classe che rappresenta locale
public class Local {
    private static final String TAG = "Local";

    // Singletone
    @Nullable
    private static Local instance = null;

    // Oggetti gestori tavoli e notifiche cameriere
    @NonNull
    private TableHandler tableHandler;
    @NonNull
    private WaiterNotificationHandler waiterNotificationHandler;

    // Oggetto per la comunicazione
    @NonNull
    private ManagerCommunication managerCommunication ;

    // Stato della stanza: roomState=true -> stanza aperta ; roomState=false -> stanza chiusa
    private boolean roomState;

    // Costruttore privato
    private Local() {
        roomState = false;
    }

    // Instanziamento singletone
    @NonNull
    public static Local getInstance() {
        if(instance==null)
            instance = new Local();
        return instance;
    }

    // Creazione stanza virtuale
    public void createRoom(@NonNull Activity activity) {
        if(roomState) // Stanza virtuale già aperta
            throw new RoomStateException(true);

        // Creazione oggetti che gestiscono i tavoli e le notifiche cameriere
        tableHandler = new TableHandler();
        waiterNotificationHandler = new WaiterNotificationHandler();

        // Creazione oggetto comunicazione
//        managerCommunication = ManagerCommunicationSTUB.getInstance(); // Per testing
        managerCommunication = ManagerCommunication.getInstance();

        // Passo le varie callback allgestore della comunicazione
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
        managerCommunication.onCustomerLeftRoom( customer -> { // Callback da eseguire quando cliente esce dalla stanza virtuale
            try{
                tableHandler.freeTable(tableHandler.getTable(customer));
            }catch( TableException e){ // Eccezione
                Log.e(TAG,"Customer that left the room didn't have a table");
            }
        });

        // Dico di avviare la stanza al gestore comunicazione
        managerCommunication.startRoom(activity);

        // Setto la stanza come aperta
        roomState = true;

    }

    // Ritorna la lista delle chiamate cameriere
    @NonNull
    public SortedSet<WaiterNotification> getWaiterNotificationList()  {
        if(!roomState) // La stanza non è aperta
            throw new RoomStateException(false);

        return waiterNotificationHandler.getNotificationList();
    }

    // Rimuove una notifica chiamata cameriere
    public void removeWaiterNotification(@NonNull WaiterNotification waiterNotification) throws WaiterNotificationException {
        if(!roomState) // La stanza non è aperta
            throw new RoomStateException(false);

        waiterNotificationHandler.removeNotification(waiterNotification);
    }

    // Ritorna la lista di tavoli liberi
    @NonNull
    public Set<ManagerTable> getFreeTableList() {
        if(!roomState) // La stanza non è aperta
            throw new RoomStateException(false);

        return tableHandler.getFreeTableList();
    }

    // Ritorna la lista di tavoli occupati
    @NonNull
    public Set<ManagerTable> getAssignedTableList() {
        if(!roomState) // La stanza non è aperta
            throw new RoomStateException(false);

        return tableHandler.getAssignedTableList();
    }

    // Cambia il tavolo associato ad un cliente
    public void changeCustomerTable(@NonNull Customer customer,@NonNull Table newTable) throws TableException {
        if(!roomState) // La stanza non è aperta
            throw new RoomStateException(false);

        tableHandler.changeCustomerTable(customer, newTable);

        if(!LocalCustomerHandler.getInstance().containsCustomer(customer))
            managerCommunication.notifyTableChanged(customer,newTable);
    }

    // Assegna un tavolo ad un cliente
    public void assignTable(@NonNull String customerName,@NonNull Table table) throws TableException {
        if(!roomState) // La stanza non è aperta
            throw new RoomStateException(false);

        Customer newCustomer = LocalCustomerHandler.getInstance().addCustomer(customerName);
        tableHandler.assignTable(newCustomer, table);
    }

    // Libera il tavolo selezionato
    public void freeTable(@NonNull Table table) throws TableException {
        if(!roomState) // La stanza non è aperta
            throw new RoomStateException(false);

        Customer customer = getCustomerByTable(table);

        // Elimino tutte le notifiche effettuate da quel cliente
        waiterNotificationHandler.removeCustomerNotifications(customer);

        tableHandler.freeTable(table);

        if(!LocalCustomerHandler.getInstance().containsCustomer(customer))
            managerCommunication.notifyCustomerRemoved(customer);
    }

    // Ritorna il tavolo associato ad un cliente
    @NonNull
    public Table getTable(@NonNull Customer customer) throws TableException {
        if(!roomState) // La stanza non è aperta
            throw new RoomStateException(false);

        return tableHandler.getTable(customer);
    }

    // Ritorna il cliente con tale tavolo, se esiste
    @NonNull
    public Customer getCustomerByTable(@NonNull Table table) throws TableException {
        if(!roomState) // La stanza non è aperta
            throw new RoomStateException(false);

        return tableHandler.getCustomer(table);
    }

    // Chiude la stanza virtuale
    public void closeRoom() {
        if(!roomState) // La stanza non è aperta
            throw new RoomStateException(false);

 /*       // Metto a null
        tableHandler = null;
        waiterNotificationHandler = null;
*/
        // Chiudo stanza
        managerCommunication.closeRoom();
/*
        // Metto a null (non sarebbe necessario)
        managerCommunication = null;
*/
        roomState = false;
    }
/*

    // TESTING

    public void testingTableHandler(){
        ((ManagerCommunicationSTUB)managerCommunication).beginTableHandler();
    }

    public void testingWaiterNotificationHandler(){
        ((ManagerCommunicationSTUB)managerCommunication).beginWaiterNotificationHandler();
    }




    public void testingUI() {
        Customer customer = LocalCustomerHandler.getInstance().addCustomer("Enrico");
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
        Customer customer1 = LocalCustomerHandler.getInstance().addCustomer("Matteo");
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
        Customer customer = LocalCustomerHandler.getInstance().addCustomer("Giacomo");
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
        Customer customer = LocalCustomerHandler.getInstance().addCustomer("Davide");
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


 */
}
