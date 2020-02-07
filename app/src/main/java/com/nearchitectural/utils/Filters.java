package com.nearchitectural.utils;

import android.util.Log;

import com.nearchitectural.ui.models.ListItemModel;

import java.util.ArrayList;
import java.util.List;

public class Filters {
    private static final String TAG = "Filter Class";

    /* Filters the locations from the db according to user input (search text and distance/filters) */
    public static List<ListItemModel> apply(List<ListItemModel> models, String query,
                                            double distanceSelected, boolean wheelchairAccessNeeded,
                                            boolean childFriendlyNeeded, boolean cheapEntryNeeded,
                                            boolean freeEntryNeeded) {
        final String lowerCaseQuery = query.toLowerCase();

        final List<ListItemModel> filteredModelList = new ArrayList<>();
        for (ListItemModel model : models) {

            final String titleText = model.getTitle().toLowerCase();
            final String placeTypeText = model.getLocationType().toLowerCase();
            final double distance = model.getMDistanceFromCurrentPosInMeters();
            Log.w(TAG, String.valueOf(distance));

            boolean textMatchFound = titleText.contains(lowerCaseQuery)
                    || placeTypeText.contains(lowerCaseQuery);

            if (distanceSelected == 0) {
                if (textMatchFound) {
                    filteredModelList.add(model);
                }
            } else {
                if (textMatchFound
                        && (distanceSelected > 0
                        && distanceSelected * 1000 >= distance)) {
                    filteredModelList.add(model);
                }
            }
        }
        Log.d(TAG, "Filtering current distance: " + distanceSelected * 1000);

        ArrayList<ListItemModel> modelsThatDontMatch = new ArrayList<>();

        if (wheelchairAccessNeeded) {
            for (ListItemModel model :
                    filteredModelList) {
                if (!model.mIsWheelChairAccessible()) {
                    modelsThatDontMatch.add(model);
                }
            }
        }
        if (childFriendlyNeeded) {
            for (ListItemModel model :
                    filteredModelList) {
                if (!model.mIsChildFriendly()) {
                    modelsThatDontMatch.add(model);
                }
            }
        }
        if (cheapEntryNeeded) {
            for (ListItemModel model :
                    filteredModelList) {
                if (!model.mHasCheapEntry()) {
                    modelsThatDontMatch.add(model);
                }
            }
        }
        if (freeEntryNeeded) {
            for (ListItemModel model :
                    filteredModelList) {
                if (!model.mHasFreeEntry()) {
                    modelsThatDontMatch.add(model);
                }
            }
        }

        filteredModelList.removeAll(modelsThatDontMatch);

        return filteredModelList;
    }
}
