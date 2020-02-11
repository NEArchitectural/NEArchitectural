package com.nearchitectural.utilities.comparators;

import com.nearchitectural.utilities.models.Location;

import java.util.Comparator;

/** author Joel Bell-Wilding
 *  version 1.0
 *  since 11/02/20
 *  purpose: Compare locations by alphabetical order of name
 */
public class AlphabeticComparator implements Comparator<Location> {

    @Override
    public int compare(Location firstLocation, Location secondLocation) {
        return firstLocation.getName().compareToIgnoreCase(secondLocation.getName());
    }

}
