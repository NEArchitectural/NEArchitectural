package com.nearchitectural.utilities.comparators;

import com.nearchitectural.ui.models.LocationModel;

import java.util.Comparator;

/* Author:  Joel Bell-Wilding - Original author
 * Version: 1.0
 * Since:   11/02/20
 * Purpose: Compare locations by alphabetical order of name
 */
public class AlphabeticComparator implements Comparator<LocationModel> {

    @Override
    public int compare(LocationModel firstLocation, LocationModel secondLocation) {
        return firstLocation.getTitle().compareToIgnoreCase(secondLocation.getTitle());
    }
}
