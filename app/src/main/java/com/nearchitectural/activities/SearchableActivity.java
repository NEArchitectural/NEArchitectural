package com.nearchitectural.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.slider.Slider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.nearchitectural.CurrentCoordinates;
import com.nearchitectural.ListItemAdapter;
import com.nearchitectural.Location;
import com.nearchitectural.R;
import com.nearchitectural.databinding.ActivitySearchBinding;
import com.nearchitectural.fragments.OptionsDialogFragment;
import com.nearchitectural.ui.models.ListItemModel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SearchableActivity extends AppCompatActivity implements OptionsDialogFragment.OptionsDialogListener {

    private boolean wheelchairAccess;
    private boolean childFriendly;
    private boolean cheapEntry;
    private boolean freeEntry;
    private LatLng currentLocation;
    public static final String TAG = "SearchableActivity";

    private RecyclerView places;
    private ActivitySearchBinding searchBinding;
    private TextView seekbarProg;
    private Slider slider;
    private AppCompatCheckBox wheelChairCheckBox;
    private AppCompatCheckBox childFriendlyCheckBox;
    private List<ListItemModel> mModels;
    private ListItemAdapter mAdapter;
    private DialogFragment dialogFragment;
    private FirebaseFirestore db;
    private double distanceSelected;
    private String currentQuery;


    private static final Comparator<ListItemModel> SMALLEST_DISTANCE_COMPARATOR = new Comparator<ListItemModel>() {
        @Override
        public int compare(ListItemModel a, ListItemModel b) {
            if (a.getMDistanceFromCurrentPosInMeters() == b.getMDistanceFromCurrentPosInMeters())
                return 0;
            return a.getMDistanceFromCurrentPosInMeters() > b.getMDistanceFromCurrentPosInMeters() ? 1 : -1;
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Use data binding to bind all views in this activity
        searchBinding = DataBindingUtil.setContentView(this, R.layout.activity_search);

        places = (RecyclerView) searchBinding.placesList;

        seekbarProg = (TextView) searchBinding.seekbarProgress;

        slider = (Slider) searchBinding.slider;

        wheelChairCheckBox = (AppCompatCheckBox) searchBinding.accessibleCb;

        childFriendlyCheckBox = (AppCompatCheckBox) searchBinding.childFriendlyCb;

        childFriendlyCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setChildFriendly(isChecked);
                filterAndRearrange();
            }
        });

        wheelChairCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setWheelchairAccess(isChecked);
                filterAndRearrange();
            }
        });

        // Currently the cards with locations are being sorted alphabetically
        mAdapter = new ListItemAdapter(this, SMALLEST_DISTANCE_COMPARATOR);

        // Get the device's location for distance calculations
        this.currentLocation = CurrentCoordinates.getCoords();

        // Initialize database reference
        db = FirebaseFirestore.getInstance();

        // Query string is empty in the beginning
        currentQuery = "";

        places.setAdapter(mAdapter);

        // Set the text below the slider
        seekbarProg.setText("Distance: " + (int) slider.getValue() + "km");
        seekbarProg.setTextColor(getResources().getColor(R.color.colorPrimaryDark));

        // List of locations
        final List<Location> locationsToShow = new ArrayList<>();

        // Get all locations from the db
        db.collection("locations")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, String.valueOf(document.getData().get("name")));
                                String name = (String) document.getData().get("name");
                                String placeType = (String) document.getData().get("placeType");
                                String id = document.getId();
                                // For each location create aa new Location instance and add it to the list
                                LatLng coords = new LatLng((double) document.getData().get("latitude"),
                                        (double) document.getData().get("longitude"));
                                boolean wheelChairAccessible = (boolean) document.getData().get("wheelChairAccessible");
                                boolean childFriendly = (boolean) document.getData().get("childFriendly");
                                boolean cheapEntry = (boolean) document.getData().get("cheapEntry");
                                boolean freeEntry = (boolean) document.getData().get("freeEntry");
                                String thumbnailAddress = (String) document.getData().get("thumbnail");

                                locationsToShow.add(new Location(id, name, placeType, coords,
                                        wheelChairAccessible, childFriendly, cheapEntry, freeEntry, thumbnailAddress));
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                            mModels = new ArrayList<>();
                            for (Location location : locationsToShow) {
                                mModels.add(new ListItemModel(location.getId(), location.getName(),
                                        location.getLocationType(),
                                        location.isWheelChairAccessible(), location.isChildFriendly(),
                                        location.hasCheapEntry(), location.hasFreeEntry(),
                                        location.getThumbnailURL(),
                                        calculateDistance(currentLocation.latitude, location.getLatitude(),
                                                currentLocation.longitude, location.getLongitude())));
                            }
                            mAdapter.add(mModels);
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });


        /* When the user uses the slider to choose max distance, update the list of locations shown */
        slider.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                seekbarProg.setText("Distance: " + (int) slider.getValue() + "km");
                distanceSelected = slider.getValue();

                filterAndRearrange();
            }
        });

        // Set up the toolbar
        Toolbar searchViewToolbar = searchBinding.searchToolbar;

        setSupportActionBar(searchViewToolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);

        Intent intent = getIntent();
        /* If when starting this activity you passed in a key-value pair
         This is how you retrieve it */
        String value = intent.getStringExtra("key"); //if it's a string you stored.
    }

    /**
     * Calculate distance between two points in latitude and longitude taking
     * into account height difference (optionally), which I've disabled for now.
     * Uses Haversine method as its base.
     * lat1, lon1 Start point lat2, lon2 End point el1 Start altitude in meters
     * el2 End altitude in meters
     *
     * @returns Distance in Meters
     */
//    public static double calculateDistance(double lat1, double lat2, double lon1,
//                                           double lon2, double el1, double el2) {
    public static double calculateDistance(double lat1, double lat2, double lon1, double lon2) {

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

//        double height = el1 - el2;

        distance = Math.pow(distance, 2);

        return Math.sqrt(distance);
    }

    // This handles creating the magnifying glass expanding search field
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);

        // Associate searchable configuration with the SearchView
        MenuItem searchItem = menu.findItem(R.id.search_item);
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) searchItem.getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        // When the query text changes - update the locations shown accordingly
        SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
            public boolean onQueryTextChange(String newText) {
                currentQuery = newText;
                filterAndRearrange();
                return true;
            }

            /* We never submit the query as the users
             don't have an enter button, but this is a necessary method */
            public boolean onQueryTextSubmit(String query) {
                // **Here you can get the value "query" which is entered in the search box.**
                return false;
            }
        };

        // Use the above custom query Listener
        searchView.setOnQueryTextListener(queryTextListener);

        // Returns true, because we are using a custom listener
        return true;
    }


    /* Handle a press on the map button */
    public void openMaps(View view) {
        Intent myIntent = new Intent(SearchableActivity.this, MapsActivity.class);
        // Pass optional parameters to the map activity
        myIntent.putExtra("key", "yolo"); //Optional parameters
        SearchableActivity.this.startActivity(myIntent);
    }

    /* Handle the popup for more filters */
    public void openOptions(View view) {
        // Create an instance of the dialogFragment fragment and show it
        /* Get the values for each filter and send them to the popup as an argument (order matters) */
        dialogFragment = new OptionsDialogFragment(this.cheapEntry, this.freeEntry);
        dialogFragment.show(getSupportFragmentManager(), "OptionsDialogFragment");
    }

    @Override
    public void onDialogPositiveClick(Bundle bundle) {
        filterAndRearrange();
    }

    @Override
    public void onDialogNegativeClick(Bundle bundle) {
        filterAndRearrange();
    }


    /* Methods for handling the different checkboxes being clicked */
    public void setWheelchairAccess(boolean wheelchairAccess) {
        this.wheelchairAccess = wheelchairAccess;
    }

    public void setChildFriendly(boolean childFriendly) {
        this.childFriendly = childFriendly;
    }

    public void setCheapEntry(boolean cheapEntry) {
        this.cheapEntry = cheapEntry;
    }

    public void setFreeEntry(boolean freeEntry) {
        this.freeEntry = freeEntry;
    }


    /* Filters the locations from the db according to user input (search text and distance/filters) */
    private static List<ListItemModel> filter(List<ListItemModel> models, String query,
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

    public void filterAndRearrange() {
        final List<ListItemModel> filteredModelList =
                filter(mModels, currentQuery, distanceSelected,
                        wheelchairAccess, childFriendly, cheapEntry, freeEntry);
        mAdapter.replaceAll(filteredModelList);
        places.scrollToPosition(0);
    }

    /* Handle a place card being pressed and take the user to the according Location page */
    public void openPlacePage(View view) {
        TextView textView = (TextView) view.findViewById(R.id.list_item_title);
        String placeName = textView.getText().toString();
        Toast.makeText(this, placeName, Toast.LENGTH_SHORT).show();
        Intent myIntent = new Intent(SearchableActivity.this, MapsActivity.class);
        myIntent.putExtra("openPlacePage", placeName); //Optional parameters
        SearchableActivity.this.startActivity(myIntent);
    }
}