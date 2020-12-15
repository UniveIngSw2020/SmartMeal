package it.unive.quadcore.smartmeal;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import java.util.Iterator;
import java.util.Set;

import it.unive.quadcore.smartmeal.local.Local;
import it.unive.quadcore.smartmeal.local.RoomStateException;
import it.unive.quadcore.smartmeal.local.TableException;
import it.unive.quadcore.smartmeal.model.Customer;
import it.unive.quadcore.smartmeal.model.ManagerTable;
import it.unive.quadcore.smartmeal.model.Table;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Local local = Local.getInstance();

        try {
            local.createRoom(this);
        }catch(RoomStateException e){
            e.printStackTrace();
        }

        local.testing();

        try {
            Set<ManagerTable> assignedTables = local.getAssignedTableList();
            Set<ManagerTable> freeTables = local.getFreeTableList();
            System.out.println("Tavoli occupati: "+assignedTables);
            System.out.println("Tavoli liberi: "+freeTables);

            Customer client = new Customer(1119,"Marco");
            Table table = freeTables.iterator().next();
            local.assignTable(client,table);

            Iterator<ManagerTable> it = assignedTables.iterator();
            it.next();
            Table table1 = it.next();
            local.freeTable(table1);

            Table table2 = assignedTables.iterator().next();
            Customer client1 = local.getCustomerByTable(table2);
            local.changeCustomerTable(client1,table1);

        }catch(RoomStateException e){
            e.printStackTrace();
        }
        catch(TableException e){
            e.printStackTrace();
        }

        try {
            local.closeRoom();
        }catch(RoomStateException e){
            e.printStackTrace();
        }

    }
}
