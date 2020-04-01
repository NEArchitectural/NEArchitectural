package com.nearchitectural.ui.models;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
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

        // Initialises database and lists to hold location and model data
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Loops through each location in the list and updates it
        for (final Location location : locationsToUpdate) {
            db.collection("locations")
                    .document(location.getId())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {

                                DocumentSnapshot document = task.getResult();

                                // Create location from database
                                Location locationTemp = DatabaseExtractor.extractLocation(document.getId(), document.getData());
                                locationsToShow.put(locationTemp.getId(), locationTemp);

                                // Find current distance between user and location
                                double distanceToUser = DistanceCalculator.calculateDistance(
                                        CurrentCoordinates.getCoords().latitude, locationTemp.getLatitude(),
                                        CurrentCoordinates.getCoords().longitude, locationTemp.getLongitude());

                                // Create location model from location object and distance to user
                                locationModelsList.put(locationTemp.getId(), new LocationModel(locationTemp, distanceToUser));
                            }
                            locationModels.postValue(new ArrayList<>(locationModelsList.values())); // Post model to model list
                            locations.postValue(new ArrayList<>(locationsToShow.values())); // Post location to location list
                        }
                    });
        }
    }

    // Initially retrieves locations from the database and creates results model
    private void createSearchResults() {

        // Initialises database and lists to hold location and model data
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Creates location and models from database and posts each to their respective lists
        db.collection("locations")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, String.valueOf(document.getData().get("name")));

                                // For each location create a new Location instance and add it to the list
                                Location locationTemp = DatabaseExtractor.extractLocation(document.getId(), document.getData());
                                locationsToShow.put(locationTemp.getId(), locationTemp);

                                // Find current distance between user and location
                                double distanceToUser = DistanceCalculator.calculateDistance(
                                        CurrentCoordinates.getCoords().latitude, locationTemp.getLatitude(),
                                        CurrentCoordinates.getCoords().longitude, locationTemp.getLongitude());

                                // Create location model from location object and distance to user
                                locationModelsList.put(locationTemp.getId(), new LocationModel(locationTemp, distanceToUser));
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                            locationModels.postValue(new ArrayList<>(locationModelsList.values())); // Post model to model list
                            locations.postValue(new ArrayList<>(locationsToShow.values())); // Post location to location list
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
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