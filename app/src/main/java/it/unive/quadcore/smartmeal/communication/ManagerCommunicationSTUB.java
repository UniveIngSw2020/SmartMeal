package it.unive.quadcore.smartmeal.communication;

import android.app.Activity;

import androidx.core.util.Consumer;
import androidx.core.util.Supplier;

import com.google.android.gms.common.util.BiConsumer;

import java.util.TreeSet;

import it.unive.quadcore.smartmeal.model.ManagerTable;
import it.unive.quadcore.smartmeal.model.Table;
import it.unive.quadcore.smartmeal.model.WaiterNotification;

/*
public class ManagerCommunicationSTUB extends ManagerCommunication{
    private static ManagerCommunicationSTUB instance = null;

    private Supplier<TreeSet<? extends Table>> supplierFreeTables;

    private BiConsumer<Customer, Table> consumerSelectTable;

    private Consumer<Customer> consumerCustomerLeft ;

    private Consumer<WaiterNotification> consumerNotifyWaiter ;

    public static ManagerCommunicationSTUB getInstance(){
        if(instance==null)
            instance = new ManagerCommunicationSTUB();

        return instance;

    }


    public void startRoom(Activity activity) {

    }
    @Override
    public void onNotifyWaiter(Consumer<WaiterNotification> consumer) {
        consumerNotifyWaiter = consumer;
    }

    @Override
    public void onSelectTable(BiConsumer<Customer, Table> consumer) {
        consumerSelectTable = consumer;
    }

    public void onRequestFreeTableList(Supplier<TreeSet<? extends Table>> supplier) {
        supplierFreeTables = supplier;
    }

    @Override
    public void onCustomerLeftRoom(Consumer<Customer> consumer) {
        consumerCustomerLeft = consumer;
    }

    @Override
    public void reportException(Exception exception) {
        exception.printStackTrace();
    }

    @Override
    public void closeRoom() {

    }

    // Per testing
    public void beginTableHandler(){
        Customer client = new Customer(112,"Enrico");
        TreeSet<? extends Table> freeTables = supplierFreeTables.get();
        Table table = freeTables.first();
        consumerSelectTable.accept(client,table);

        Customer client1 = new Customer(113,"Matteo");
        Table table1 = freeTables.last();
        consumerSelectTable.accept(client1,table1);

        freeTables = supplierFreeTables.get();
        Table table2 = freeTables.first();
        Customer client2 = new Customer(114,"Giacomo");
        consumerSelectTable.accept(client2,table2);

        freeTables = supplierFreeTables.get();
        Table table3 = freeTables.first();
        Customer client3 = new Customer(115,"Davide");
        consumerSelectTable.accept(client3,table3);

        consumerCustomerLeft.accept(client2);
        freeTables = supplierFreeTables.get();

//        Customer client4 = new Customer(112,"Andrea");
//        Table table4 = freeTables.first();
//        consumerSelectTable.accept(client4,table4);
    }

    public void beginWaiterNotificationHandler(){
        Customer client = new Customer(112,"Enrico");
        WaiterNotification notification1 = new WaiterNotification(client);
        consumerNotifyWaiter.accept(notification1);

        WaiterNotification notification2 = new WaiterNotification(client);
        consumerNotifyWaiter.accept(notification2);

        Customer client2 = new Customer(113,"Matteo");
        WaiterNotification notification3 = new WaiterNotification(client2);
        consumerNotifyWaiter.accept(notification3);

        consumerNotifyWaiter.accept(notification1); // Eccezione : notifica che esiste già

//        for(int i=0;i<5;i++) // Eccezione : superato numeo massimo notitifiche
//            consumerNotifyWaiter.accept(new WaiterNotification(client));
    }
}
*/
