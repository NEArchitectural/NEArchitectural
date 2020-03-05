package com.nearchitectural.utilities.comparators;

import com.nearchitectural.utilities.models.Location;

import java.util.Comparator;

/* Author:  Joel Bell-Wilding
 * Version: 1.0
 * Since:   11/02/20
 * Purpose: Compare locations by newest to oldest (opening date)
 */
public class NewestToOldestComparator implements Comparator<Location> {

    @Override
    public int compare(Location firstLocation, Location secondLocation) {
        return firstLocation.getYearOpened() - secondLocation.getYearOpened();
    }
}
