package com.nearchitectural.utilities.comparators;

import com.nearchitectural.ui.models.ListItemModel;

import java.util.Comparator;

/* Author:  Kristiyan Doykov
 * Since:   TODO: Fill in date
 * Version: 1.0
 * Purpose: Comparator which compares location list models by closest distance to the user
 */
public class ShortestDistanceComparator implements Comparator<ListItemModel> {

    @Override
    public int compare(ListItemModel firstModel, ListItemModel secondModel) {
        if (firstModel.getMDistanceFromCurrentPosInMeters() == secondModel.getMDistanceFromCurrentPosInMeters())
            return 0;
        return firstModel.getMDistanceFromCurrentPosInMeters() > secondModel.getMDistanceFromCurrentPosInMeters() ? 1 : -1;
    }
}
