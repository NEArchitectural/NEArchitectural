package com.nearchitectural.utilities;

import android.location.Location;
import java.util.Comparator;

/** @author Joel Bell-Wilding
 *  @version 1.0
 *  @since 28/01/20
 *  purpose: Compare locations by distance to user
 */
public class ClosestToUserComparator implements Comparator<LocationInfo> {

    private Location userLocation; // User's current location for distance comparison

    public ClosestToUserComparator(Location userLocation) {
        this.userLocation = userLocation;
    }

    // Compares the distance between two locations (in metres), returns to the nearest whole meter
    @Override
    public int compare(LocationInfo firstLocation, LocationInfo secondLocation) {

        double distanceToFirstLoc = userLocation.distanceTo(firstLocation.getCoordinates());
        double distanceToSecondLoc = userLocation.distanceTo((secondLocation.getCoordinates()));

        return (int) (distanceToSecondLoc - distanceToFirstLoc);
    }
}
