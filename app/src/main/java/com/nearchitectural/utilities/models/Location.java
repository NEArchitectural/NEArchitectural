package com.nearchitectural.utilities.models;

import com.nearchitectural.utilities.TagID;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/* Author:  Kristiyan Doykov, Joel Bell-Wilding
 * Since:   12/12/19
 * Version: 1.0
 * Purpose: Record-like class which stores information regarding a given location
 */
public class Location {

    private final String id; // Unique ID String for a given location
    private String name; // Location name
    private int yearOpened; // year location was opened initially
    private String yearOpenedString; // String representation of year opened
    private int likes; // Number of likes a location has
    private String type; // Type of location e.g. castle, bridge
    private String summary; // Brief information about location
    private final double latitude; // Latitude of location
    private final double longitude; // Longitude of location
    private Map<TagID, Boolean> tags; // Mapping of Tag ID to active state
    private String thumbnailURL; // URL for thumbnail image used for displaying on UI
    private final String reportID; // Reference ID for corresponding report in database

    public Location(String id, String name, int yearOpened, int likes, String type, String summary, double latitude,
                    double longitude, Map<TagID, Boolean> tags, String thumbnailURL, String reportID) {

        this.id = id;
        this.name = name;
        this.yearOpened = yearOpened;
        // Setting yearOpened String to BC or AC depending on if year opened is negative
        this.yearOpenedString = yearOpened < 0 ? yearOpened + " BC" : yearOpened < 1000 ? yearOpened + " AD" : String.valueOf(yearOpened);
        this.likes = likes;
        this.type = type;
        this.summary = summary;
        this.latitude = latitude;
        this.longitude = longitude;
        this.tags = tags;
        this.thumbnailURL = thumbnailURL;
        this.reportID = reportID;
    }

    // Getters for Location attributes
    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getSummary() {
        return summary;
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

    // Returns only active tags from tags list
    public List<TagID> getActiveTags() {
        List<TagID> activeTags = new ArrayList<>();
        for (TagID tag : tags.keySet()) {
            if (tags.get(tag))
                activeTags.add(tag);
        }
        return activeTags;
    }

    // Returns a boolean representing if a tag is active or not when provided with TagID
    public Boolean getTagValue(TagID tagID) {
        return tags.get(tagID);
    }

    public String getThumbnailURL() {
        return thumbnailURL;
    }

    public int getYearOpened() {
        return yearOpened;
    }

    public String getYearOpenedString() {
        return yearOpenedString;
    }

    public int getLikes() {
        return likes;
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
        return type != null ? type.equals(model.type) : model.getType() == null
                && name != null ? name.equals(model.name) : model.name == null;
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + (name != null ? name.hashCode() : 0) +
                (type != null ? type.hashCode() : 0);
        return result;
    }

}
