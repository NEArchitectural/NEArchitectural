package com.nearchitectural.utilities;

import java.util.Comparator;

/** @author Joel Bell-Wilding
 *  @version 1.0
 *  @since 28/01/20
 *  purpose: Compare locations by newest to oldest (opening date)
 */
public class NewestToOldestComparator implements Comparator<LocationInfo> {

    @Override
    public int compare(LocationInfo firstLocation, LocationInfo secondLocation) {
        return firstLocation.getDateOpened().compareTo(secondLocation.getDateOpened());
    }
}
