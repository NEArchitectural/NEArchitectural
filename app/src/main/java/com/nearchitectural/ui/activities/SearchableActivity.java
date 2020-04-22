package com.nearchitectural.ui.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.slider.Slider;
import com.nearchitectural.R;
import com.nearchitectural.databinding.ActivitySearchBinding;
import com.nearchitectural.databinding.ActivitySearchLandscapeBinding;
import com.nearchitectural.ui.adapters.SearchResultAdapter;
import com.nearchitectural.ui.fragments.TagSelectorFragment;
import com.nearchitectural.ui.models.LocationModel;
import com.nearchitectural.ui.models.ModelController;
import com.nearchitectural.utilities.CurrentCoordinates;
import com.nearchitectural.utilities.Filter;
import com.nearchitectural.utilities.Settings;
import com.nearchitectural.utilities.TagMapper;
import com.nearchitectural.utilities.comparators.AlphabeticComparator;
import com.nearchitectural.utilities.comparators.ShortestDistanceComparator;
import com.nearchitectural.utilities.models.TagID;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/* Author:  Kristyan Doykov, Joel Bell-Wilding
 * Since:   12/12/19
 * Version: 1.2
 * Purpose: Activity which handles searching through list of locations through numerous approaches
 *          i.e. text search, tag filtration, distance to user
 */
public class SearchableActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static final String TAG = "SearchableActivity"; // Tag used for logging status of application

    // LAYOUT ELEMENTS
    private RecyclerView searchResultsRecyclerView;
    private TextView sliderText;
    private Slider slider;
    private AppCompatCheckBox wheelChairCheckBox;
    private AppCompatCheckBox likedLocationsCheckBox;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private TextView actionBarTitle;
    private TextView resultsCount;
    private Toolbar searchViewToolbar;
    private ProgressBar progressBar;

    private SearchResultAdapter searchResultsAdapter; // Adapter for filtering search results
    private ModelController searchResultsController; // ModelController to maintain/update search results
    private Map<String, LocationModel> modelMap; // Map of location IDs to their models
    private double distanceSelected; // The user-selected distance outside of which locations will not be displayed
    private String currentQuery; // The string value stored in the text search bar
    private TagMapper searchTagMapper; // Utility object used to aid in handling search by tag
    private static final String SEARCH_QUERY_KEY = "search_query"; // Bundle key for storing/retrieving the search query string

    /* Variables used to determine if search results must be updated (i.e. if a
    * current value is different from its 'last' value, search results need updating */
    private HashSet<String> lastLikedLocationIDs;
    private double lastLatitude;
    private double lastLongitude;

    private String openedLocationID; // Location ID of location being viewed when search result is tapped

    // Handles initialisation of activity upon opening
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Apply user's chosen font size across activity
        getTheme().applyStyle(Settings.getInstance().getFontSize(), true);

        // BINDING AND ORIENTATION VARIABLES
        ActivitySearchBinding searchBinding; // Binds all views in this activity (portrait orientation)
        ActivitySearchLandscapeBinding searchLandscapeBinding; // Binds all views in this activity (landscape orientation)
        int columns; // Number of columns needed for location cards (search results)

        // Use data binding to bind all UI elements in this activity for both orientations
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {

            columns = 1; // One column of search results
            searchBinding = DataBindingUtil.setContentView(this, R.layout.activity_search);

            // Binding all UI elements for portrait orientation (default)
            searchViewToolbar = searchBinding.searchToolbar;
            actionBarTitle = searchBinding.actionBarTitle;
            drawer = searchBinding.drawerLayout;
            navigationView = searchBinding.navView;
            searchResultsRecyclerView = searchBinding.placesList;
            sliderText = searchBinding.sliderSearchText;
            slider = searchBinding.slider;
            wheelChairCheckBox = searchBinding.accessibleCb;
            likedLocationsCheckBox = searchBinding.likedLocationsCb;
            resultsCount = searchBinding.resultsCount;
            progressBar = searchBinding.progressBar;

        } else {

            columns = 2; // Two columns of search results
            searchLandscapeBinding = DataBindingUtil.setContentView(this, R.layout.activity_search_landscape);

            // Binding all UI elements for landscape orientation
            searchViewToolbar = searchLandscapeBinding.searchToolbar;
            actionBarTitle = searchLandscapeBinding.actionBarTitle;
            drawer = searchLandscapeBinding.drawerLayout;
            navigationView = searchLandscapeBinding.navView;
            searchResultsRecyclerView = searchLandscapeBinding.placesList;
            sliderText = searchLandscapeBinding.sliderSearchText;
            slider = searchLandscapeBinding.slider;
            wheelChairCheckBox = searchLandscapeBinding.accessibleCb;
            likedLocationsCheckBox = searchLandscapeBinding.likedLocationsCb;
            resultsCount = searchLandscapeBinding.resultsCount;
            progressBar = searchLandscapeBinding.progressBar;
        }

        // Set the view model for displaying search results
        searchResultsController = ViewModelProviders.of(this).get(ModelController.class);

        // Initialise the action bar (top bar)
        setSupportActionBar(searchViewToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(false);
            getSupportActionBar().setElevation(10);
        }
        actionBarTitle.setText(R.string.search);

        // Enable/disable the distance slider based on whether location is enabled
        if (!Settings.getInstance().locationPermissionsAreGranted()) {
            sliderText.setText(R.string.slider_disabled);
        } else {
            sliderText.setText(R.string.slider_unset_text);
        }
        slider.setEnabled(Settings.getInstance().locationPermissionsAreGranted());

        // Set the menu to use the listener provided in this class
        navigationView.setNavigationItemSelectedListener(this);

        // The "hamburger" button for the menu
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, searchViewToolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        // Ensure the hamburger button closes/opens the menu accordingly
        toggle.syncState();

        // Determines how many columns the results list uses (depending on orientation)
        searchResultsRecyclerView.setLayoutManager(new GridLayoutManager(this, columns));

        // Sets all active search tags to false (i.e. not activated by the user)
        searchTagMapper = new TagMapper();

        /* Set listeners to be able to apply tags when the user checks/unchecks a checkbox */
        likedLocationsCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setTag(TagID.LIKED_BY_YOU, isChecked);
                filterAndRearrange();
                searchResultsRecyclerView.scrollToPosition(0);
            }
        });

        wheelChairCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setTag(TagID.WHEELCHAIR_ACCESSIBLE, isChecked);
                filterAndRearrange();
                searchResultsRecyclerView.scrollToPosition(0);
            }
        });

        // If locations are granted order by results by distance to user, else order alphabetically by name
        if (Settings.getInstance().locationPermissionsAreGranted()) {
            searchResultsAdapter = new SearchResultAdapter(this, new ShortestDistanceComparator());
        } else {
            searchResultsAdapter = new SearchResultAdapter(this, new AlphabeticComparator());
        }

        // Query string is empty in the beginning
        currentQuery = "";
        // Set adapter for recycler view
        searchResultsRecyclerView.setAdapter(searchResultsAdapter);

        // Create locations models using live data (results will appear when retrieved from database)
        modelMap = new HashMap<>();
        searchResultsController.updateAllLocationModels().observe(this, new Observer<Map<String, LocationModel>>() {
            @Override
            public void onChanged(Map<String, LocationModel> modelIDMap) {
                modelMap = modelIDMap;
                // Once location loaded, hide progress bar and show search results
                progressBar.setVisibility(View.GONE);
                searchResultsRecyclerView.setVisibility(View.VISIBLE);
                filterAndRearrange();
            }
        });

        // Add all models to the search results adapter
        searchResultsAdapter.add(new ArrayList<>(modelMap.values()));

        /* When the user uses the slider to choose max distance, update the list of locations shown */
        slider.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {

                distanceSelected = Double.MAX_VALUE; // Initially set distance to max possible value

                if (slider.getValue() == 10) {
                    // Show all locations (as distance is > 10 distance units)
                    sliderText.setText(String.format(getString(R.string.slider_max_distance),
                            Settings.getInstance().getDistanceUnit().getDisplayName()));

                } else if (slider.getValue() != 0) {
                    // Show locations within the selected distance
                    distanceSelected = slider.getValue();
                    sliderText.setText(String.format(getString(R.string.slider_distance),
                            (int) distanceSelected,
                            Settings.getInstance().getDistanceUnit().getDisplayName()));

                } else {
                    // Slider not moved - show all locations
                    sliderText.setText(R.string.slider_unset_text);
                }
                filterAndRearrange();
                searchResultsRecyclerView.scrollToPosition(0);
            }
        });
    }

    // Handles creating the expanding search field on press of the magnifying glass icon
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);

        // Associate searchable configuration with the SearchView
        MenuItem searchItem = menu.findItem(R.id.search_item);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) searchItem.getActionView();

        // Sets the query upon UI state restore (i.e. when the user changes screen orientation)
        if (!currentQuery.equals("")) {
            searchItem.expandActionView();
            searchView.setQuery(currentQuery, false);
        }

        // Make sure search field takes up whole action bar even in landscape
        searchView.setMaxWidth(Integer.MAX_VALUE);
        assert searchManager != null;
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        // When the query text changes - update the locations shown accordingly
        SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
            public boolean onQueryTextChange(String newText) {
                currentQuery = newText;
                filterAndRearrange();
                return true;
            }

            /* No submission is made by the user so simply return false */
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
        };

        // Use the above custom query Listener
        searchView.setOnQueryTextListener(queryTextListener);

        // Returns true, because we are using a custom listener
        return true;
    }

    @Override
    protected void onPause() {
        // When activity is paused, record the last known values of each variable
        lastLikedLocationIDs = Settings.getInstance().getLikedLocations();
        lastLatitude = CurrentCoordinates.getCoords().latitude;
        lastLongitude = CurrentCoordinates.getCoords().longitude;
        super.onPause();
    }

    @Override
    protected void onResume() {
        // If location has been liked/unliked after location page opened, update search result for location
        if (openedLocationID != null && !lastLikedLocationIDs.equals(Settings.getInstance().getLikedLocations())) {
            searchResultsController.updateLocationModel(openedLocationID);
        }
        openedLocationID = null;

        // Determine if user's location has significantly changed from previous interaction and update results if so
        CurrentCoordinates.getInstance().getDeviceLocation(this, new CurrentCoordinates.LocationCallback<LatLng>() {
            @Override
            public void onLocationRetrieved(LatLng coordinates) {
                double threshold = 0.001;
                /* Update results if there is a significant difference between user's last known position
                 * and user's current position */
                if ((Math.abs(lastLatitude-CurrentCoordinates.getCoords().latitude) > threshold)
                        || Math.abs(lastLongitude-CurrentCoordinates.getCoords().longitude) > threshold) {
                    searchResultsController.updateAllLocationModels();
                }
            }
        });

        super.onResume();
    }

    /* Handle a location card being pressed and take the user to the according Location page */
    public void openLocationPage(View view) {
        openedLocationID = view.getTag().toString();
        // Open a location page for the location with the provided ID
        Intent myIntent = new Intent(SearchableActivity.this, MapsActivity.class);
        myIntent.putExtra(getString(R.string.navigation_location_page), view.getTag().toString());
        SearchableActivity.this.startActivity(myIntent);
        Log.d(TAG, "goToLocation");
    }

    // Opens the appropriate fragment corresponding to the provided fragment name
    public void openFragment(int fragmentID) {
        Intent myIntent = new Intent(SearchableActivity.this, MapsActivity.class);
        myIntent.putExtra(getString(R.string.navigation_open_fragment), fragmentID); // Optional parameters
        this.startActivity(myIntent);
    }

    /* Handle the popup for more tags*/
    public void openTagSelector(View view) {
        // Remove tags that are handled through separate UI elements
        final boolean likedByYou = searchTagMapper.getTagValuesMap().get(TagID.LIKED_BY_YOU);
        final boolean wheelchairAccessible = searchTagMapper.getTagValuesMap().get(TagID.WHEELCHAIR_ACCESSIBLE);
        searchTagMapper.removeTagFromMapper(TagID.LIKED_BY_YOU);
        searchTagMapper.removeTagFromMapper(TagID.WHEELCHAIR_ACCESSIBLE);

        // Create an instance of the tag selector fragment and show it
        TagSelectorFragment tagSelector = new TagSelectorFragment(searchTagMapper);
        tagSelector.setDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                // Re-add tags that are handled separately and filter results
                searchTagMapper.addTagToMapper(TagID.LIKED_BY_YOU, likedByYou);
                searchTagMapper.addTagToMapper(TagID.WHEELCHAIR_ACCESSIBLE, wheelchairAccessible);
                filterAndRearrange();
            }
        });
        tagSelector.show(getSupportFragmentManager(), "TagSelectorFragment");
    }

    // Sets a tag to active or inactive
    public void setTag(TagID tag, boolean isActive) {
        searchTagMapper.addTagToMapper(tag, isActive);
    }

    /* Use Filter with current search criteria and update search results */
    public void filterAndRearrange() {
        List<LocationModel> filteredModelList =
                Filter.apply(new ArrayList<>(modelMap.values()), currentQuery, distanceSelected,
                        searchTagMapper.getTagValuesMap());
        searchResultsAdapter.replaceAll(filteredModelList);

        // Edit the number of matches upon filtering
        String resultsText = getResources().getQuantityString(R.plurals.search_results_count,
                searchResultsAdapter.getItemCount(), searchResultsAdapter.getItemCount());
        resultsCount.setText(resultsText);
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
        // Switch statement to handle which fragment should be opened
        switch (item.getItemId()) {
            case R.id.nav_timeline:
                openFragment(R.string.navigation_timeline);
                break;

            case R.id.nav_map:
                openFragment(R.string.navigation_map);
                break;

            case R.id.nav_settings:
                openFragment(R.string.navigation_settings);
                break;

            case R.id.nav_info:
                openFragment(R.string.navigation_about);
                break;

            case R.id.nav_help:
                openFragment(R.string.navigation_help);
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save the current search query for state change
        for (TagID tag : TagID.values()) {
            outState.putBoolean(tag.name(), searchTagMapper.getTagValuesMap().get(tag));
        }
        outState.putString(SEARCH_QUERY_KEY, currentQuery);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // Restore the search query on state change
        for (TagID tag : TagID.values()) {
            searchTagMapper.addTagToMapper(tag, savedInstanceState.getBoolean(tag.name()));
        }
        currentQuery = savedInstanceState.getString(SEARCH_QUERY_KEY);
    }
}