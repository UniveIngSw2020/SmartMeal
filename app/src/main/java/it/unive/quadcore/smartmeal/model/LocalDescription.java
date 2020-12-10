package it.unive.quadcore.smartmeal.model;

import android.location.Location;
import android.media.Image;

public class LocalDescription {

    private final String name;
    private final String presentation ;
    private final Location location;
    private final Image image;
    private final Menu menu;

    public LocalDescription(String name,String presentation,Location location,Image image,Menu menu){
        this.name = name;
        this.presentation = presentation;
        this.location = location;
        this.image = image;
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
    public Image getImage(){
        return image;
    }
    public Menu getMenu(){
        return menu;
    }

    @Override
    public String toString() {
        return "LocalDescription{" +
                "name='" + name + '\'' +
                ", presentation='" + presentation + '\'' +
                ", location=" + location +
                ", image=" + image +
                ", menu=" + menu +
                '}';
    }
}
