package com.nearchitectural.utilities;

/*
 * Author:  Shaun Kurzyk
 * Since:   20/12/19
 * Version: 1.1
 * Purpose: Utility class which calculates the distance between two points on
 *          earth (i.e. location to location, or user position to location)
 */
public class DistanceCalculator {

    private final static int EARTH_RADIUS = 6371; // Radius of the earth

    /* Calculates the distance between two points on the map (using their latitude and
     * longitude. Code fragment taken and modified from the following web-page:
     * https://stackoverflow.com/questions/3694380/calculating-distance-between-two-points-using-latitude-longitude
     */
    public static double calculateDistance(double lat1, double lat2, double lon1, double lon2) {

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double tempDistOne = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double tempDistTwo = 2 * Math.atan2(Math.sqrt(tempDistOne), Math.sqrt(1 - tempDistOne));
        // Return distance in meters  (regardless of distance unit setting)
        return EARTH_RADIUS * tempDistTwo * Settings.DistanceUnit.KILOMETRE.getConversionRate();
    }
}
