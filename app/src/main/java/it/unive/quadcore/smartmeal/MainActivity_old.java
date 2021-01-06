package it.unive.quadcore.smartmeal;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;

import it.unive.quadcore.smartmeal.local.Local;
import it.unive.quadcore.smartmeal.local.RoomStateException;
import it.unive.quadcore.smartmeal.local.TableException;
import it.unive.quadcore.smartmeal.local.WaiterNotificationException;
import it.unive.quadcore.smartmeal.model.LocalDescription;
import it.unive.quadcore.smartmeal.model.ManagerTable;
import it.unive.quadcore.smartmeal.model.Table;
import it.unive.quadcore.smartmeal.model.WaiterNotification;
import it.unive.quadcore.smartmeal.storage.ApplicationMode;
import it.unive.quadcore.smartmeal.storage.CustomerStorage;
import it.unive.quadcore.smartmeal.storage.ManagerStorage;
import it.unive.quadcore.smartmeal.storage.StorageException;

public class MainActivity_old extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_main); FILE LAYOUT ELIMINATO (ERA ACTIVITY VUOTA)

        /*
        Local local = Local.getInstance();

        try {
            local.createRoom(this);
        }catch(RoomStateException e){
            e.printStackTrace();
        }*/

        /* TESTING TABLE HANDLER
        local.testingTableHandler();

        try {
            Set<ManagerTable> assignedTables = local.getAssignedTableList();
            Set<ManagerTable> freeTables = local.getFreeTableList();
            System.out.println("Tavoli occupati: "+assignedTables);
            System.out.println("Tavoli liberi: "+freeTables);

            Customer client = new Customer(1119,"Marco");
            Table table = freeTables.iterator().next();
            local.assignTable(client,table);
            //freeTables = local.getFreeTableList(); // Da eccezione : non ci sono tavoli liberi

            // Libera il secondo tavolo
            Iterator<ManagerTable> it = assignedTables.iterator();
            it.next();
            Table table1 = it.next();
            local.freeTable(table1);

            local.getCustomerByTable(table1); // eccezione

            /* // Cambia il tavolo al cliente che aveva il primo tavolo
            Table table2 = assignedTables.iterator().next();
            Customer client1 = local.getCustomerByTable(table2);
            local.changeCustomerTable(client1,table1);*/

           /* // Stessa cosa ma con eccezione : cambio con lo stesso tavolo
            Table table2 = assignedTables.iterator().next();
            Customer client1 = local.getCustomerByTable(table2);
            local.changeCustomerTable(client1,table2);*/

           /* // Stessa cosa ma con eccezione : cliente non ha un tavolo
            Table table2 = assignedTables.iterator().next();
            local.changeCustomerTable(new Customer(111111,"Giulio"),table1);*/

           /* // Liberiamo tutti i tavoli
            assignedTables = local.getAssignedTableList();
            for(Table t : assignedTables)
                local.freeTable(t);
            assignedTables = local.getAssignedTableList();*/

        /*}catch(RoomStateException e){
            e.printStackTrace();
        }
        catch(TableException e){
            e.printStackTrace();
        }

        try {
            local.closeRoom();
        }catch(RoomStateException e){
            e.printStackTrace();
        }*/


        /* TESTING WAITER NOTIFICATION HANDLER


        local.testingWaiterNotificationHandler();


        try {
            SortedSet<WaiterNotification> notifications = local.getWaiterNotificationList();

            WaiterNotification notification1 = notifications.first();
            WaiterNotification notification2 = notifications.last();

            local.removeWaiterNotification(notification1);
            local.removeWaiterNotification(notification2);

           /* // Eccezione : notifica che non è presente
            WaiterNotification notificationDummy = new WaiterNotification(new Customer(11111,"Guido"));
            local.removeWaiterNotification(notificationDummy);*/

        /*    // Tolgo tutte le notifiche
            for(WaiterNotification notification : local.getWaiterNotificationList())
                local.removeWaiterNotification(notification);

            // Eccezione : no notifiche. Insieme vuoto.
           // notifications = local.getWaiterNotificationList();

        } catch (RoomStateException e) {
            e.printStackTrace();
        } catch (WaiterNotificationException e) {
            e.printStackTrace();
        } */


        /* TESTING MANAGER STORAGE */

        ManagerStorage.initializeStorage(this);

        //ManagerStorage.setName("Enrico");

        try {
            String name = ManagerStorage.getName();
            System.out.println(name);
        }catch(StorageException e){
            e.printStackTrace();
        }

        //ManagerStorage.setApplicationMode(ApplicationMode.CUSTOMER);

        try {
            ApplicationMode applicationMode = ManagerStorage.getApplicationMode();
        }catch(StorageException e){
            e.printStackTrace();
        }

        try {
            LocalDescription localDescription = ManagerStorage.getLocalDescription();
            System.out.println(localDescription);
        }catch (Exception e){
            e.printStackTrace();
        }


        Set<ManagerTable> tables = ManagerStorage.getTables();

        int MaxNotificationNumber = ManagerStorage.getMaxNotificationNumber();



        /* TESTING CUSTOMER STORAGE*/

        //CustomerStorage.setSensorMode(false);
        boolean sensorMode = CustomerStorage.getSensorMode();

        CustomerStorage.setNotificationMode(false);
        boolean notificationMode = CustomerStorage.getNotificationMode();

    }
}
