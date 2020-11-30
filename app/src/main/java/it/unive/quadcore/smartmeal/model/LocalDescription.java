package it.unive.quadcore.smartmeal.model;

import android.location.Location;
import android.media.Image;

public abstract class LocalDescription {
    // immutable
    public abstract String getName();
    public abstract String getPresentation();
    public abstract Location getLocation();
    public abstract Image getImage();
    public abstract Menu getMenu();
}
