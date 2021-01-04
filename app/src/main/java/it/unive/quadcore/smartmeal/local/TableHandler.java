package it.unive.quadcore.smartmeal.local;

import org.jetbrains.annotations.NotNull;

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
// Per ora gestisco con synchronized. Si potrebbe usare approcio più efficente tramite Lock lettori-scrittori.

// Visibile solo alla classe Locale (private o package-private)
class TableHandler {
    // Mappa clienti-tavoli
    @NotNull
    private final Map<Customer,ManagerTable> customerTableMap ;

    // Lista di tavoli liberi (usata come cache)
    @NotNull
    private final SortedSet<ManagerTable> freeTableList ;

    /* Mappa tavoli-tavoli gestore.
       Serve a 2 cose:
            - Tenere i soli tavoli significativi per il locale
            - Evitare casting a ManagerTable
     */
    @NotNull
    private final Map<Table,ManagerTable> tablesMap;

    // Costruttore con visibilità package
    TableHandler(){
        customerTableMap = new HashMap<>();

        // Prendo tutti i tavoli del locale
        Set<ManagerTable> tables = ManagerStorage.getTables();

        // Tutti i tavoli sono inizialmente liberi
        freeTableList = new TreeSet<>(tables);

        tablesMap = new HashMap<>();
        //tables.forEach(managerTable -> tableMap.put(managerTable,managerTable));
        for(ManagerTable managerTable : tables)
            tablesMap.put(managerTable, managerTable);

    }


    // Ritorna la lista di tavoli liberi
    synchronized TreeSet<ManagerTable> getFreeTableList() { // SortedSet
        // La lista di tavoli liberi è vuota
        /*if(freeTableList==null || freeTableList.size()==0)
            throw new TableException("There aren't free tables");*/

        // Ritorna lista tavoli liberi (una copia)
        return new TreeSet<>(freeTableList) ;
    }

    // Ritorna la lista di tavoli occupati
    synchronized SortedSet<ManagerTable> getAssignedTableList() {

        // Tutti i tavoli nella mappa customer-tables
        SortedSet<ManagerTable> assignedTableList = new TreeSet<>(customerTableMap.values());

        // Non c'è nessun tavolo occupato
       /* if(assignedTableList.size() == 0)
            throw new TableException("There aren't assigned tables");*/

        return assignedTableList ;
    }

    // Cambia il tavolo associato ad un dato cliente
    synchronized void changeCustomerTable(Customer customer, Table newTable) throws TableException {
        /*
        // Il tavolo specificato non è un tavolo corretto per il locale
        if(!tableMap.containsKey(newTable))
            throw new TableException("The selected table doesn't exist");
        ManagerTable managerTable = tableMap.get(newTable) ; // Versione ManagerTable di newTable

        // Cliente non ha nessun tavolo
        if(!customerTableMap.containsKey(customer))
            throw new TableException("This customer doesn't have alredy a table");

        // Tavolo già occupato
        if(!freeTableList.contains(managerTable))
            throw new TableException("This table is alredy assigned");

        // Vecchio tavolo di quel cliente
        ManagerTable oldTable = customerTableMap.get(customer);

        // Cambio tavolo associato al cliente
        customerTableMap.put(customer,managerTable);

        freeTableList.add(oldTable); // Ora tavolo vecchio è libero
        freeTableList.remove(managerTable); // Tavolo specificato ora è occupato

         */


        // Siccome metodo è syncrhonized, posso anche fare più semplicemente così :
        // QUESTI 2 CONTROLLI SONO GIA' FATTI IN ASSIGNTABLE FATTO IN FONDO. PERO' DEVO FARLI ORA, SE NO LIBERO UN TAVOLO CHE NON
        // AVREI DOVUTO LIBERARE
        if(!tablesMap.containsKey(newTable)) // Tavolo specificato non esiste
            throw new NoSuchTableException("The selected table doesn't exist");
        ManagerTable managerTable = tablesMap.get(newTable) ;

        if(!freeTableList.contains(managerTable)) // Tavolo già occupato
            throw new AlreadyOccupiedTableException("This table is already occupied");

        ManagerTable oldTable = getTable(customer);
        freeTable(oldTable);
        assignTable(customer, newTable);



    }

    // Assegna un tavolo ad un cliente senza tavolo
    synchronized void assignTable(Customer customer, Table table) throws TableException {

        // Il tavolo specificato non è un tavolo corretto per il locale
        if(!tablesMap.containsKey(table))
            throw new NoSuchTableException("The selected table doesn't exist");
        ManagerTable managerTable = tablesMap.get(table) ;

        // Tavolo già occupato
        if(!freeTableList.contains(managerTable))
            throw new AlreadyOccupiedTableException("This table is already occupied");

        // Cliente ha già un tavolo
        if(customerTableMap.containsKey(customer))
            throw new AlreadyAssignedTableException("This customer already has a table");

        // Assegno tavolo al cliente
        customerTableMap.put(customer, managerTable);

        // Tavolo specificato non è più libero
        freeTableList.remove(managerTable);
    }

    // Libera un tavolo
    synchronized void freeTable(Table table) throws TableException {

        /* // Il tavolo specificato non è un tavolo corretto per il locale
        if(!tablesMap.containsKey(table))
            throw new TableException("The selected table doesn't exist");
        ManagerTable managerTable = tablesMap.get(table) ;

        // Tavolo non è occupato da nessun cliente
        if(freeTableList.contains(managerTable))
            throw new TableException("This table isn't assigned");*/

        Customer customer = getCustomer(table); // Controllo se tavolo esiste e se è occupato
        ManagerTable managerTable = tablesMap.get(table) ;

        // Rimuovo questa associazione dalla mappa
        customerTableMap.remove(customer);

        // Il tavolo ora è libero
        freeTableList.add(managerTable);
    }

    // Ritorna il tavolo occupato da un certo cliente
    // ManagerTable o Table ?
    synchronized ManagerTable getTable(Customer customer) throws TableException {
        // Cliente non ha un tavolo
        if(!customerTableMap.containsKey(customer))
            throw new TableException("This customer doesn't have a table assigned"); //TODO: Forse null? altrimenti serve un metodo boolean hasTable(Customer customer)

        // Ritorno tavolo occupato (posso ritornarlo direttamente senza copiarlo perchè è immutable)
        return customerTableMap.get(customer);
    }

    // Ritorna il cliente che occupa un certo tavolo
    synchronized Customer getCustomer(Table table) throws TableException {
        // Il tavolo specificato non è un tavolo corretto per il locale
        if(!tablesMap.containsKey(table))
            throw new NoSuchTableException("The selected table doesn't exist");
        ManagerTable managerTable = tablesMap.get(table) ;

        // Tavolo non è occupato da nessun cliente
        if(freeTableList.contains(managerTable))
            throw new TableException("This table isn't assigned"); //TODO: Forse null? altrimenti serve un metodo boolean hasCustomer(Table table)

        /*Customer customer = null;
        for(Customer supp : customerTableMap.keySet()){
            if(customerTableMap.get(supp).equals(managerTable))
                customer = supp ;
        }*/
        // Itero la mappa per trovare cliente con quel tavolo associato
        // Uso Iterator e non smart for perchè così posso fermarmi anticipatamente (più efficente)
        Iterator<Customer> it = customerTableMap.keySet().iterator();
        boolean found = false;
        Customer customer = null;
        while(it.hasNext() && !found){
            customer = it.next();
            if(customerTableMap.get(customer).equals(managerTable))
                found = true;
        }

        return customer;
    }
}
