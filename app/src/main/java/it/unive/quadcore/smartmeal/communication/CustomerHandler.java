package it.unive.quadcore.smartmeal.communication;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CustomerHandler {
    public static class Customer {

        // id
        private final String id;
        // nome
        private final String name;

        public Customer(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        // Uguaglianza rispetto a id
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;      // TODO instanceof
            Customer customer = (Customer) o;
            return id.equals(customer.id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }

        @NonNull
        @Override
        public String toString() {
            return "Customer{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    '}';
        }
    }

    private static CustomerHandler instance;

    public synchronized static CustomerHandler getInstance() {
        if (instance == null) {
            instance = new CustomerHandler();
        }

        return instance;
    }

    @NonNull
    private final Map<String, Customer> customerMap;

    private CustomerHandler() {
        this.customerMap = new HashMap<>();
    }

    synchronized void addCustomer(String customerId, String customerName) {
        if (containsCustomer(customerId)) {
            throw new IllegalStateException("A customer with the given id already exists");
        }
        Customer customer = new Customer(customerId, customerName);
        customerMap.put(customer.getId(), customer);
    }

    synchronized void removeCustomer(Customer customer) {
        removeCustomer(customer.getId());
    }

    synchronized void removeCustomer(String customerId) {
        if (!containsCustomer(customerId)) {
            throw new IllegalStateException("A customer with the given id doesn't exist");
        }
        customerMap.remove(customerId);
    }

    synchronized Customer getCustomer(String customerId) {
        if (!containsCustomer(customerId)) {
            throw new IllegalStateException("A customer with the given id doesn't exist");
        }
        return customerMap.get(customerId);
    }

    synchronized boolean containsCustomer(String customerId) {
        return customerMap.containsKey(customerId);
    }
}
