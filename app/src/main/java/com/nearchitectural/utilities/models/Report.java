package com.nearchitectural.utilities.models;

import java.util.List;

/* Author:  Joel Bell-Wilding
 * Since:   18/02/20
 * Version: 1.0
 * Purpose: A record-like class holding report-based information (text and images) for a
 *          given location. There is a one-to-one relationship between a location and a report.
 */
public class Report {

    private String reportID; // Unique reference ID for report
    private List<String> paragraphs; // A list of paragraphs which together form the full report
    private List<String> slideshowURLs; // A list of images to be displayed on the location page

    public Report(String reportID, List<String> paragraphs, List<String> slideshowURLs) {
        this.reportID = reportID;
        this.paragraphs = paragraphs;
        this.slideshowURLs = slideshowURLs;
    }

    // Getters
    public String getReportID() {
        return reportID;
    }

    public List<String> getParagraphs() {
        return paragraphs;
    }

    public List<String> getSlideshowURLs() {
        return slideshowURLs;
    }

    /* Concatenates all paragraphs together to produce a single text string
     *containing the full report */
    public String getFullReport() {
        String fullReport = "\n";
        for (String paragraph : paragraphs) {
            fullReport += paragraph + "\n";
        }
        return fullReport;
    }

}
