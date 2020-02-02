package com.nearchitectural;

import java.io.Serializable;

public class Settings implements Serializable {

    // A place to store all the global settings and user preferences
    private static volatile Settings soleInstance;
    private static boolean wheelchairAccessNeeded;
    private static boolean childFriendlyNeeded;
    private static int fontSize;
    private double maxDistance;

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

    public static boolean isWheelchairAccessNeeded() {
        return wheelchairAccessNeeded;
    }

    public static void setWheelchairAccessNeeded(boolean wheelchairAccessNeeded) {
        Settings.wheelchairAccessNeeded = wheelchairAccessNeeded;
    }

    public static boolean isChildFriendlyNeeded() {
        return childFriendlyNeeded;
    }

    public static void setChildFriendlyNeeded(boolean childFriendlyNeeded) {
        Settings.childFriendlyNeeded = childFriendlyNeeded;
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
}