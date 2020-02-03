package com.nearchitectural.utilities;

import java.util.Comparator;

/** @author Joel Bell-Wilding
 *  @version 1.0
 *  @since 28/01/20
 *  purpose: Compare locations by alphabetical order of name
 */
public class AlphabeticComparator implements Comparator<LocationInfo> {

    @Override
    public int compare(LocationInfo firstLocation, LocationInfo secondLocation) {
        return firstLocation.getName().compareToIgnoreCase(secondLocation.getName());
    }
}
