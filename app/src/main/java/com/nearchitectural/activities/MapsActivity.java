package com.nearchitectural.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
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
import com.nearchitectural.R;
import com.nearchitectural.databinding.ActivityMapsBinding;
import com.nearchitectural.fragments.AboutFragment;
import com.nearchitectural.fragments.HelpFragment;
import com.nearchitectural.fragments.LocationFragment;
import com.nearchitectural.fragments.MapFragment;
import com.nearchitectural.fragments.SettingsFragment;
import com.nearchitectural.fragments.TimelineFragment;


public class MapsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    private DrawerLayout drawer;
    private ActivityMapsBinding mapsBinding;
    private NavigationView navigationView;
    private FragmentManager fragmentManager;
    private LatLng currentLocation;
    public Boolean mLocationPermissionsGranted;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSIONS_REQUEST_CODE = 1234;

    private static final String TAG = "MapActivity";


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
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);

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
            if (bundle.get("openPlacePage") != null) {
                LocationFragment lf = new LocationFragment();
                Bundle arguments = new Bundle();
                String placeName = bundle.getString("openPlacePage");
                arguments.putString("placeName", placeName);
                lf.setArguments(arguments);
                bundle.remove("openPlacePage");
                fragmentManager.beginTransaction().replace(R.id.fragment_container,
                        lf).commit();
            } else {
                /* If no place page needs to be opened, do the default - launch the map fragment */
                fragmentManager.beginTransaction().replace(R.id.fragment_container,
                        new MapFragment()).commit();

                navigationView.getMenu().getItem(0).setChecked(true);
            }
        } else {
            /* Same as before, but this is for when the bundle is null, a.k.a no arguments were
             * provided when launching this activity */
            fragmentManager.beginTransaction().replace(R.id.fragment_container,
                    new MapFragment()).commit();

            navigationView.getMenu().getItem(0).setChecked(true);
        }
    }

    public NavigationView getNavigationView() {
        return this.navigationView;
    }

    /* Requesting a users permission to use location services */
    private void getLocationPermission() {
        String[] permissions = {FINE_LOCATION, COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionsGranted = true;
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
        myIntent.putExtra("key", "yolo");
        MapsActivity.this.startActivity(myIntent);
    }
}
