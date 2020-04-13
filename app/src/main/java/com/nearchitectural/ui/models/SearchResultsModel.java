package com.nearchitectural.ui.models;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nearchitectural.utilities.CurrentCoordinates;
import com.nearchitectural.utilities.DatabaseExtractor;
import com.nearchitectural.utilities.DistanceCalculator;
import com.nearchitectural.utilities.models.Location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* Author:  Kristiyan Doykov, Joel Bell-Wilding
 * Since:   12/12/19
 * Version: 1.1
 * Purpose: Acts as a model which holds the list of search results (i.e. a list of locations models)
 *          to be adapted and displayed on the UI
 */
public class SearchResultsModel extends ViewModel {

    private static final String TAG = "SAViewModel";
    private Map<String, Location> locationsToShow; // List of locations to be displayed in results
    private Map<String, LocationModel> locationModelsList; // List of models corresponding to location
    private MutableLiveData<List<Location>> locations; // Observes the state of the locations list
    private MutableLiveData<List<LocationModel>> locationModels; // Observes the state of the models list

    public SearchResultsModel() {
        locationsToShow = new HashMap<>();
        locationModelsList = new HashMap<>();
        createSearchResults(); // Create results initially when instantiated
    }

    // Returns the list of locations to show
    public LiveData<List<Location>> getLocationsToShow() {
        if (locations == null) {
            locations = new MutableLiveData<>();
        }
        return locations;
    }

    // Returns the list of location models to be displayed
    public LiveData<List<LocationModel>> getLocationModels() {
        if (locationModels == null) {
            locationModels = new MutableLiveData<>();
        }
        return locationModels;
    }

    // Takes a list of locations to update and replaces each with the updated version stored in the database
    public void refineSearchResults(List<Location> locationsToUpdate) {

        DatabaseExtractor extractor = new DatabaseExtractor();

        for (Location location : locationsToUpdate) {
            extractor.extractLocationByID(location.getId(), new DatabaseExtractor.DatabaseCallback<Location>() {
                @Override
                public void onDataRetrieved(Location data) {
                    if (data != null) {
                        locationsToShow.put(data.getId(), data);

                        // Find current distance between user and location
                        double distanceToUser = DistanceCalculator.calculateDistance(
                                CurrentCoordinates.getCoords().latitude, data.getLatitude(),
                                CurrentCoordinates.getCoords().longitude, data.getLongitude());

                        // Create location model from location object and distance to user
                        locationModelsList.put(data.getId(), new LocationModel(data, distanceToUser));
                    }
                    locationModels.postValue(new ArrayList<>(locationModelsList.values())); // Post model to model list
                    locations.postValue(new ArrayList<>(locationsToShow.values())); // Post location to location list
                }
            });
        }
    }

    // Initially retrieves locations from the database and creates results model
    private void createSearchResults() {

        DatabaseExtractor extractor = new DatabaseExtractor();

        extractor.extractAllLocations(new DatabaseExtractor.DatabaseCallback<List<Location>>() {
            @Override
            public void onDataRetrieved(List<Location> data) {
                if (data != null) {
                    for (Location location : data) {
                        locationsToShow.put(location.getId(), location);

                        // Find current distance between user and location
                        double distanceToUser = DistanceCalculator.calculateDistance(
                                CurrentCoordinates.getCoords().latitude, location.getLatitude(),
                                CurrentCoordinates.getCoords().longitude, location.getLongitude());

                        // Create location model from location object and distance to user
                        locationModelsList.put(location.getId(), new LocationModel(location, distanceToUser));
                        Log.d(TAG, location.getId() + " => " + location.getName());
                    }
                    locationModels.postValue(new ArrayList<>(locationModelsList.values())); // Post model to model list
                    locations.postValue(new ArrayList<>(locationsToShow.values())); // Post location to location list
                } else {
                    Log.w(TAG, "Error getting documents.");
                    // Send an empty results list
                    locationsToShow = new HashMap<>();
                    locations.postValue(new ArrayList<>(locationsToShow.values()));
                    locationModelsList = new HashMap<>();
                    locationModels.postValue(new ArrayList<>(locationModelsList.values()));
                }
            }
        });
    }
}