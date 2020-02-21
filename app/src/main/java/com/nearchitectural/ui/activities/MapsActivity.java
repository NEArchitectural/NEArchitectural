package com.nearchitectural.ui.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.nearchitectural.R;
import com.nearchitectural.databinding.ActivityMapsBinding;
import com.nearchitectural.ui.fragments.AboutFragment;
import com.nearchitectural.ui.fragments.HelpFragment;
import com.nearchitectural.ui.fragments.LocationFragment;
import com.nearchitectural.ui.fragments.MapFragment;
import com.nearchitectural.ui.fragments.SettingsFragment;
import com.nearchitectural.ui.fragments.TimelineFragment;
import com.nearchitectural.utilities.Settings;
import com.nearchitectural.utilities.models.Location;

/* Author:  Kristiyan Doykov
 * Since:   TODO: Fill in date
 * Version: 1.0
 * Purpose: Handle events and presentation of locations on Maps home screen
 */
public class MapsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MapActivity"; // Tag used for logging status of application

    private DrawerLayout drawer; // Layout showing the sidebar menu
    private ActivityMapsBinding mapsBinding; // Binding between to the maps activity layout
    private NavigationView navigationView; // View containing the drawer menu
    private TextView actionBarTitle; // Title for activity
    private FragmentManager fragmentManager; // Utility for switching between fragments
    private LatLng currentLocation; // User's current location

    // Boolean representing whether location permissions have been granted
    public Boolean mLocationPermissionsGranted;
    // Location permission constant representing GPS access has been granted
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    // Location permission constant representing Network access has been granted
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSIONS_REQUEST_CODE = 1234;

    /* Upon creation of the activity, bind all the view components */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mapsBinding = DataBindingUtil.setContentView(this, R.layout.activity_maps);

        // Needed for switching back and forth between the different fragments
        fragmentManager = getSupportFragmentManager();

        Toolbar toolbar = mapsBinding.toolbar;

        // Set the action bar (top bar)
        setSupportActionBar(toolbar);
        toolbar.bringToFront();
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(false);

        actionBarTitle = mapsBinding.actionBarTitle;

        // Map the drawer pop up
        drawer = mapsBinding.drawerLayout;
        // Map the drawer menu
        navigationView = mapsBinding.navView;
        // Set the menu to use the listener provided in this class
        navigationView.setNavigationItemSelectedListener(this);

        // The "hamburger" button for the menu
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);

        // This is to make sure the button closes/opens the menu accordingly
        toggle.syncState();

        // Acquire permissions to use users location
        getLocationPermission();

        /* Check the extras provided with launching this activity, e.g any strings like a place page
         * to be opened, as the Location Fragment is making use of the fragment container in this
         * layout, so when a user presses a certain card on the search view page, this layout will
         * know exactly for which location to open a LocationFragment */
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if (bundle != null) {
            if (bundle.get("openLocationPage") != null) {
                Gson gson = new Gson();
                Location location = gson.fromJson(bundle.getString("location"),Location.class);
                //  Create the new LocationFragment and set the location for the LocationFragment
                LocationFragment lf = new LocationFragment(location);
//                Bundle arguments = new Bundle();
//                lf.setArguments(arguments);
                bundle.remove("openLocationPage");
                fragmentManager.beginTransaction().replace(R.id.fragment_container,
                        lf).commit();
            }
            // Check if the user tried to open one of the fragments from the Searchable Activity
            else if (bundle.getString("openFragment") != null) {
                String fragment = bundle.getString("openFragment");
                switch (fragment) {
                    case "Map":
                        bundle.remove("openFragment");
                        fragmentManager.beginTransaction().replace(R.id.fragment_container, new MapFragment())
                                .commit();
                        break;
                    case "Settings":
                        bundle.remove("openFragment");
                        fragmentManager.beginTransaction().replace(R.id.fragment_container, new SettingsFragment())
                                .commit();
                        break;
                    case "About":
                        bundle.remove("openFragment");
                        fragmentManager.beginTransaction().replace(R.id.fragment_container, new AboutFragment())
                                .commit();
                        break;
                    case "Help":
                        bundle.remove("openFragment");
                        fragmentManager.beginTransaction().replace(R.id.fragment_container, new HelpFragment())
                                .commit();
                        break;
                    case "Timeline":
                        bundle.remove("openFragment");
                        fragmentManager.beginTransaction().replace(R.id.fragment_container, new TimelineFragment())
                                .commit();
                        break;
                }

            } else {
                /* If no place page or menu item needs to be opened, do the default
                - launch the map fragment */
                fragmentManager.beginTransaction().replace(R.id.fragment_container,
                        new MapFragment()).commit();
            }
        } else {
            /* Same as before, but this is for when the bundle is null, a.k.a no arguments were
             * provided when launching this activity */
            fragmentManager.beginTransaction().replace(R.id.fragment_container,
                    new MapFragment()).commit();
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // do nothing, just override
    }

    // Getter for navigationView
    public NavigationView getNavigationView() {
        return this.navigationView;
    }

    // Sets new title for action bar
    public void setActionBarTitle(String title){
        actionBarTitle.setText(title);
    }

    /* Requesting a users permission to use location services */
    private void getLocationPermission() {
        String[] permissions = {FINE_LOCATION, COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionsGranted = true;
                Settings.getInstance().setLocationPermissionsGranted(true);
            } else {
                ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSIONS_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSIONS_REQUEST_CODE);
        }
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


    /* Message bubble if you want to display something to the user */
    public void MessageBox(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    /* Listener for click events on the nav drawer menu items */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_timeline:
                fragmentManager.beginTransaction().replace(R.id.fragment_container,
                        new TimelineFragment()).addToBackStack(TimelineFragment.TAG).commit();
                drawer.closeDrawer(GravityCompat.START);
                break;
            case R.id.nav_map:
                fragmentManager.beginTransaction().replace(R.id.fragment_container,
                        new MapFragment()).addToBackStack(MapFragment.TAG).commit();
                drawer.closeDrawer(GravityCompat.START);
                break;

            case R.id.nav_settings:
                fragmentManager.beginTransaction().replace(R.id.fragment_container,
                        new SettingsFragment()).addToBackStack(SettingsFragment.TAG).commit();
                drawer.closeDrawer(GravityCompat.START);
                break;

            case R.id.nav_info:
                fragmentManager.beginTransaction().replace(R.id.fragment_container,
                        new AboutFragment()).addToBackStack(AboutFragment.TAG).commit();
                drawer.closeDrawer(GravityCompat.START);
                break;

            case R.id.nav_help:
                fragmentManager.beginTransaction().replace(R.id.fragment_container,
                        new HelpFragment()).addToBackStack(HelpFragment.TAG).commit();
                drawer.closeDrawer(GravityCompat.START);
                break;
        }
        return true;
    }

    /* On press of the magnifying glass open the search view */
    public void openSearch(View view) {
        Intent myIntent = new Intent(MapsActivity.this, SearchableActivity.class);
        /* Optional key value pairs if you need to provide info to the Search view*/
        myIntent.putExtra("key", "value");
        MapsActivity.this.startActivity(myIntent);
    }
}
