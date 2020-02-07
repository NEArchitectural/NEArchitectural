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
import com.nearchitectural.R;
import com.nearchitectural.adapters.ListItemAdapter;
import com.nearchitectural.comparators.Comparators;
import com.nearchitectural.databinding.ActivitySearchBinding;
import com.nearchitectural.fragments.OptionsDialogFragment;
import com.nearchitectural.models.Location;
import com.nearchitectural.ui.models.ListItemModel;
import com.nearchitectural.utils.CalculateDistance;
import com.nearchitectural.utils.CurrentCoordinates;
import com.nearchitectural.utils.Filters;

import java.util.ArrayList;
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

        /* Set listeners to be able to apply when the user checks/unchecks a CheckBox */
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

        // Currently the cards with locations are being sorted by distance from the user
        mAdapter = new ListItemAdapter(this, Comparators.SMALLEST_DISTANCE_COMPARATOR);

        // Get the device's location for distance calculations
        this.currentLocation = CurrentCoordinates.getCoords();

        // Initialize database reference
        db = FirebaseFirestore.getInstance();

        // Query string is empty in the beginning
        currentQuery = "";

        // Adapter to apply the data
        places.setAdapter(mAdapter);

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

                                // For each location create aa new Location instance and add it to the list
                                String id = document.getId();

                                String name = document.getData().get("name") == null ?
                                        "Unknown" : (String) document.getData().get("name");

                                String placeType = document.getData().get("placeType") == null ?
                                        "Unknown" : (String) document.getData().get("placeType");

                                LatLng coords = document.getData().get("latitude") == null || document.getData().get("longitude") == null ?
                                        new LatLng(0, 0) : new LatLng((double) document.getData().get("latitude"),
                                        (double) document.getData().get("longitude"));

                                boolean wheelChairAccessible = document.getData().get("wheelChairAccessible") != null
                                        && (boolean) document.getData().get("wheelChairAccessible");

                                boolean childFriendly = document.getData().get("childFriendly") != null
                                        && (boolean) document.getData().get("childFriendly");

                                boolean cheapEntry = document.getData().get("cheapEntry") != null
                                        && (boolean) document.getData().get("cheapEntry");

                                boolean freeEntry = document.getData().get("freeEntry") != null
                                        && (boolean) document.getData().get("freeEntry");
                                String thumbnailAddress = document.getData().get("thumbnail") == null ?
                                        "" : (String) document.getData().get("thumbnail");

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
                                        CalculateDistance.calculateDistance(currentLocation.latitude, location.getLatitude(),
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
                if (slider.getValue() != 0) {
                    seekbarProg.setText("Distance: " + (int) slider.getValue() + "km");
                    if (slider.getValue() == 10) {
                        seekbarProg.setText("Distance: over 10km");
                        distanceSelected = 100;
                    } else {
                        distanceSelected = slider.getValue();
                    }
                } else {
                    seekbarProg.setText("");
                    distanceSelected = 100;
                }
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

    /* Handle a place card being pressed and take the user to the according Location page */
    public void openPlacePage(View view) {
        TextView textView = (TextView) view.findViewById(R.id.list_item_title);
        String placeName = textView.getText().toString();
        Toast.makeText(this, placeName, Toast.LENGTH_SHORT).show();
        Intent myIntent = new Intent(SearchableActivity.this, MapsActivity.class);
        myIntent.putExtra("openPlacePage", placeName); //Optional parameters
        SearchableActivity.this.startActivity(myIntent);
    }

    /* Handle the popup for more filters */
    public void openOptions(View view) {
        // Create an instance of the dialogFragment fragment and show it
        /* Get the values for each apply and send them to the popup as an argument (order matters) */
        dialogFragment = new OptionsDialogFragment(this.cheapEntry, this.freeEntry);
        dialogFragment.show(getSupportFragmentManager(), "OptionsDialogFragment");
    }


    /* Handlers for the closing of the pop-up for more filters */
    @Override
    public void onDialogPositiveClick(Bundle bundle) {
        filterAndRearrange();
    }

    @Override
    public void onDialogNegativeClick(Bundle bundle) {
        filterAndRearrange();
    }


    /* Methods for handling the different checkboxes being set */
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

    /* Apply apply method and replace the current list with the list of matches and rearrange */
    public void filterAndRearrange() {
        final List<ListItemModel> filteredModelList =
                Filters.apply(mModels, currentQuery, distanceSelected,
                        wheelchairAccess, childFriendly, cheapEntry, freeEntry);
        mAdapter.replaceAll(filteredModelList);
        places.scrollToPosition(0);
    }
}