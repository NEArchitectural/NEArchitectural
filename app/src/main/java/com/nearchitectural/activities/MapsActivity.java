package com.nearchitectural.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.nearchitectural.R;
import com.nearchitectural.fragments.AboutFragment;
import com.nearchitectural.fragments.HelpFragment;
import com.nearchitectural.fragments.MapFragment;
import com.nearchitectural.fragments.SearchFragment;
import com.nearchitectural.fragments.SettingsFragment;
import com.nearchitectural.fragments.TimelineFragment;


public class MapsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    private DrawerLayout drawer;
    private FloatingActionButton actionButton;
    private EditText mSearchText;
    private NavigationView navigationView;
    private FragmentManager fragmentManager;
    private Fragment currentFragment;
    public Boolean mLocationPermissionsGranted;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSIONS_REQUEST_CODE = 1234;


    /* Create class Place */
//    private ArrayList<Place> places;

    private static final String TAG = "MapActivity";
    /* Declare any global app settings here such as
     * text size etc. */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* Set these settings again here instead of saving them to the device like suggested */

        setContentView(R.layout.activity_maps);

        fragmentManager = getSupportFragmentManager();

        /* Query the db here and populate the places list */


        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);


//        mSearchText = findViewById(R.id.search_input);

        drawer = findViewById(R.id.drawer_layout);

        navigationView = findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(this);

        fragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                if (fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 1) instanceof MapFragment) {
                    navigationView.getMenu().findItem(R.id.nav_map).setChecked(true);
                } else if (fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 1) instanceof SettingsFragment) {
                    navigationView.getMenu().findItem(R.id.nav_settings).setChecked(true);
                } else if (fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 1) instanceof TimelineFragment) {
                    navigationView.getMenu().findItem(R.id.nav_timeline).setChecked(true);
                } else if (fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 1) instanceof AboutFragment) {
                    navigationView.getMenu().findItem(R.id.nav_info).setChecked(true);
                } else if (fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 1) instanceof HelpFragment) {
                    navigationView.getMenu().findItem(R.id.nav_help).setChecked(true);
                } else if (fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 1) instanceof SearchFragment) {
                    navigationView.setCheckedItem(-1);
                }
            }
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);

        toggle.syncState();

        getLocationPermission();

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new MapFragment()).addToBackStack(null).commit();

        navigationView.getMenu().getItem(0).setChecked(true);

        currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
    }


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


    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (!(currentFragment instanceof SearchFragment)) {
                if (currentFragment instanceof MapFragment) {
                    navigationView.getMenu().findItem(R.id.nav_map).setChecked(true);
                } else if (currentFragment instanceof SettingsFragment) {
                    navigationView.getMenu().findItem(R.id.nav_settings).setChecked(true);
                } else if (currentFragment instanceof HelpFragment) {
                    navigationView.getMenu().findItem(R.id.nav_help).setChecked(true);
                } else if (currentFragment instanceof AboutFragment) {
                    navigationView.getMenu().findItem(R.id.nav_info).setChecked(true);
                } else if (currentFragment instanceof TimelineFragment) {
                    navigationView.getMenu().findItem(R.id.nav_timeline).setChecked(true);
                }
            } else {
                navigationView.setCheckedItem(0);
            }
            super.onBackPressed();
        }
    }


    /* Message bubble */
    public void MessageBox(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_timeline:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new TimelineFragment()).addToBackStack(TimelineFragment.TAG).commit();
                setCurrent(getSupportFragmentManager().findFragmentById(R.id.fragment_container));
                drawer.closeDrawer(GravityCompat.START);
                break;
            case R.id.nav_map:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new MapFragment()).addToBackStack(MapFragment.TAG).commit();
                setCurrent(getSupportFragmentManager().findFragmentById(R.id.fragment_container));
                drawer.closeDrawer(GravityCompat.START);
                break;

            case R.id.nav_settings:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new SettingsFragment()).addToBackStack(SettingsFragment.TAG).commit();
                setCurrent(getSupportFragmentManager().findFragmentById(R.id.fragment_container));
                drawer.closeDrawer(GravityCompat.START);
                break;

            case R.id.nav_info:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new AboutFragment()).addToBackStack(AboutFragment.TAG).commit();
                setCurrent(getSupportFragmentManager().findFragmentById(R.id.fragment_container));
                drawer.closeDrawer(GravityCompat.START);
                break;

            case R.id.nav_help:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new HelpFragment()).addToBackStack(HelpFragment.TAG).commit();
                setCurrent(getSupportFragmentManager().findFragmentById(R.id.fragment_container));
                drawer.closeDrawer(GravityCompat.START);
                break;
        }
        return true;
    }

    public Fragment getCurrent() {
        return currentFragment;
    }

    public void setCurrent(Fragment current) {
        this.currentFragment = current;
    }

    public void openSearch(View view) {
        Intent myIntent = new Intent(MapsActivity.this, SearchableActivity.class);
        myIntent.putExtra("key", "yolo"); //Optional parameters
        MapsActivity.this.startActivity(myIntent);
    }
}
