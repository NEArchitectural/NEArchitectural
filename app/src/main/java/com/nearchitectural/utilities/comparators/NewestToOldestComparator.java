package com.nearchitectural.utilities.comparators;

import com.nearchitectural.ui.models.LocationModel;

import java.util.Comparator;

/* Author:  Joel Bell-Wilding
 * Version: 1.0
 * Since:   11/02/20
 * Purpose: Compare locations by oldest to newest (opening date)
 */
public class NewestToOldestComparator implements Comparator<LocationModel> {

    @Override
    public int compare(LocationModel firstLocation, LocationModel secondLocation) {
        return secondLocation.getLocationInfo().getYearOpened() - firstLocation.getLocationInfo().getYearOpened();
    }
}
