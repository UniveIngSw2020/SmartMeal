package it.unive.quadcore.smartmeal.local;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import it.unive.quadcore.smartmeal.model.Customer;
import it.unive.quadcore.smartmeal.model.CustomerHandler;

class LocalCustomerHandler extends CustomerHandler<LocalCustomerHandler.LocalCustomer> {
    public static class LocalCustomer extends Customer {
        private LocalCustomer(@NonNull String name) {
            super(LocalCustomerHandler.getNewId(), name);
        }
    }

    @NonNull
    private static Integer i=0;

    @NonNull
    private synchronized static String getNewId() {
        String newId = i.toString();
        i++;
        return newId;
    }

    @Nullable
    private static LocalCustomerHandler instance;

    public synchronized static LocalCustomerHandler getInstance() {
        if (instance == null) {
            instance = new LocalCustomerHandler();
        }
        return instance;
    }

    private LocalCustomerHandler() {}

    synchronized LocalCustomer addCustomer(@NonNull String customerName) {
        LocalCustomer localCustomer = new LocalCustomerHandler.LocalCustomer(customerName);
        addCustomer(localCustomer);
        return localCustomer;
    }

    @Override
    public synchronized boolean containsCustomer(@Nullable Customer customer) {
        if(!(customer instanceof LocalCustomer))
            return false;
        return super.containsCustomerHelper((LocalCustomer) customer);
    }
}

