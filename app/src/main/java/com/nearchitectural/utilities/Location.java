package com.nearchitectural.utilities;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;

/** @author Joel Bell-Wilding
 *  @version 1.0
 *  @since 28/01/20
 *  purpose: Record-like class storing information regarding a Location (building/attraction)
 *  for access by front-end activities and LocationList class
 */
public class Location {

    private final String locationID; // Unique location identifier
    private final String name; // Name of Location
    private final Calendar dateOpened; // Date location was opened initially
    private int likes; // Number of likes a location has (initially read in from database)
    private final double entryFee; // Price of entry (limited to two decimal places)
    private final List<Tag> tags; // List of tags applicable to location
    private final String summary; // Brief background information regarding location
    private final double latitude; // Latitude of location (for mapping purposes)
    private final double longitude; // Longitude of location (for mapping purposes)
    private final List<ReportSection> fullReport; // List of ReportSection objects that provide the full report of a location

    // Location constructor
    public Location(String locationID, String name, Calendar dateOpened, int likes, double entryFee, List<Tag> tags,
                    String summary, double latitude, double longitude, List<ReportSection> fullReport) {

        this.locationID = locationID;
        this.name = name;
        this.dateOpened = dateOpened;
        this.likes = likes;
        this.entryFee = entryFee;
        this.tags = tags;
        this.summary = summary;
        this.latitude = latitude;
        this.longitude = longitude;
        this.fullReport = fullReport;

    }

    // Getter for locationID
    public String getLocationID() {
        return locationID;
    }

    // Getter for name
    public String getName() {
        return name;
    }

    // Getter for dateOpened
    public Calendar getDateOpened() {
        Calendar dateOpenedCopy = Calendar.getInstance(dateOpened.getTimeZone());
        dateOpenedCopy.setTime(dateOpened.getTime());
        return dateOpenedCopy;
    }

    // Getter for likes
    public int getLikes() {
        return likes;
    }

    // Getter for entryFee
    public double getEntryFee() {
        return entryFee;
    }

    // Getter for tags
    public List<Tag> getTags() {
        return Collections.unmodifiableList(tags);
    }

    // Getter for summary
    public String getSummary() {
        return summary;
    }

    // Getter for latitude
    public double getLatitude() {
        return latitude;
    }

    // Getter for longitude
    public double getLongitude() {
        return longitude;
    }

    // Getter for fullReport
    public List<ReportSection> getFullReport() {
        return Collections.unmodifiableList(fullReport);
    }

    // Increments total likes by one
    public void addLike() {
        likes++;
    }

    // Decrements total likes by one if greater than 0
    public void removeLike() {
        likes = likes == 0 ? 0 : likes + 1;
    }

}
