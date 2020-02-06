package com.nearchitectural.models;

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
    private String thumbnailURL;
    // We also will need some image for the thumbnail

    public Location(String id, String name, String placeType, LatLng coords,
                    boolean wheelChairAccessible, boolean childFriendly,
                    boolean cheapEntry, boolean freeEntry, String thumbnailAddress) {
        this.id = id;
        this.name = name;
        this.locationType = placeType;
        this.latitude = coords.latitude;
        this.longitude = coords.longitude;
        this.isWheelChairAccessible = wheelChairAccessible;
        this.isChildFriendly = childFriendly;
        this.hasCheapEntry = cheapEntry;
        this.hasFreeEntry = freeEntry;
        this.thumbnailURL = thumbnailAddress;
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

    public String getThumbnailURL() {
        return thumbnailURL;
    }

    public void setThumbnailURL(String thumbnailURL) {
        this.thumbnailURL = thumbnailURL;
    }
}
