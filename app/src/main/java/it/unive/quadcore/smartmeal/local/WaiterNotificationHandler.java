package it.unive.quadcore.smartmeal.local;

import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import it.unive.quadcore.smartmeal.model.WaiterNotification;
import it.unive.quadcore.smartmeal.storage.ManagerStorage;

import static it.unive.quadcore.smartmeal.communication.CustomerHandler.Customer;

// TODO : gestione RACE CONDITION
// Si può semplicemente mettere tutti i metodi syncrhonized (per ora gestito così)

// Classe visibile solo a Local
class WaiterNotificationHandler {

    // Set ordinato delle notifiche cameriere. E' ordinato sulla base dell'ordinamento naturale della classe WaiterNotification.
    // Tale ordinamento è rispetto alla data-ora della notifica.
    private final SortedSet<WaiterNotification> notificationList ;

    // Massimo numero di notifiche in coda che ogni cliente può avere da gestire
    private final int MAX_NOTIFICATION_NUMBER ;

    // Costruttore visibile solo a Local
    WaiterNotificationHandler(){
        notificationList = new TreeSet<>();

        MAX_NOTIFICATION_NUMBER = ManagerStorage.getMaxNotificationNumber();
    }

    // Metodo privato che dato un customer ritorna il numero di notifiche in coda fatte da quel customer
    private int countCustomerWaiterNotification(Customer customer){
        int count=0;

        for(WaiterNotification waiterNotification : notificationList)
            if(waiterNotification.getCustomer().equals(customer))
                count++;

        return count;
    }

    /* Aggiunge una notifica alla coda di notifiche. E' un metodo visibile solo a Local.
       Tale operazione lancia un'eccezione in 2 casi:
                - se c'è già un numero di notification in coda fatte da quel customer superiore al limite massimo
                - se c'è già tale waiterNotification nella coda
     */
    synchronized void addNotification(WaiterNotification waiterNotification) throws WaiterNotificationException {

        // Controllo che il numero di notification in coda fatte da quel customer non sia superiore al limite massimo
        if(countCustomerWaiterNotification(waiterNotification.getCustomer())>=MAX_NOTIFICATION_NUMBER)
            throw new WaiterNotificationException("Max notifications number exceeded");

        if(!notificationList.add(waiterNotification))
            throw new WaiterNotificationException("The added notification alredy exists");
    }

    // Rimuove una notifica dal set. Se tale notifica non esiste viene lanciata un'eccezione.
    // E' un metodo visibile solo a Local.
    synchronized void removeNotification(WaiterNotification waiterNotification) throws WaiterNotificationException {
        if(!notificationList.remove(waiterNotification))
            throw new WaiterNotificationException("The selected notification does not exist");
    }

    // Ritorna la lista di notifiche. Se tale lista è vuota, ritorna un'eccezione.
    // E' un metodo visibile solo a Local.
    synchronized SortedSet<WaiterNotification> getNotificationList() throws WaiterNotificationException {
        if(notificationList==null || notificationList.isEmpty()) // Controllo a null non servirebbe
            throw new WaiterNotificationException("The notification list is empty");
        return new TreeSet<>(notificationList); // Ritorno una copia
    }

    // Rimuove tutte le notifiche effettuate da un client
    synchronized void removeCustomerNotifications(Customer customer) {
        Iterator<WaiterNotification> it = notificationList.iterator();
        while(it.hasNext()){
            WaiterNotification notification = it.next();
            if(notification.getCustomer().equals(customer))
                it.remove();
        }
    }
}
