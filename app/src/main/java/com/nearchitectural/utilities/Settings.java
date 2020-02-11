package com.nearchitectural.utilities;

import java.io.Serializable;

/**author: Kristiyan Doykov
 * since: TODO: Fill in date
 * version: 1.0
 * purpose: Settings singleton class which stores and allows manipulation of application-wide settings
 */
public class Settings implements Serializable {

    // A place to store all the global settings and user preferences
    private static volatile Settings soleInstance;
    private static TagMapper activeTags;
    private static int fontSize;
    private double maxDistance;
    private boolean mLocationPermissionsGranted;

    //private constructor
    private Settings() {

        //Prevent form the reflection api.
        if (soleInstance != null) {
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        }
    }

    // Used to get an instance of the Singleton and change/retrieve settings
    public static Settings getInstance() {
        if (soleInstance == null) { //if there is no instance available... create new one
            synchronized (Settings.class) {
                if (soleInstance == null) soleInstance = new Settings();
            }
        }

        return soleInstance;
    }

    /* Use these methods to update or get a specific setting */

    public static boolean getTagValue(TagID tag) {
        return activeTags.getTagValuesMap().get(tag);
    }

    public static void setTagValue(TagID tag, boolean isActive) {
        Settings.activeTags.addTagToMapper(tag, isActive);
    }

    public static int getFontSize() {
        return fontSize;
    }

    public static void setFontSize(int fontSize) {
        Settings.fontSize = fontSize;
    }

    /* Make singleton free from serialize and deserialize operation. In other words guard against
    multiple instances of this class */
    protected Settings readResolve() {
        return getInstance();
    }

    public double getMaxDistance() {
        return maxDistance;
    }

    public void setMaxDistance(double maxDistance) {
        this.maxDistance = maxDistance;
    }

    public boolean ismLocationPermissionsGranted() {
        return mLocationPermissionsGranted;
    }

    public void setmLocationPermissionsGranted(boolean mLocationPermissionsGranted) {
        this.mLocationPermissionsGranted = mLocationPermissionsGranted;
    }
}