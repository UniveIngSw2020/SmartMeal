package it.unive.quadcore.smartmeal.model;

import androidx.annotation.NonNull;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

// Classe che rappresenta Menu'. Nella versione più semplice è un insieme di prodotti. In generale è una mappa tra tipi di prodotto
// (tipi di pasto) e insieme di prodotti di quella categoria.
public class Menu {
    // SortedSet<Product>
    //private Set<Product> products;

    // Mappa tra le categorie di pasto e i prodotti
    private Map<FoodCategory,Set<Product>> categoriesProductsMap;

    public Menu(Set<Product> products){
        for(FoodCategory foodCategory : FoodCategory.values()) {
            Set<Product> productsOfThatCategory = new TreeSet<>();
            /*products.forEach(product->{
                if(product.getCategory()==foodCategory)
                    productsOfThatCategory.add(product);
            });*/
            for(Product product : products)
                if(product.getCategory()==foodCategory)
                    productsOfThatCategory.add(product);
            if(productsOfThatCategory.size()>0)
                categoriesProductsMap.put(foodCategory,productsOfThatCategory);
        }

    }

    public Map<FoodCategory,Set<Product>> getCategoriesProductsMap(){
        return categoriesProductsMap;
    }

    public Set<Product> getProducts(){
        Set<Product> res = new TreeSet<>();
        //categoryProductsMap.forEach(((foodCategory, products) -> res.addAll(products)));
        for(FoodCategory foodCategory : categoriesProductsMap.keySet())
            res.addAll(categoriesProductsMap.get(foodCategory));
        return res;
    }

    @NonNull
    @Override
    public String toString() {
        return "Menu{" +
                "products=" + categoriesProductsMap +
                '}';
    }
}
