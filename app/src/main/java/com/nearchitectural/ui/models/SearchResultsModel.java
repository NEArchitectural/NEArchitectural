package com.nearchitectural.ui.models;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.nearchitectural.utilities.CurrentCoordinates;
import com.nearchitectural.utilities.DatabaseExtractor;
import com.nearchitectural.utilities.DistanceCalculator;
import com.nearchitectural.utilities.models.Location;

import java.util.ArrayList;
import java.util.List;

/* Author:  Kristiyan Doykov
 * Since:   TODO: Fill in date
 * Version: 1.0
 * Purpose: Acts as a model which holds the list of search results (i.e. a list of locations models)
 *          to be adapted and displayed on the UI
 */
public class SearchResultsModel extends ViewModel {

    private static final String TAG = "SAViewModel";
    private ArrayList<Location> locationsToShow; // List of locations to be displayed in results
    private ArrayList<LocationModel> locationModelsList; // List of models corresponding to location
    private MutableLiveData<List<Location>> locations; // Observes the state of the locations list
    private MutableLiveData<List<LocationModel>> locationModels; // Observes the state of the models list

    // Returns the list of locations to show
    public LiveData<List<Location>> getLocationsToShow() {
        if (locations == null) {
            locations = new MutableLiveData<>();
            loadLocationsAndCreateModels();
        }
        return locations;
    }

    // Returns the list of location models to be displayed
    public LiveData<List<LocationModel>> getLocationModels() {
        if (locationModels == null) {
            locationModels = new MutableLiveData<>();
            loadLocationsAndCreateModels();
        }
        return locationModels;
    }

    // Retrieves locations from the database and creates the models as needed
    private void loadLocationsAndCreateModels() {

        // Initialises database and lists to hold location and model data
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        locationsToShow = new ArrayList<>();
        locationModelsList = new ArrayList<>();

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
                                Location locationTemp = DatabaseExtractor.extractLocation(document);
                                locationsToShow.add(locationTemp);

                                // Find current distance between user and location
                                double distanceToUser = DistanceCalculator.calculateDistance(
                                        CurrentCoordinates.getCoords().latitude, locationTemp.getLatitude(),
                                        CurrentCoordinates.getCoords().longitude, locationTemp.getLongitude());

                                // Create location model from location object and distance to user
                                locationModelsList.add(new LocationModel(locationTemp, distanceToUser));
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                            locationModels.postValue(locationModelsList); // Post model to model list
                            locations.postValue(locationsToShow); // Post location to location list
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                            locationsToShow = new ArrayList<>();
                            locations.postValue(locationsToShow);
                            locationModelsList = new ArrayList<>();
                            locationModels.postValue(locationModelsList);
                        }
                    }
                });
    }
}