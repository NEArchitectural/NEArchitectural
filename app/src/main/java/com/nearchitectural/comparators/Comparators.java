package com.nearchitectural.comparators;

import com.nearchitectural.ui.models.ListItemModel;

import java.util.Comparator;

public class Comparators {

    /* Compares two locations by distance from the user */
    public static final Comparator<ListItemModel> SMALLEST_DISTANCE_COMPARATOR = new Comparator<ListItemModel>() {
        @Override
        public int compare(ListItemModel a, ListItemModel b) {
            if (a.getMDistanceFromCurrentPosInMeters() == b.getMDistanceFromCurrentPosInMeters())
                return 0;
            return a.getMDistanceFromCurrentPosInMeters() > b.getMDistanceFromCurrentPosInMeters() ? 1 : -1;
        }
    };
}
