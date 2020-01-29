package com.nearchitectural.utilities;

import java.util.Comparator;

/** @author Joel Bell-Wilding
 *  @version 1.0
 *  @since 28/01/20
 *  purpose: Compare locations by newest to oldest (opening date)
 */
public class NewestToOldestComparator implements Comparator<Location> {

    @Override
    public int compare(Location firstLocation, Location secondLocation) {
        return firstLocation.getDateOpened().compareTo(secondLocation.getDateOpened());
    }
}
