package it.unive.quadcore.smartmeal.local;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import it.unive.quadcore.smartmeal.model.Customer;
import it.unive.quadcore.smartmeal.model.ManagerTable;
import it.unive.quadcore.smartmeal.model.Table;
import it.unive.quadcore.smartmeal.storage.ManagerStorage;

// TODO : gestione RACE CONDITION

// Visibile solo alla classe Locale (private o package-private)
public abstract class TableHandler {
    private Map<Customer,ManagerTable> customerTableMap ;

    private TreeSet<ManagerTable> freeTableList ; // TODO : SortedSet

    // eventuale mappa tra Customer e Table (o viceversa)

    TableHandler(){
        customerTableMap = new HashMap<>();

        Set<ManagerTable> tables = ManagerStorage.getTables();

        freeTableList = new TreeSet<>(tables);
    }

    // TODO : pensare come risolvere problema generic
    //public abstract Set<ManagerTable> getFreeManagerTableList();
    public TreeSet<ManagerTable> getFreeTableList(){

        return new TreeSet<>(freeTableList) ;
    }
    public void changeCustomerTable(Customer customer, Table newTable) throws TableException {
        ManagerTable manTable = (ManagerTable)newTable ;

        if(!customerTableMap.containsKey(customer))
            throw new TableException("This customer has alredy a table");

        if(!freeTableList.contains(manTable))
            throw new TableException("This table is alredy assigned");

        ManagerTable oldTable = customerTableMap.get(customer);
        customerTableMap.put(customer,manTable);

        freeTableList.add(oldTable);
        freeTableList.remove(manTable);
    }
    // TODO : parlare dei tipi Table e ManagerTable
    public void assignTable(Customer customer, Table table) throws TableException {
        ManagerTable manTable = (ManagerTable)table ;

        if(!freeTableList.contains(manTable))
            throw new TableException("This table is alredy assigned");

        if(customerTableMap.containsKey(customer))
            throw new TableException("This customer has alredy a table");

        customerTableMap.put(customer, manTable);

        freeTableList.remove(table);
    }
    public void freeTable(Table table) throws TableException {
        ManagerTable managerTable = (ManagerTable)table;
        if(freeTableList.contains(managerTable))
            throw new TableException("This table isn't assigned");

        /*Customer customer = null;
        for(Customer supp : customerTableMap.keySet()){
            if(customerTableMap.get(supp).equals(managerTable))
                customer = supp ;
        }*/
        Iterator<Customer> it = customerTableMap.keySet().iterator();
        boolean found = false;
        Customer customer = null;
        while(it.hasNext() && !found){
            customer = it.next();
            if(customerTableMap.get(customer).equals(managerTable))
                found = true;
        }
        customerTableMap.remove(customer);

        freeTableList.add(managerTable);
    }
    public Table getTable(Customer customer) throws TableException {
        if(!customerTableMap.containsKey(customer))
            throw new TableException("This customer doesn't have a table assigned");

        return customerTableMap.get(customer);
    }
}
