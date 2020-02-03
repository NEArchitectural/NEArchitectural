package com.nearchitectural;

import com.google.android.gms.maps.model.LatLng;

public class Location {
    private final String id;
    private String name;
    private String locationType;
    private String locationInfo;
    private double latitude;
    private double longitude;
    private boolean isWheelChairAccessible;
    private boolean isChildFriendly;
    private boolean hasCheapEntry;
    private boolean hasFreeEntry;
    // We also will need some image for the thumbnail

    public Location(String id, String name, String locationType, String locationInfo,
                    double latitude, double longitude, boolean isWheelChairAccessible,
                    boolean isChildFriendly, boolean hasCheapEntry, boolean hasFreeEntry) {
        this.id = id;
        this.name = name;
        this.locationType = locationType;
        this.locationInfo = locationInfo;
        this.latitude = latitude;
        this.longitude = longitude;
        this.isWheelChairAccessible = isWheelChairAccessible;
        this.isChildFriendly = isChildFriendly;
        this.hasCheapEntry = hasCheapEntry;
        this.hasFreeEntry = hasFreeEntry;
    }

    public Location(String id) {
        this.id = id;
    }

    public Location(String id, String name, String locationType, String locationInfo) {
        this.id = id;
        this.name = name;
        this.locationType = locationType;
        this.locationInfo = locationInfo;
    }

    public Location(String id, String name, String locationType) {
        this.id = id;
        this.name = name;
        this.locationType = locationType;
    }

    public Location(String id, String name, String locationType, LatLng coords) {
        this.id = id;
        this.name = name;
        this.locationType = locationType;
        this.latitude = coords.latitude;
        this.longitude = coords.longitude;
    }

    public Location(String id, String name, String placeType, LatLng coords, boolean wheelChairAccessible,
                    boolean childFriendly, boolean cheapEntry, boolean freeEntry) {
        this.id = id;
        this.name = name;
        this.locationType = placeType;
        this.latitude = coords.latitude;
        this.longitude = coords.longitude;
        this.isWheelChairAccessible = wheelChairAccessible;
        this.isChildFriendly = childFriendly;
        this.hasCheapEntry = cheapEntry;
        this.hasFreeEntry = freeEntry;
    }


    private void setName(String name) {
        this.name = name;
    }

    private void setLocationType(String placeType) {
        this.locationType = placeType;
    }

    private void setLocationInfo(String info) {
        this.locationInfo = info;
    }

    public String getName() {
        return name;
    }

    public String getLocationType() {
        return locationType;
    }

    public String getLocationInfo() {
        return locationInfo;
    }

    public String getId() {
        return id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public boolean isWheelChairAccessible() {
        return isWheelChairAccessible;
    }

    public boolean isChildFriendly() {
        return isChildFriendly;
    }

    public boolean hasCheapEntry() {
        return hasCheapEntry;
    }

    public boolean hasFreeEntry() {
        return hasFreeEntry;
    }

}
