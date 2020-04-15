package com.nearchitectural.ui.activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;
import com.nearchitectural.R;
import com.nearchitectural.databinding.ActivityMapsBinding;
import com.nearchitectural.ui.fragments.AboutFragment;
import com.nearchitectural.ui.fragments.HelpFragment;
import com.nearchitectural.ui.fragments.LocationFragment;
import com.nearchitectural.ui.fragments.MapFragment;
import com.nearchitectural.ui.fragments.SettingsFragment;
import com.nearchitectural.ui.fragments.TimelineFragment;
import com.nearchitectural.utilities.CurrentCoordinates;
import com.nearchitectural.utilities.Settings;
import com.nearchitectural.utilities.SettingsManager;


/* Author:  Kristiyan Doykov, Joel Bell-Wilding
 * Since:   10/12/19
 * Version: 1.2
 * Purpose: Handle initialisation of application and provide utilities for child fragments
 *          (i.e. location services and permissions handling and state restoration)
 */
public class MapsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MapActivity"; // Tag used for logging status of application

    // LAYOUT ELEMENTS
    private DrawerLayout drawer; // Layout showing the sidebar menu
    private NavigationView navigationView; // View containing the drawer menu
    private TextView actionBarTitle; // Title for activity
    private Toolbar toolbar; // Action (top) bar

    private boolean canRequestLocation; // Boolean to flag whether the application can request the user's location
    private FragmentManager fragmentManager; // Utility for switching between fragments

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Instantiate settings singleton with user's saved settings for use across child fragments
        final SettingsManager settingsManager = new SettingsManager(this);
        settingsManager.retrieveSettings();

        // Boolean which is true only when the application is first loaded
        boolean firstTimeLaunch = !Settings.getInstance().settingsFileExist();
        settingsManager.saveSettings(); // Save settings to set settingsFileExists to true

        super.onCreate(savedInstanceState);

        // Apply user's chosen font size across activity and child fragments
        getTheme().applyStyle(Settings.getInstance().getFontSize(), true);
        // Get user coordinates initially
        CurrentCoordinates.getInstance().getDeviceLocation(this);

        // Binding between to the maps activity layout
        ActivityMapsBinding mapsBinding = DataBindingUtil.setContentView(this, R.layout.activity_maps);

        // Needed for switching back and forth between the different fragments
        fragmentManager = getSupportFragmentManager();

        // Binding layout elements
        toolbar = mapsBinding.toolbar;
        actionBarTitle = mapsBinding.actionBarTitle;
        drawer = mapsBinding.drawerLayout;
        navigationView = mapsBinding.navView;

        // Initialise the action bar (top bar)
        setSupportActionBar(toolbar);
        toolbar.bringToFront();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(false);
            getSupportActionBar().setElevation(10);
        }

        // Set the menu to use the listener provided in this class
        navigationView.setNavigationItemSelectedListener(this);

        // The "hamburger" button for the menu
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        // Ensure the hamburger button closes/opens the menu accordingly
        toggle.syncState();

        // Flag initially set to true allowing the application to make a request
        canRequestLocation = true;

        /* Bundle to hold information needed when handling if a fragment/location
         * page needs to be opened */
        Bundle bundle = getIntent().getExtras();

        // If application does not need to restore state, launch as normal
        if (savedInstanceState == null) {
            // Fragment to be opened based on user's choice (Map by default)
            Fragment fragmentToOpen = MapFragment.newInstance(firstTimeLaunch);

            String openLocationPage = getString(R.string.navigation_location_page);
            String openFragment = getString(R.string.navigation_open_fragment);

            if (bundle != null) {
                // Handles opening a location page
                if (bundle.get(openLocationPage) != null) {
                    fragmentToOpen = LocationFragment.newInstance((String) bundle.get(openLocationPage));
                } else if (bundle.getInt(openFragment) != 0) {
                    // Check if the user tried to open one of the fragments from the Searchable Activity
                    int fragmentID = bundle.getInt(openFragment);
                    bundle.remove(openFragment);
                    // Switch statement to handle which fragment should be opened
                    switch (fragmentID) {
                        case R.string.navigation_map:
                            fragmentToOpen = MapFragment.newInstance(firstTimeLaunch);
                            break;
                        case R.string.navigation_settings:
                            fragmentToOpen = new SettingsFragment();
                            // Prevents popup from being shown upon every opening of settings fragment
                            canRequestLocation = false;
                            break;
                        case R.string.navigation_about:
                            fragmentToOpen = new AboutFragment();
                            break;
                        case R.string.navigation_help:
                            fragmentToOpen = new HelpFragment();
                            break;
                        case R.string.navigation_timeline:
                            fragmentToOpen = new TimelineFragment();
                            break;
                        default:
                            throw new IllegalStateException("Unexpected value: " + getString(fragmentID));
                    }
                }
            }
            openFragment(fragmentToOpen, false);
        }
    }

    // Opens the provided fragment
    public void openFragment(Fragment fragmentToOpen, boolean addToBackStack) {
        if (addToBackStack) {
            // Opens fragment and adds to backstack if boolean is set
            fragmentManager.beginTransaction().replace(R.id.fragment_container,
                    fragmentToOpen).addToBackStack(fragmentToOpen.getTag()).commit();
        } else {
            // else just opens the new fragment
            fragmentManager.beginTransaction().replace(R.id.fragment_container,
                    fragmentToOpen).commit();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        // Handles the user enabling/disabling location from outside the application
        if (hasFocus && locationServicesEnabled() && canRequestLocation) {
            requestLocationPermissions();
        } else if (hasFocus && canRequestLocation) {
            requestLocationPermissions();
        }
    }

    // Requests user both to enabled device location and grant location permissions for application
    public void requestLocationPermissions() {

        // Prompt user to enable device location if not enabled
        if(!locationServicesEnabled()) {
            Settings.getInstance().setLocationPermissionsGranted(false);
            promptUserToEnableLocation();
            handleFragmentPermissions(false);
        }

        // Prompt user to enable location permissions if not granted
        if (!locationPermissionsGranted() && locationServicesEnabled()) {
            Settings.getInstance().setLocationPermissionsGranted(false);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            handleFragmentPermissions(false);
        }
        // Save changes to settings if any made
        new SettingsManager(this).saveSettings();
    }

    /* Method which determines if user has enabled location services (different from granting
     * location permissions)
     * Code taken and adapted from the following link:
     * https://stackoverflow.com/questions/10311834/how-to-check-if-location-services-are-enabled
     */
    public boolean locationServicesEnabled() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        assert lm != null;
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ignored) {}

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ignored) {}

        return gps_enabled || network_enabled;
    }

    // Method which determines if user has granted location permissions for the application
    private boolean locationPermissionsGranted() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    /* Creates an alert dialog prompting the user to enable location services on their device
     * Code taken and adapted from the following link (see locationServicesEnabled comments above):
     * https://stackoverflow.com/questions/10311834/how-to-check-if-location-services-are-enabled
     */
    private void promptUserToEnableLocation() {

        new MaterialAlertDialogBuilder(this, R.style.ThemeOverlay_MaterialComponents)
                .setTitle(R.string.gps_network_not_enabled_title)
                .setMessage(R.string.gps_network_not_enabled)
                .setCancelable(false)
                .setPositiveButton(R.string.open_location_settings, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        // If positive button clicked, open Android settings for enabling location
                        startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), 1);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // If negative button clicked, location permissions cannot be granted
                        Settings.getInstance().setLocationPermissionsGranted(false);
                        // Unset flag to prevent infinite loop of dialogs from popping up
                        canRequestLocation = false;
                    }
                })
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Update permissions across activity, settings and open fragments
        boolean permissionsGranted = locationServicesEnabled() && locationPermissionsGranted();
        Settings.getInstance().setLocationPermissionsGranted(permissionsGranted);
        new SettingsManager(this).saveSettings();
        handleFragmentPermissions(permissionsGranted);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, int[] grantResults) {

        // Unset flag to prevent infinite loop of dialogs from popping up
        canRequestLocation = false;

        // Boolean determining if application has been granted location permissions
        boolean permissionsGranted = grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED && locationServicesEnabled();

        // Update Settings and coordinates to handle the result of permissions request
        Settings.getInstance().setLocationPermissionsGranted(permissionsGranted);
        new SettingsManager(this).saveSettings();
        CurrentCoordinates.getInstance().getDeviceLocation(this);

        handleFragmentPermissions(permissionsGranted);
    }

    // Invokes the location permission handling methods of child fragments which use location
    private void handleFragmentPermissions(boolean permissionsGranted) {
        Fragment activeFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (activeFragment instanceof SettingsFragment) {
            // If Settings page is open, handle location permissions result within fragment
            ((SettingsFragment) activeFragment).locationPermissionsResult(permissionsGranted);
        } else if (activeFragment instanceof MapFragment) {
            // If Maps page is open, handle location permissions result within fragment
            ((MapFragment) activeFragment).locationPermissionsResult(permissionsGranted);
        }
    }

    // Getter for navigationView (navigation side bar)
    public NavigationView getNavigationView() {
        return this.navigationView;
    }

    // Sets new title for action bar
    public void setActionBarTitle(String title){
        actionBarTitle.setText(title);
    }

    // Handles the event of the user pressing the back button
    @Override
    public void onBackPressed() {
        // Close the nav drawer if open
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        super.onBackPressed();
    }

    /* Listener for click events on the nav drawer menu items */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        /* Switch statement to handle the opening of the appropriate fragment for
           each navigation item */

        Fragment selectedFragment = MapFragment.newInstance(false);

        switch (item.getItemId()) {

            case R.id.nav_timeline:
                selectedFragment = new TimelineFragment();
                break;

            case R.id.nav_map:
                selectedFragment = MapFragment.newInstance(false);
                break;

            case R.id.nav_settings:
                selectedFragment = new SettingsFragment();
                break;

            case R.id.nav_info:
                selectedFragment = new AboutFragment();
                break;

            case R.id.nav_help:
                selectedFragment = new HelpFragment();
                break;
        }
        canRequestLocation = true;
        openFragment(selectedFragment, false);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /* Starts search activity when the magnifying glass icon in the action bar is tapped  */
    public void openSearch(View view) {
        Intent myIntent = new Intent(MapsActivity.this, SearchableActivity.class);
        MapsActivity.this.startActivity(myIntent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Update coordinates with last known position when activity is paused
        CurrentCoordinates.getInstance().getDeviceLocation(this);
    }
}
