package com.nearchitectural.utilities;

import android.graphics.drawable.Drawable;
import java.util.List;

/** @author Joel Bell-Wilding
 *  @version 1.0
 *  @since 28/01/20
 *  purpose: Stores text and images relating to a single section of a location's historic report
 */
public class ReportSection {

    private final String title; // Title of report section
    private final List<String> information; // List of paragraphs for report section
    private final List<Drawable> images; // List of images for report section

    // ReportSection constructor
    public ReportSection(String title, List<String> information, List<Drawable> images) {
        this.title = title;
        this.information = information;
        this.images = images;
    }

    // Getter for title
    public String getTitle() {
        return title;
    }

    // Getter for information (list of paragraphs)
    public List<String> getInformation() {
        return information;
    }

    // Getter for images (list of images)
    public List<Drawable> getImages() {
        return images;
    }
}
