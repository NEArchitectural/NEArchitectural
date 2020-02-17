package com.nearchitectural.utils;

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
import com.nearchitectural.models.Location;
import com.nearchitectural.ui.models.ListItemModel;

import java.util.ArrayList;
import java.util.List;

public class SearchableActivityViewModel extends ViewModel {
    private FirebaseFirestore db;
    private static final String TAG = "SAViewModel";
    private ArrayList<Location> locationsToShowList;
    private ArrayList<ListItemModel> mModelsList;
    private MutableLiveData<List<Location>> locations;
    private MutableLiveData<List<ListItemModel>> models;

    public LiveData<List<Location>> getLocationsToShow() {
        if (locations == null) {
            locations = new MutableLiveData<>();
            loadLocationsAndCreateModels();
        }
        return locations;
    }

    public LiveData<List<ListItemModel>> getmModels() {
        if (models == null) {
            models = new MutableLiveData<>();
            loadLocationsAndCreateModels();
        }
        return models;
    }

    private void loadLocationsAndCreateModels() {
        db = FirebaseFirestore.getInstance();

        locationsToShowList = new ArrayList<>();

        mModelsList = new ArrayList<>();

        db.collection("locations")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, String.valueOf(document.getData().get("name")));

                                // For each location create aa new Location instance and add it to the list
                                Location locationTemp = DatabaseExtractor.extractLocation(document);

                                locationsToShowList.add(locationTemp);

                                mModelsList.add(new ListItemModel(locationTemp.getId(), locationTemp.getName(),
                                        locationTemp.getLocationType(),
                                        locationTemp.isWheelChairAccessible(), locationTemp.isChildFriendly(),
                                        locationTemp.hasCheapEntry(), locationTemp.hasFreeEntry(),
                                        locationTemp.getThumbnailURL(),
                                        CalculateDistance.calculateDistance(CurrentCoordinates.getCoords().latitude, locationTemp.getLatitude(),
                                                CurrentCoordinates.getCoords().longitude, locationTemp.getLongitude()), locationTemp.getLikes()));
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                            models.postValue(mModelsList);
                            locations.postValue(locationsToShowList);
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                            locationsToShowList = new ArrayList<>();
                            locations.postValue(locationsToShowList);
                            mModelsList = new ArrayList<>();
                            models.postValue(mModelsList);
                        }
                    }
                });
    }


}
