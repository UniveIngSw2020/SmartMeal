package it.unive.quadcore.smartmeal.communication;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import it.unive.quadcore.smartmeal.model.Customer;
import it.unive.quadcore.smartmeal.model.CustomerHandler;

class RemoteCustomerHandler extends CustomerHandler<RemoteCustomerHandler.RemoteCustomer> {
    public static class RemoteCustomer extends Customer {
        private RemoteCustomer(String id, String name) {
            super(id, name);
        }
    }

    private static RemoteCustomerHandler instance;

    public synchronized static RemoteCustomerHandler getInstance() {
        if (instance == null) {
            instance = new RemoteCustomerHandler();
        }
        return instance;
    }

    private RemoteCustomerHandler() {}

    synchronized void addCustomer(@NonNull String customerId, @NonNull String customerName) {
        addCustomer(new RemoteCustomer(customerId, customerName));
    }

    @Override
    public synchronized void removeCustomer(String customerId) {
        super.removeCustomer(customerId);
    }

    @Override
    public RemoteCustomer getCustomer(String customerId) {
        return super.getCustomer(customerId);
    }

    @Override
    public boolean containsCustomer(String customerId) {
        return super.containsCustomer(customerId);
    }

    @Override
    public void removeAllCustomers() {
        // TODO rimuovere tutti i customer
    }
}
