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
import com.nearchitectural.databinding.ActivitySearchBinding;
import com.nearchitectural.fragments.OptionsDialogFragment;
import com.nearchitectural.models.Location;
import com.nearchitectural.ui.models.ListItemModel;
import com.nearchitectural.utilities.CurrentCoordinates;
import com.nearchitectural.utilities.DistanceCalculator;
import com.nearchitectural.utilities.Filters;
import com.nearchitectural.utilities.TagID;
import com.nearchitectural.utilities.TagMapper;
import com.nearchitectural.utilities.comparators.ShortestDistanceComparator;

import java.util.ArrayList;
import java.util.List;

/**author: Kristyan Doykov, Joel Bell-Wilding
 * since: TODO: Fill in date
 * version: 1.1
 * purpose: Activity which handles searching through list of locations through numerous approaches
 * i.e. text search, tag filtration, distance to user
 */
public class SearchableActivity extends AppCompatActivity implements OptionsDialogFragment.OptionsDialogListener {

    private TagMapper searchTagMapper; // Utility object used to aid in handling search by tag
    private LatLng currentLocation; // User's current latitude and longitude
    public static final String TAG = "SearchableActivity"; // Tag used for logging status of application

    private ActivitySearchBinding searchBinding; // Binds all views in this activity

    // Objects representing UI elements
    private RecyclerView places;
    private TextView seekbarProg;
    private Slider slider;
    private AppCompatCheckBox wheelChairCheckBox;
    private AppCompatCheckBox childFriendlyCheckBox;

    private List<ListItemModel> mModels; // Location cards
    private ListItemAdapter mAdapter; // Adapter for filtering location cards

    private FirebaseFirestore db; // Represents the database storing location information
    private double distanceSelected; // The user-selected distance outside of which locations will not be displayed
    private String currentQuery; // The string value stored in the text search bar

    // Handles initialisation of activity upon opening
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Use data binding to bind all views in this activity
        searchBinding = DataBindingUtil.setContentView(this, R.layout.activity_search);

        // Initialises UI elements
        places = searchBinding.placesList;
        seekbarProg = searchBinding.seekbarProgress;
        slider = searchBinding.slider;
        wheelChairCheckBox = searchBinding.accessibleCb;
        childFriendlyCheckBox = searchBinding.childFriendlyCb;

        // Currently the cards with locations are being sorted by distance from the user
        mAdapter = new ListItemAdapter(this, new ShortestDistanceComparator());
        mModels = new ArrayList<>();

        // Get the device's location for distance calculations
        this.currentLocation = CurrentCoordinates.getCoords();

        // Initialize database reference
        db = FirebaseFirestore.getInstance();

        // Sets all active search tags to false (i.e. not activated by the user)
        this.searchTagMapper = new TagMapper();

        // Query string is empty in the beginning
        currentQuery = "";

        // Adapter to apply the data
        places.setAdapter(mAdapter);

        createLocationsCardsFromDatabase();

        /* Set listeners to be able to apply when the user checks/unchecks a CheckBox */
        childFriendlyCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setTag(TagID.CHILD_FRIENDLY, isChecked, "Wheelchair Accessible");
                filterAndRearrange();
            }
        });

        wheelChairCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setTag(TagID.WHEELCHAIR_ACCESSIBLE, isChecked, "Child Friendly");
                filterAndRearrange();
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

    // Creates location cards from the database
    private void createLocationsCardsFromDatabase() {

        // Read in all locations from the database
        db.collection("locations")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {

                            Location nextLocation; // Represents next location to read in from database
                            TagMapper locationTagMapper; // Maps tags to boolean values for each location

                            for (QueryDocumentSnapshot document : task.getResult()) {

                                // For each location in database create a new Location instance and add it to the list
                                String id = document.getId();

                                String name = document.getData().get("name") == null ?
                                        "Unknown" : (String) document.getData().get("name");

                                String placeType = document.getData().get("placeType") == null ?
                                        "Unknown" : (String) document.getData().get("placeType");

                                LatLng coords = document.getData().get("latitude") == null || document.getData().get("longitude") == null ?
                                        new LatLng(0, 0) : new LatLng((double) document.getData().get("latitude"),
                                        (double) document.getData().get("longitude"));

                                // Stores information about which tags are active for this location
                                locationTagMapper = new TagMapper(document);

                                String thumbnailAddress = document.getData().get("thumbnail") == null ?
                                        "" : (String) document.getData().get("thumbnail");

                                // Instantiate new location using database values
                                nextLocation = new Location(id, name, placeType, coords,
                                        locationTagMapper.getTagValuesMap(), thumbnailAddress);

                                // Add location to location cards list
                                mModels.add(new ListItemModel(nextLocation,
                                                DistanceCalculator.calculateDistance(currentLocation.latitude, nextLocation.getLatitude(),
                                                        currentLocation.longitude, nextLocation.getLongitude())));
                            }
                            mAdapter.add(mModels); // Add location cards to adapter
                        } else {
                            Log.d(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    // Handles creating the magnifying glass expanding search field
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
        myIntent.putExtra("key", "value_here"); //Optional parameters
        SearchableActivity.this.startActivity(myIntent);
    }

    /* Handle a place card being pressed and take the user to the according Location page */
    public void openPlacePage(View view) {
        TextView textView = view.findViewById(R.id.list_item_title);
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
        DialogFragment dialogFragment = new OptionsDialogFragment(searchTagMapper);
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

    // Sets a tag to active or inactive
    public void setTag(TagID tag, boolean isActive, String tagName) {
        this.searchTagMapper.addTagToMapper(tag, isActive, tagName);
    }

    /* Apply apply method and replace the current list with the list of matches and rearrange */
    public void filterAndRearrange() {
        final List<ListItemModel> filteredModelList =
                Filters.apply(mModels, currentQuery, distanceSelected,
                        searchTagMapper.getTagValuesMap());
        mAdapter.replaceAll(filteredModelList);
        places.scrollToPosition(0);
    }
}