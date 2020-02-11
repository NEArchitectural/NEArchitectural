package com.nearchitectural.utilities.comparators;

import com.nearchitectural.utilities.models.Location;

import java.util.Comparator;

/** author Joel Bell-Wilding
 *  version 1.0
 *  since 11/02/20
 *  purpose: Compare locations by newest to oldest (opening date)
 */
public class NewestToOldestComparator implements Comparator<Location> {

    @Override
    public int compare(Location firstLocation, Location secondLocation) {
        return firstLocation.getDateOpened().compareTo(secondLocation.getDateOpened());
    }

}
