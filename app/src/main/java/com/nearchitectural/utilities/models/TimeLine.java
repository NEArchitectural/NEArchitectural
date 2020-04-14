package com.nearchitectural.utilities.models;

/* Author:  James Allwood-Panter
 * Since:   24/03/20
 * Version: 1.0
 * Purpose: Record-like class which stores information regarding a given location for the timeline.
 */
public class TimeLine  {

    private String name; // Location name
    private long yearOpened; // year location was opened initially
    private String yearOpenedString; // String representation of year opened
    private String thumbnailURL; // URL for thumbnail image used for displaying on UI
    private String summaryTimeLine; // Location summary



    public TimeLine(String name, String summary, long yearOpened, String thumbnailAddress) {

        this.name = name;
        this.yearOpened = yearOpened;
        this.yearOpenedString = yearOpened < 0 ? yearOpened + "BC" : yearOpened + "AC";
        this.thumbnailURL = thumbnailURL;
        this.summaryTimeLine = summaryTimeLine;
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getThumbnailURL() {
        return thumbnailURL;
    }

    public long getYearOpened() {
        return yearOpened;
    }

    public String getYearOpenedString() {
        return yearOpenedString;
    }

    public String getSummaryTimeLine() {
        return summaryTimeLine; }

}
