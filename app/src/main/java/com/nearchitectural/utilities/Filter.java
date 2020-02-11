package com.nearchitectural.utilities;

import android.util.Log;

import com.nearchitectural.ui.models.ListItemModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**author: Kristiyan Doykov, Joel Bell-Wilding
 * since: TODO: Fill in date
 * version: 1.1
 * purpose: Filter a list of locations based on a set of factors (tags applied, distance to user etc)
 */
public class Filter {

    private static final String TAG = "Filter Class";
    private final static int KILOMETER_CONVERSION = 1000;

    /* Filter the locations from the database according to user input (search text and distance/filters) */
    public static List<ListItemModel> apply(List<ListItemModel> models, String query,
                                            double distanceSelected, Map<TagID, Boolean> activeTags) {

        final String lowerCaseQuery = query.toLowerCase();

        final List<ListItemModel> filteredModelList = new ArrayList<>();

        // Cycles through all locations and adds to list if within search criteria
        for (ListItemModel model : models) {

            final String titleText = model.getLocationInfo().getName().toLowerCase();
            final String placeTypeText = model.getLocationInfo().getLocationType().toLowerCase();
            final double distance = model.getMDistanceFromCurrentPosInMeters();
            Log.w(TAG, String.valueOf(distance));

            // Checks if either title or place type strings match search string
            boolean textMatchFound = titleText.contains(lowerCaseQuery)
                    || placeTypeText.contains(lowerCaseQuery);

            // Checks if location is within distance radius
            if (distanceSelected == 0) {
                if (textMatchFound) {
                    filteredModelList.add(model);
                }
            } else {
                if (textMatchFound
                        && (distanceSelected > 0
                        && distanceSelected * KILOMETER_CONVERSION >= distance)) {
                    filteredModelList.add(model);
                }
            }
        }
        Log.d(TAG, "Filtering current distance: " + distanceSelected * KILOMETER_CONVERSION);

        List<ListItemModel> nonMatchModels = new ArrayList<>();

        // Cycles through all locations and removes any without the applied tags
        for (ListItemModel model: filteredModelList) {

            for (TagID tag: TagID.values()) {

                if (activeTags.get(tag) && !model.getLocationInfo().getTagValue(tag)) {
                    nonMatchModels.add(model);
                }
            }
        }
        filteredModelList.removeAll(nonMatchModels); // Remove all non-matching models

        return filteredModelList;
    }
}
