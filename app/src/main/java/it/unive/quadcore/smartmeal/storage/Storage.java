package it.unive.quadcore.smartmeal.storage;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import android.location.Location;
import android.media.Image;
import android.preference.PreferenceManager;

import java.util.Set;
import java.util.TreeSet;

import it.unive.quadcore.smartmeal.R;
import it.unive.quadcore.smartmeal.model.FoodCategory;
import it.unive.quadcore.smartmeal.model.LocalDescription;
import it.unive.quadcore.smartmeal.model.Menu;
import it.unive.quadcore.smartmeal.model.Money;
import it.unive.quadcore.smartmeal.model.Product;

class Storage {

    // TODO : possibilità di fare Storage oggetto singletone


    protected static boolean initialized = false;

    protected static Activity activity ; //

    // Shared Preferences. La prima di deafult, ovvero si mettono i settings dell'applicazione. La seconda di uso generico.
    protected static SharedPreferences defaultSharedPreferences;
    protected static SharedPreferences sharedPreferences;
    /**
     * Rende non instanziabile questa classe.
     */
    Storage() {}

    public static void initializeStorage(Activity activity){ // Alter ego di getInstance
        if(initialized)
            throw new StorageException("The storage has alredy been initialized");

        Storage.activity = activity;

        // Shared Preference di deafult. Usata per i settings dell'applicazione.
        defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);

        // Shared Preference di uso generico.
        sharedPreferences = activity.getSharedPreferences("SharedPreference" , Context.MODE_PRIVATE); // TODO : rimpiazzare con stringa di res

        initialized=true;
    }

    public static ApplicationMode getApplicationMode() {
        if(!initialized)
            throw new StorageException("The storage hasn't been initialize yet");

        String applicationModeString = defaultSharedPreferences.getString("ApplicationMode",null); // TODO : rimpiazzare con stringa di res
        if(applicationModeString==null)
            throw new StorageException("The application mode was not found in storage");

        ApplicationMode applicationMode ;
        try{
            applicationMode = ApplicationMode.valueOf(applicationModeString);
            return applicationMode;
        }catch(IllegalArgumentException e) {
            throw new StorageException("The storage contains an invalid application mode");
        }
    }

    public static void setApplicationMode(ApplicationMode applicationMode) {
        if(!initialized)
            throw new StorageException("The storage hasn't been initialize yet");

        SharedPreferences.Editor editor = defaultSharedPreferences.edit();

        String applicationModeString = applicationMode.name(); // toString in alternativa
        editor.putString("ApplicationMode",applicationModeString); // TODO : rimpiazzare con stringa di res
        editor.apply();
    }



    public static String getName() {
        if(!initialized)
            throw new StorageException("The storage hasn't been initialize yet");

        String name = sharedPreferences.getString("Name",null); // TODO : rimpiazzare con stringa di res
        if(name==null)
            throw new StorageException("The name was not found in storage");

        return name;
    }

    public static void setName(String name) {
        if(!initialized)
            throw new StorageException("The storage hasn't been initialize yet");

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Name", name); // TODO : rimpiazzare con stringa di res
        editor.apply();
    }



    public static LocalDescription getLocalDescription() {
        if(!initialized)
            throw new StorageException("The storage hasn't been initialize yet");

        // Creiamo i prodotti
        Set<Product> products = generateProducts();

        // Creiamo il menu
        Menu menu = new Menu(products);

        //Creiamo Local Description
        String presentation = "A delicious and friendly pub in a boat-like location";
        Location location = new Location("");
        location.setLatitude(45.56121046165753d);
        location.setLongitude(12.237328642473326);
        //int imageID = R.drawable.localpicture;

        return new LocalDescription("The boat restourant",presentation, location/*, imageID*/ ,menu);

    }

    private static Set<Product> generateProducts(){
        Set<Product> products = new TreeSet<>();

        Product product = new Product("Tomato soup",new Money(200), FoodCategory.STARTERS,"");
        products.add(product);
        product = new Product("French onion soup",new Money(250), FoodCategory.STARTERS,"");
        products.add(product);
        product = new Product("Tomato salad",new Money(290), FoodCategory.SALADS,"");
        products.add(product);
        product = new Product("Chicken salad",new Money(330), FoodCategory.SALADS,"");
        products.add(product);
        product = new Product("German sausage and chips",new Money(650), FoodCategory.MAIN_COURSES,"");
        products.add(product);
        product = new Product("Grilled fish and potatoes",new Money(625), FoodCategory.MAIN_COURSES,"");
        products.add(product);
        product = new Product("Italian cheese and tomato pizza",new Money(480), FoodCategory.MAIN_COURSES,"");
        products.add(product);
        product = new Product("Thai chicken and rice",new Money(590), FoodCategory.MAIN_COURSES,"");
        products.add(product);
        product = new Product("Vegetable pasta",new Money(480), FoodCategory.MAIN_COURSES,"");
        products.add(product);
        product = new Product("Roast chicken and potatoes",new Money(590), FoodCategory.MAIN_COURSES,"");
        products.add(product);
        product = new Product("Cheeseburger",new Money(320), FoodCategory.SIDE_DISHES,"");
        products.add(product);
        product = new Product("Vegetable omelette",new Money(325), FoodCategory.SIDE_DISHES,"");
        products.add(product);
        product = new Product("Cheese and tomato sandwich",new Money(320), FoodCategory.SIDE_DISHES,"");
        products.add(product);
        product = new Product("Burger",new Money(290), FoodCategory.SIDE_DISHES,"");
        products.add(product);
        product = new Product("Chicken sandwich",new Money(350), FoodCategory.SIDE_DISHES,"");
        products.add(product);
        product = new Product("Cheese omelette",new Money(350), FoodCategory.SIDE_DISHES,"");
        products.add(product);
        product = new Product("Mineral water",new Money(100), FoodCategory.DRINKS,"");
        products.add(product);
        product = new Product("Fresh orange juice",new Money(120), FoodCategory.DRINKS,"");
        products.add(product);
        product = new Product("Soft drinks",new Money(130), FoodCategory.DRINKS,"");
        products.add(product);
        product = new Product("English tea",new Money(90), FoodCategory.DRINKS,"");
        products.add(product);
        product = new Product("Irish cream coffee",new Money(90), FoodCategory.DRINKS,"");
        products.add(product);
        product = new Product("Fruit salad and cream",new Money(220), FoodCategory.DESSERTS,"");
        products.add(product);
        product = new Product("Ice cream",new Money(200), FoodCategory.DESSERTS,"");
        products.add(product);
        product = new Product("Lemon cake",new Money(220), FoodCategory.DESSERTS,"");
        products.add(product);
        product = new Product("Cheese and biscuit",new Money(220), FoodCategory.DESSERTS,"");
        products.add(product);
        product = new Product("Chocolate chake",new Money(250), FoodCategory.DESSERTS,"");
        products.add(product);

        return products;
    }
}
