package it.unive.quadcore.smartmeal.model;

import androidx.annotation.NonNull;

import java.util.Date;
import java.util.Objects;;

// Classe che rappresenta le notifiche al cameriere
public class WaiterNotification implements Comparable<WaiterNotification> {

    // Tipo per la data e ora
    private final Date dateTime;
    // Cliente che ha inviato la notifica
    private final Customer customer;

    public WaiterNotification(Customer customer) {
        dateTime = new Date(System.currentTimeMillis()); //Data attuale
        this.customer = customer;
    }

    public Customer getCustomer() {
        return customer;
    }

    // Data e orario
    public Date getTime(){
        return new Date(dateTime.getTime());
    }

    // Comparabile rispetto alla data. Ordinamento naturale.
    // Se le date sono uguali, comparo i clienti.
    @Override
    public int compareTo(WaiterNotification o) {
        if(dateTime.compareTo(o.dateTime)!=0)
            return dateTime.compareTo(o.dateTime);
        else
            // Comparare i nomi dei clienti non è ottimale perchè se no compareTo non sarebbe più consistente rispetto a equals
            // Non sarebbe più vero che equals dà true se e solo se compareTo dà 0
            //return customer.getName().compareTo(o.getCustomer().getName());
            return customer.getId().compareTo(o.getCustomer().getId()); // Comparo gli id dei clienti
    }

    // Uguaglianza rispetto alla data e al customer.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WaiterNotification that = (WaiterNotification) o;
        return dateTime.equals(that.dateTime) &&
                customer.equals(that.customer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dateTime, customer);
    }

    @NonNull
    @Override
    public String toString() {
        return "WaiterNotification{" +
                "dateTime=" + dateTime +
                ", customer=" + customer +
                '}';
    }
}
