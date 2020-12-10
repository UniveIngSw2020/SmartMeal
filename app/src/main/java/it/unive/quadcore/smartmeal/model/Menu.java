package it.unive.quadcore.smartmeal.model;

import java.util.Set;

import it.unive.quadcore.smartmeal.model.Product;

public class Menu {
    // SortedSet<Product>
    private Set<Product> products;

    public Menu(Set<Product> products){
        this.products = products ;
    }
    public Set<Product> getProducts(){
        return products;
    }

    @Override
    public String toString() {
        return "Menu{" +
                "products=" + products +
                '}';
    }
}
