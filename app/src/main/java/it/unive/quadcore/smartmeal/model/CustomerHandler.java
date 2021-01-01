package it.unive.quadcore.smartmeal.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public abstract class CustomerHandler<C extends Customer> {

    @NonNull
    private final Map<String, C> customerMap;

    protected CustomerHandler() {
        this.customerMap = new HashMap<>();
    }

    protected synchronized void addCustomer(C customer) {
        if (containsCustomer(customer.getId())) {
            throw new IllegalStateException("A customer with the given id already exists");
        }
        customerMap.put(customer.getId(), customer);
    }

    public synchronized void removeCustomer(@NonNull C customer) {
        Objects.requireNonNull(customer);
        removeCustomer(customer.getId());
    }

    protected synchronized void removeCustomer(String customerId) {
        if (!containsCustomer(customerId)) {
            throw new IllegalStateException("A customer with the given id doesn't exist");
        }
        customerMap.remove(customerId);
    }

    protected C getCustomer(String customerId) {
        if (!containsCustomer(customerId)) {
            throw new IllegalStateException("A customer with the given id doesn't exist");
        }
        return customerMap.get(customerId);
    }

    protected boolean containsCustomer(String customerId) {
        return customerMap.containsKey(customerId);
    }

    public boolean containsCustomer(@Nullable C customer) {
        if(customer==null)
            return false;
        return customerMap.containsKey(customer.getId());
    }

    public abstract void removeAllCustomers();
}
