package com.nearchitectural.utilities.comparators;

import com.nearchitectural.utilities.models.Location;

import java.util.Comparator;

/** author Joel Bell-Wilding
 *  version 1.0
 *  since 11/02/20
 *  purpose: Compare locations by oldest to newest (opening date)
 */
public class OldestToNewestComparator implements Comparator<Location> {

    @Override
    public int compare(Location firstLocation, Location secondLocation) {
        return secondLocation.getDateOpened().compareTo(firstLocation.getDateOpened());
    }
}
