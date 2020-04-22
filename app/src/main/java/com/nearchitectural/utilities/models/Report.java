package com.nearchitectural.utilities.models;

import java.util.List;

/* Author:  Joel Bell-Wilding
 * Since:   18/02/20
 * Version: 1.0
 * Purpose: A record-like class holding report-based information (text and images) for a
 *          given location. There is a one-to-one relationship between a location and a report.
 */
public class Report {

    private final String reportID; // Unique reference ID for report
    private List<String> paragraphs; // A list of paragraphs which together form the full report
    private List<String> slideshowURLs; // A list of images to be displayed on the location page
    private List<String> references; // A list of all references for the location info and images
    private String timelineSnippet; // A snippet of architectural information to be displayed in the timeline

    public Report(String reportID, List<String> paragraphs, List<String> slideshowURLs, List<String> references, String timelineSnippet) {
        this.reportID = reportID;
        this.paragraphs = paragraphs;
        this.slideshowURLs = slideshowURLs;
        this.references = references;
        this.timelineSnippet = timelineSnippet;
    }

    // Getters
    public String getReportID() {
        return reportID;
    }

    public List<String> getParagraphs() {
        return paragraphs;
    }

    public List<String> getReferences() {
        return references;
    }

    public List<String> getSlideshowURLs() {
        return slideshowURLs;
    }

    public String getTimelineSnippet() {
        return timelineSnippet;
    }

    /* Concatenates all paragraphs together to produce a single text string
     * containing the full report */
    public String getFullReport() {
        StringBuilder stringBuilder = new StringBuilder();
        for (String paragraph : paragraphs) {
            stringBuilder.append(paragraph);
            stringBuilder.append("\n\n");
        }
        // Returns an empty string if report is empty (i.e. database retrieval failed)
        return stringBuilder.length() == 0 ? "" : stringBuilder.substring(0, stringBuilder.length()-2); // Removes last two empty lines
    }

    // Concatenates all report references to produce a single text string
    public String getFullReferences() {
        StringBuilder stringBuilder = new StringBuilder();
        if(references != null) {
            for (String reference :
                    references) {
                stringBuilder.append(reference).append("\n\n");
            }
            return stringBuilder.length() == 0 ? "" : stringBuilder.substring(0, stringBuilder.length() - 2);
        }
        // In case no references have been registered in the database display an appropriate message
        return "No references to display";
    }
}
