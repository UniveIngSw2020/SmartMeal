package it.unive.quadcore.smartmeal.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

// Classe che gestisce i clienti del locale
public abstract class CustomerHandler<C extends Customer> {

    @NonNull
    private final Map<String, C> customerMap;

    protected CustomerHandler() {
        this.customerMap = new HashMap<>();
    }

    protected synchronized void addCustomer(@NonNull C customer) {
        if (containsCustomer(customer.getId())) {
            throw new IllegalStateException("A customer with the given id already exists");
        }
        customerMap.put(customer.getId(), customer);
    }

    public synchronized void removeCustomer(@NonNull C customer) {
        Objects.requireNonNull(customer);
        removeCustomer(customer.getId());
    }

    public synchronized void removeCustomer(String customerId) {
        if (!containsCustomer(customerId)) {
            throw new IllegalStateException("A customer with the given id doesn't exist");
        }
        customerMap.remove(customerId);
    }

    public synchronized C getCustomer(String customerId) {
        if (!containsCustomer(customerId)) {
            throw new IllegalStateException("A customer with the given id doesn't exist");
        }
        return customerMap.get(customerId);
    }

    public synchronized boolean containsCustomer(String customerId) {
        return customerMap.containsKey(customerId);
    }

    protected synchronized boolean containsCustomerHelper(@Nullable C customer) {
        return customer!=null && customerMap.containsKey(customer.getId());
    }

    public abstract boolean containsCustomer(@Nullable Customer customer);

    public synchronized void removeAllCustomers() {
        customerMap.clear();
    }
}
