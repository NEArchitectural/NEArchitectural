package com.nearchitectural.utilities;

import java.util.Comparator;

/** @author Joel Bell-Wilding
 *  @version 1.0
 *  @since 28/01/20
 *  purpose: Compare locations by alphabetical order of name
 */
public class AlphabeticComparator implements Comparator<Location> {

    @Override
    public int compare(Location firstLocation, Location secondLocation) {
        return firstLocation.getName().compareToIgnoreCase(secondLocation.getName());
    }
}
