package com.nearchitectural.utilities;

import com.nearchitectural.ui.models.LocationModel;
import com.nearchitectural.utilities.models.Location;
import com.nearchitectural.utilities.models.TagID;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/* Author:  Joel Bell-Wilding, Kristiyan Doykov
 * Since:   15/01/20
 * Version: 1.1
 * Purpose: Filter locations based on a set of factors (tags applied, distance to user etc)
 */
public class Filter {

    private static final String TAG = "Filter Class";

    /* Filter the locations according to search criteria (search text and distance/filters) */
    public static List<LocationModel> apply(List<LocationModel> models, String query,
                                            double distanceSelected, Map<TagID, Boolean> activeTags) {

        final List<LocationModel> filteredModelList = new ArrayList<>(); // List of models to filter
        final String lowerCaseQuery = query.toLowerCase(); // Search string
        // Distance unit conversion rate
        final int conversionRate = Settings.getInstance().getDistanceUnit().getConversionRate();

        // Cycles through all locations and adds to list if within search criteria
        for (LocationModel model : models) {

            final String titleText = model.getLocationInfo().getName().toLowerCase();
            final String placeTypeText = model.getLocationInfo().getType().toLowerCase();
            final double distance = model.getMDistanceFromCurrentPos();

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
                        && distanceSelected * conversionRate >= distance)) {
                    filteredModelList.add(model);
                }
            }
        }

        // A list of models which do not match the active search tags
        List<LocationModel> nonMatchModels = new ArrayList<>();
        // Cycles through all locations and removes any without the applied tags
        for (LocationModel model: filteredModelList) {
            for (TagID tag: TagID.values()) {
                if (activeTags.get(tag) && !model.getLocationInfo().getTagValue(tag)) {
                    nonMatchModels.add(model);
                }
            }
        }
        filteredModelList.removeAll(nonMatchModels); // Remove all non-matching models

        return filteredModelList;
    }

    // Method used to determine if provided location meets the criteria of the user settings
    public static boolean locationMeetsSettingsCriteria(Location location) {

        Settings userSettings = Settings.getInstance();

        // Guard against database retrieval errors
        if (location.getName().equals("Unknown")
                || (location.getLatitude() == 0 && location.getLongitude() == 0)) {
            return false;
        }

        // Ensures location matches all set tags
        for (TagID tag: TagID.values()) {
            if (userSettings.getTagValue(tag) && !location.getTagValue(tag)) {
                return false;
            }
        }

        // Ensures location is within the user specified max distance
        return (DistanceCalculator.calculateDistance(CurrentCoordinates.getCoords().latitude,
                location.getLatitude(),
                CurrentCoordinates.getCoords().longitude,
                location.getLongitude()) <= userSettings.getMaxDistance());
    }
}
