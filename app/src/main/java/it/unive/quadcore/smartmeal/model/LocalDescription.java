package it.unive.quadcore.smartmeal.model;

import android.location.Location;
import android.media.Image;

import androidx.annotation.NonNull;

public class LocalDescription {

    private final String name;
    private final String presentation ;
    private final Location location;
    //private final int imageID; // Immagine non dovrebbe servire : dovrebbe essere direttamente mostrata nell'interfaccia grafica tramite xml
    private final Menu menu;

    public LocalDescription(String name,String presentation,Location location/*,int imageID*/,Menu menu){
        this.name = name;
        this.presentation = presentation;
        this.location = location;
        //this.imageID = imageID;
        this.menu = menu;
    }
    public String getName(){
        return name;
    }
    public String getPresentation(){
        return presentation;
    }
    public Location getLocation(){
        return location;
    }
   /* public int getImageID(){
        return imageID;
    }*/
    public Menu getMenu(){
        return menu;
    }

    @NonNull
    @Override
    public String toString() {
        return "LocalDescription{" +
                "name='" + name + '\'' +
                ", presentation='" + presentation + '\'' +
                ", location=" + location +
               // ", image=" + imageID +
                ", menu=" + menu +
                '}';
    }
}
