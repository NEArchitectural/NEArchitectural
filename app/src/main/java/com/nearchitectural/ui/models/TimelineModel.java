package com.nearchitectural.ui.models;

/* Author:  Taylor Stubbs, James Allwood-Panter
 * Since:   03/04/20
 * Version: 1.1
 * Purpose: Extends LocationModel to provide further information about how to display the
 *          current timeline model in ViewHolder
 */
public class TimelineModel extends LocationModel {

    private boolean isFirstInList = false; // Flags if model is the first in list (i.e. index 0)
    private boolean isLastInList = false; // Flags if model is the last in list (i.e. index n-1)
    private boolean oddIndex = false; // Flags if model has an odd index (i.e. index % 2 is false)
    private boolean landscapeLayout = false; // Flags if model is to be displayed in landscape orientation

    public TimelineModel(LocationModel model) {
        super(model.getLocationInfo(), model.getMDistanceFromCurrentPos());
    }

    // Getters and setters for isFirstInList and isLastInList booleans
    public boolean isFirstInList() {
        return isFirstInList;
    }

    public void setFirstInList(boolean firstInList) {
        isFirstInList = firstInList;
    }

    public boolean isLastInList() {
        return isLastInList;
    }

    public void setLastInList(boolean lastInList) {
        isLastInList = lastInList;
    }

    // Getter and setter for whether location model is odd number in list
    public boolean isOddIndex() {
        return oddIndex;
    }

    public void setOddIndex(boolean oddIndex) {
        this.oddIndex = oddIndex;
    }

    /* Getter and setter for whether device is in landscape (and hence whether model ViewHolder
    should be displayed in landscape or otherwise */
    public boolean isLandscapeLayout() {
        return landscapeLayout;
    }

    public void setLandscapeLayout(boolean landscapeLayout) {
        this.landscapeLayout = landscapeLayout;
    }
}
