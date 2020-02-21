package com.nearchitectural.ui.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.slider.Slider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.nearchitectural.R;
import com.nearchitectural.databinding.ActivitySearchBinding;
import com.nearchitectural.databinding.ActivitySearchLandscapeBinding;
import com.nearchitectural.ui.adapters.ListItemAdapter;
import com.nearchitectural.ui.fragments.OptionsDialogFragment;
import com.nearchitectural.ui.models.ListItemModel;
import com.nearchitectural.ui.models.SearchableActivityViewModel;
import com.nearchitectural.utilities.CurrentCoordinates;
import com.nearchitectural.utilities.DatabaseExtractor;
import com.nearchitectural.utilities.Filter;
import com.nearchitectural.utilities.TagID;
import com.nearchitectural.utilities.TagMapper;
import com.nearchitectural.utilities.comparators.ShortestDistanceComparator;
import com.nearchitectural.utilities.models.Location;

import java.util.ArrayList;
import java.util.List;

/* Author:  Kristyan Doykov, Joel Bell-Wilding
 * Since:   TODO: Fill in date
 * Version: 1.1
 * Purpose: Activity which handles searching through list of locations through numerous approaches
 *          i.e. text search, tag filtration, distance to user
 */
public class SearchableActivity extends AppCompatActivity implements OptionsDialogFragment.OptionsDialogListener, NavigationView.OnNavigationItemSelectedListener {

    private TagMapper searchTagMapper; // Utility object used to aid in handling search by tag
    private LatLng currentLocation; // User's current latitude and longitude
    public static final String TAG = "SearchableActivity"; // Tag used for logging status of application

    private ActivitySearchBinding searchBinding; // Binds all views in this activity (portrait orientation)
    private ActivitySearchLandscapeBinding searchLandscapeBinding; // Binds all views in this activity (landscape orientation)
    private SearchableActivityViewModel mViewModel; // UI model for searchable activity
    private int columns; // Number of columns needed for location cards

    // Objects representing UI elements
    private RecyclerView places;
    private TextView seekbarProg;
    private Slider slider;
    private AppCompatCheckBox wheelChairCheckBox;
    private AppCompatCheckBox childFriendlyCheckBox;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private TextView actionBarTitle;

    private List<ListItemModel> mModels; // Location cards
    private ListItemAdapter mAdapter; // Adapter for filtering location cards

    private FirebaseFirestore db; // Represents the database storing location information
    private double distanceSelected; // The user-selected distance outside of which locations will not be displayed
    private String currentQuery; // The string value stored in the text search bar
    private List<Location> locationsToShow; // List of all locations to show

    // Handles initialisation of activity upon opening
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Use data binding to bind all views in this activity
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            columns = 1;
            searchBinding = DataBindingUtil.setContentView(this, R.layout.activity_search);
        } else {
            columns = 2;
            searchLandscapeBinding = DataBindingUtil.setContentView(this, R.layout.activity_search_landscape);
        }

        mViewModel = ViewModelProviders.of(this).get(SearchableActivityViewModel.class);

        if (searchBinding != null) {

            // Set up the toolbar
            Toolbar searchViewToolbar = searchBinding.searchToolbar;
            setSupportActionBar(searchViewToolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(false);

            actionBarTitle = searchBinding.actionBarTitle;

            actionBarTitle.setText("Search");

            // Map the drawer pop up
            drawer = searchBinding.drawerLayout;
            // Map the drawer menu
            navigationView = searchBinding.navView;
            // Set the menu to use the listener provided in this class
            navigationView.setNavigationItemSelectedListener(this);

            // The "hamburger" button for the menu
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, searchViewToolbar,
                    R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.addDrawerListener(toggle);

            // This is to make sure the button closes/opens the menu accordingly
            toggle.syncState();

            places = searchBinding.placesList;

            places.setLayoutManager(new GridLayoutManager(this, columns));

            seekbarProg = searchBinding.seekbarProgress;

            slider = searchBinding.slider;

            wheelChairCheckBox = searchBinding.accessibleCb;

            childFriendlyCheckBox = searchBinding.childFriendlyCb;

        } else {
            // Set up the toolbar
            Toolbar searchViewToolbar = searchLandscapeBinding.searchToolbar;
            setSupportActionBar(searchViewToolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(false);

            actionBarTitle = searchLandscapeBinding.actionBarTitle;

            actionBarTitle.setText("Search");

            // Map the drawer pop up
            drawer = searchLandscapeBinding.drawerLayout;
            // Map the drawer menu
            navigationView = searchLandscapeBinding.navView;
            // Set the menu to use the listener provided in this class
            navigationView.setNavigationItemSelectedListener(this);

            // The "hamburger" button for the menu
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, searchViewToolbar,
                    R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.addDrawerListener(toggle);

            // This is to make sure the button closes/opens the menu accordingly
            toggle.syncState();

            places = searchLandscapeBinding.placesList;

            places.setLayoutManager(new GridLayoutManager(this, columns));

            seekbarProg = searchLandscapeBinding.seekbarProgress;

            slider = searchLandscapeBinding.slider;

            wheelChairCheckBox = searchLandscapeBinding.accessibleCb;

            childFriendlyCheckBox = searchLandscapeBinding.childFriendlyCb;
        }

        /* Set listeners to be able to apply when the user checks/unchecks a CheckBox */
        childFriendlyCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setTag(TagID.CHILD_FRIENDLY, isChecked);
                filterAndRearrange();
            }
        });

        wheelChairCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setTag(TagID.WHEELCHAIR_ACCESSIBLE, isChecked);
                filterAndRearrange();
            }
        });

        // Sets all active search tags to false (i.e. not activated by the user)
        this.searchTagMapper = new TagMapper();

        // Currently the cards with locations are being sorted by distance from the user
        mAdapter = new ListItemAdapter(this, new ShortestDistanceComparator());
        // Get the device's location for distance calculations
        this.currentLocation = CurrentCoordinates.getCoords();
        // Initialize database reference
        db = FirebaseFirestore.getInstance();
        // Query string is empty in the beginning
        currentQuery = "";
        // Adapter to apply the data
        places.setAdapter(mAdapter);

        locationsToShow = new ArrayList<>();

        mViewModel.getLocationsToShow().observe(this, new Observer<List<Location>>() {
            @Override
            public void onChanged(List<Location> locations) {
                locationsToShow.addAll(locations);
            }
        });

        mModels = new ArrayList<>();

        mViewModel.getmModels().observe(this, new Observer<List<ListItemModel>>() {
            @Override
            public void onChanged(List<ListItemModel> listItemModels) {
                mModels.addAll(listItemModels);
                filterAndRearrange();
            }
        });

        mAdapter.add(mModels);

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

        Intent intent = getIntent();
        /* If when starting this activity you passed in a key-value pair
         This is how you retrieve it */
        String value = intent.getStringExtra("key"); //if it's a string you stored.
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
        // Make sure search field takes up whole action bar even in landscape
        searchView.setMaxWidth(Integer.MAX_VALUE);
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

    /* Handle a place card being pressed and take the user to the according Location page */
    public void openLocationPage(View view) {
        TextView textView = view.findViewById(R.id.list_item_title);
        db.collection("locations").whereEqualTo("name", textView.getText())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Location location = DatabaseExtractor.extractLocation(document);

                                Intent myIntent = new Intent(SearchableActivity.this, MapsActivity.class);
                                myIntent.putExtra("openLocationPage", location.getName());
                                Gson gson = new Gson();
                                String locationJSON = gson.toJson(location);
                                myIntent.putExtra("location", locationJSON);
                                SearchableActivity.this.startActivity(myIntent);
                                Log.d(TAG, "goToLocation");
                            }
                        } else {
                            /* If this block executes, either no document was found
                             * matching the search name or some other error occurred*/
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

    public void openFragment(String fragmentName){
        Intent myIntent = new Intent(SearchableActivity.this, MapsActivity.class);
        myIntent.putExtra("openFragment", fragmentName); //Optional parameters
        SearchableActivity.this.startActivity(myIntent);
    }

    /* Handle the popup for more filters */
    public void openMoreFilters(View view) {
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
    public void setTag(TagID tag, boolean isActive) {
        this.searchTagMapper.addTagToMapper(tag, isActive);
    }

    /* Apply apply method and replace the current list with the list of matches and rearrange */
    public void filterAndRearrange() {
        List<ListItemModel> filteredModelList =
                Filter.apply(mModels, currentQuery, distanceSelected,
                        searchTagMapper.getTagValuesMap());
        mAdapter.replaceAll(filteredModelList);
        places.scrollToPosition(0);
    }

    /* If the back button is pressed */
    @Override
    public void onBackPressed() {
        // Close the nav drawer if open
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        super.onBackPressed();
    }

    /* Manages the drawer menu click events */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_timeline:
                drawer.closeDrawer(GravityCompat.START);
                openFragment("Timeline");
                break;
            case R.id.nav_map:
                drawer.closeDrawer(GravityCompat.START);
                openFragment("Map");
                break;

            case R.id.nav_settings:
                drawer.closeDrawer(GravityCompat.START);
                openFragment("Settings");
                break;

            case R.id.nav_info:
                drawer.closeDrawer(GravityCompat.START);
                openFragment("About");
                break;

            case R.id.nav_help:
                drawer.closeDrawer(GravityCompat.START);
                openFragment("Help");
                break;
        }
        return true;
    }
}