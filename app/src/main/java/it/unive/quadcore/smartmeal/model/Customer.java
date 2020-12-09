package it.unive.quadcore.smartmeal.model;

import java.util.Objects;

// Creato dentro ManagerCommunication. In pratica usato solo lato gestore.
public class Customer {

    // id
    private final int id;
    // nome
    private final String name;

    public Customer(int id,String name){
        this.id = id;
        this.name = name;
    }

    public int getId(){
        return id;
    }

    public String getName(){
        return name;
    }

    // Uguaglianza rispetto a id
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return id == customer.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
