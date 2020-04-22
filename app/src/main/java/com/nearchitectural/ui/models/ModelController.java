package com.nearchitectural.ui.models;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nearchitectural.utilities.CurrentCoordinates;
import com.nearchitectural.utilities.DatabaseExtractor;
import com.nearchitectural.utilities.DistanceCalculator;
import com.nearchitectural.utilities.models.Location;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* Author:  Kristiyan Doykov, Joel Bell-Wilding
 * Since:   12/12/19
 * Version: 1.1
 * Purpose: Creates and updates Location models from static location information in
 *          the database and dynamic information from the context of use (i.e. user's location).
 *          Allows state of internal model map to be persistent across view configuration changes.
 */
public class ModelController extends ViewModel {

    private static final String TAG = "ModelProducerViewModel";
    private Map<String, LocationModel> modelIDMap; // Map of location IDs to their corresponding models
    private MutableLiveData<Map<String, LocationModel>> locationModels; // Observes the state of the models map

    public ModelController() {
        modelIDMap = new HashMap<>();
    }

    // Updates the state of a specific location model in the model map
    public void updateLocationModel(String locationIDs) {
        if (locationModels == null) {
            locationModels = new MutableLiveData<>();
        }
        retrieveModel(locationIDs);
    }

    // Updates the state of all location models and returns the live data object to be observed
    public LiveData<Map<String, LocationModel>> updateAllLocationModels() {
        if (locationModels == null) {
            locationModels = new MutableLiveData<>();
        }
        retrieveAllModels();
        return locationModels;
    }

    // Retrieves and posts the location model for the location with the provided ID
    private void retrieveModel(String locationID) {

        new DatabaseExtractor().extractLocationByID(locationID, new DatabaseExtractor.DatabaseCallback<Location>() {
            @Override
            public void onDataRetrieved(Location data) {

                if (data != null) {
                    // Find current distance between user and location
                    double distanceToUser = DistanceCalculator.calculateDistance(
                            CurrentCoordinates.getCoords().latitude, data.getLatitude(),
                            CurrentCoordinates.getCoords().longitude, data.getLongitude());

                    // Create location model from location object and distance to user
                    modelIDMap.put(data.getId(), new LocationModel(data, distanceToUser));
                }
                locationModels.postValue(modelIDMap); // Post model to map
            }
        });
    }

    // Retrieves and posts all location models to the modelIDMap
    private void retrieveAllModels() {

        new DatabaseExtractor().extractAllLocations(new DatabaseExtractor.DatabaseCallback<List<Location>>() {
            @Override
            public void onDataRetrieved(List<Location> data) {

                if (data != null) {
                    for (Location location : data) {

                        // Find current distance between user and location
                        double distanceToUser = DistanceCalculator.calculateDistance(
                                CurrentCoordinates.getCoords().latitude, location.getLatitude(),
                                CurrentCoordinates.getCoords().longitude, location.getLongitude());

                        // Create location model from location object and distance to user
                        modelIDMap.put(location.getId(), new LocationModel(location, distanceToUser));
                    }
                    locationModels.postValue(modelIDMap); // Post model to map
                } else {
                    // Send an empty results map if db retrieval fails
                    modelIDMap = new HashMap<>();
                    locationModels.postValue(modelIDMap);
                }
            }
        });
    }
}