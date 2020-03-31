package com.nearchitectural.utilities.comparators;

import com.nearchitectural.ui.models.LocationModel;

import java.util.Comparator;

/* Author:  Kristiyan Doykov
 * Since:   15/01/20
 * Version: 1.0
 * Purpose: Comparator which compares location list models by closest distance to the user
 */
public class ShortestDistanceComparator implements Comparator<LocationModel> {

    @Override
    public int compare(LocationModel firstModel, LocationModel secondModel) {
        if (firstModel.getMDistanceFromCurrentPos() == secondModel.getMDistanceFromCurrentPos())
            return 0;
        return firstModel.getMDistanceFromCurrentPos() > secondModel.getMDistanceFromCurrentPos() ? 1 : -1;
    }
}
