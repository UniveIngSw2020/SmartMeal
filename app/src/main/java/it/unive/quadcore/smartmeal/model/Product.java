package it.unive.quadcore.smartmeal.model;

import androidx.annotation.NonNull;

import java.util.Objects;

public class Product implements Comparable<Product>{
    private final String name;
    private final Money price;
    // Categoria del prodotto
    private final FoodCategory category;
    private final String description;

    public Product(String name, Money price, FoodCategory category, String description) {
        this.name = name;
        this.price = price;
        this.category = category;
        this.description = description;
    }

    public String getName() {
        return name;
    }
    public Money getPrice() {
        return price;
    }
    public String getDescription() {
        return description;
    }
    public FoodCategory getCategory() {
        return category;
    }

    @NonNull
    @Override
    public String toString() {
        return "Product{" +
                "name='" + name + '\'' +
                ", category=" + category +
                '}';
    }

    // Due prodotti sono uguali se hanno lo stesso nome e hanno la stessa categoria
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(name, product.name) &&
                category == product.category;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, category);
    }

    // Prodotti confrontabili rispetto alla categoria. Se hanno stessa categoria, sono confrontabili rispetto al nome
    // (lessicografico)
    @Override
    public int compareTo(Product o) {
        if(category==o.category)
            return name.compareTo(o.name);
        return category.compareTo(o.category);
    }
}
