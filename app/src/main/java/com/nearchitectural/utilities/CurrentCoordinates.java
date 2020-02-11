package com.nearchitectural.utilities;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

/**author: Kristiyan Doykov
 * since: TODO: Fill in date
 * version: 1.0
 * purpose: Singleton class which stores and manipulates the user's current coordinates
 */
public class CurrentCoordinates implements Serializable {

    private static volatile CurrentCoordinates soleInstance;
    private static LatLng coords;

    //private constructor
    private CurrentCoordinates(){

        //Prevent form the reflection api.
        if (soleInstance != null){
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        }
    }

    public static CurrentCoordinates getInstance() {
        if (soleInstance == null) { //if there is no instance available... create new one
            synchronized (CurrentCoordinates.class) {
                if (soleInstance == null) soleInstance = new CurrentCoordinates();
            }
        }

        return soleInstance;
    }

    public static LatLng getCoords() {
        return new LatLng(coords.latitude,coords.longitude);
    }

    public static void setCoords(LatLng coords) {
        CurrentCoordinates.coords = coords;
    }

    //Make singleton from serialize and deserialize operation.
    protected CurrentCoordinates readResolve() {
        return getInstance();
    }
}
