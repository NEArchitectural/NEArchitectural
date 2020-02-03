package com.nearchitectural.utilities;

import android.location.Location;

/** @author Joel Bell-Wilding
 *  @version 1.0
 *  @since 03/02/20
 *  purpose: Store, update and manipulate information regarding the user's location
 */
public class UserLocation {

    private Location userCoordinates; // Location object storing the user's (last known) longitude and latitude
    public double DEFAULT_LONGITUDE = 54.9738; // Default longitude (based at Grey's Monument, Newcastle)
    public double DEFAULT_LATITUDE = 1.6132; // Default latitude (based at Grey's Monument, Newcastle)

    // No parameter constructor - sets user latitude and longitude to default values (based at Grey's Monument, Newcastle)
    public UserLocation() {
        userCoordinates = new Location("Default Location");
        userCoordinates.setLatitude(DEFAULT_LATITUDE);
        userCoordinates.setLongitude(DEFAULT_LONGITUDE);
    }

    // Returns the distance between a user and a provided location
    public double distanceFromUser(LocationInfo location) {
        return userCoordinates.distanceTo(location.getCoordinates());
    }
}
