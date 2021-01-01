package it.unive.quadcore.smartmeal.local;

import androidx.annotation.NonNull;

import it.unive.quadcore.smartmeal.model.Customer;
import it.unive.quadcore.smartmeal.model.CustomerHandler;

public class LocalCustomerHandler extends CustomerHandler<LocalCustomerHandler.LocalCustomer> {
    public static class LocalCustomer extends Customer {
        private LocalCustomer(@NonNull String name) {
            super(LocalCustomerHandler.getNewId(), name);
        }
    }

    private static Integer i=0;

    @NonNull
    private static String getNewId() {
        String newId = i.toString();
        i++;
        return newId;
    }

    private static LocalCustomerHandler instance;

    public synchronized static LocalCustomerHandler getInstance() {
        if (instance == null) {
            instance = new LocalCustomerHandler();
        }
        return instance;
    }

    private LocalCustomerHandler() {}

    synchronized void addCustomer(@NonNull String customerName) {
        addCustomer(new LocalCustomerHandler.LocalCustomer(customerName));
    }

    @Override
    public void removeAllCustomers() {
        // TODO rimuovere tutti i customer
    }
}

