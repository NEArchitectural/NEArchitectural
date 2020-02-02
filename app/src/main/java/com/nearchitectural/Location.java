package com.nearchitectural;

import com.google.android.gms.maps.model.LatLng;

public class Location {
    private final String id;
    private String name;
    private String locationType;
    private String locationInfo;
    private double latitude;
    private double longitude;
    // We also will need some image for the thumbnail

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
}
