package com.nearchitectural.utilities;

/*
 * Author:  Kristiyan Doykov
 * Since:   20/12/19
 * Version: 1.0
 * Purpose: Utility class which calculates the distance between two points on
 *          earth (i.e. location to location, or user position to location)
 */
public class DistanceCalculator {

    private final static int KILOMETER_CONVERSION = 1000; // Conversion to kilometer from meters
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

        double distance = EARTH_RADIUS * tempDistTwo * KILOMETER_CONVERSION; // convert to meters

        distance = Math.pow(distance, 2);

        return Math.sqrt(distance);
    }
}
