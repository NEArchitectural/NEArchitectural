package com.nearchitectural.models;

import com.google.android.gms.maps.model.LatLng;
import com.nearchitectural.utilities.TagID;

import java.util.Calendar;
import java.util.Map;

/**author: Kristiyan Doykov, Joel Bell-Wilding
 * since: TODO: Fill in date
 * version: 1.0
 * purpose: Record-like class which stores information regarding a given location
 */
public class Location {

    /* TODO: Update Location constructor to use all attributes (potentially make all
        attributes but likes immutable) */
    private final String id; // Unique ID String for a given location
    private String name; // Location name
    private Calendar dateOpened; // Date location was opened initially
    private int likes; // Number of likes a location has
    private String locationType; // Type of location e.g. castle, bridge
    private String locationInfo; // Brief information about location
    private final double latitude; // Value of latitude
    private final double longitude; // Value of longitude
    private Map<TagID, Boolean> tags; // Mapping of Tag ID to active state
    private String thumbnailURL; // URL for thumbnail image used for displaying on UI
    private final String reportID; // Reference ID for corresponding report in database

    public Location(String id, String name, String placeType, LatLng coords,
                    Map<TagID, Boolean> tags, String thumbnailURL) {

        this.id = id;
        this.name = name;
        this.locationType = placeType;
        this.latitude = coords.latitude;
        this.longitude = coords.longitude;
        this.tags = tags;
        this.thumbnailURL = thumbnailURL;
        this.reportID = null;
    }

    // Getters for Location attributes
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

    public double getLongitude() {
        return longitude;
    }

    public Map<TagID, Boolean> getAllTags() {
        return tags;
    }

    public Boolean getTagValue(TagID tagID) {
        return tags.get(tagID);
    }

    public String getThumbnailURL() {
        return thumbnailURL;
    }

    public Calendar getDateOpened() {
        return dateOpened;
    }

    public int getLikes() {
        return likes;
    }

    public Map<TagID, Boolean> getTags() {
        return tags;
    }

    public String getReportID() {
        return reportID;
    }

    // Increments total likes by one
    public void addLike() {
        likes++;
    }

    // Decrements total likes by one if greater than 0
    public boolean removeLike() {
        if (likes == 0) {
            return false;
        }
        likes--;
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Location model = (Location) o;

        if (!id.equals(model.id)) return false;
        return locationType != null ? locationType.equals(model.locationType) : model.getLocationType() == null
                && name != null ? name.equals(model.name) : model.name == null;
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + (name != null ? name.hashCode() : 0) +
                (locationType != null ? locationType.hashCode() : 0);
        return result;
    }

}
