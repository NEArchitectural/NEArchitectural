package com.nearchitectural.utilities;

import java.util.Comparator;

/** @author Joel Bell-Wilding
 *  @version 1.0
 *  @since 28/01/20
 *  purpose: Compare locations by oldest to newest (opening date)
 */
public class OldestToNewestComparator implements Comparator<LocationInfo> {

    @Override
    public int compare(LocationInfo firstLocation, LocationInfo secondLocation) {
        return secondLocation.getDateOpened().compareTo(firstLocation.getDateOpened());
    }
}
