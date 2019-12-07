package com.nearchitectural;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.core.Tag;

import java.util.Locale;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private DrawerLayout drawer;
    private FloatingActionButton actionButton;
    private EditText mSearchText;
    private static final String TAG = "MapActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);


        mSearchText = findViewById(R.id.search_input);

        drawer = findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

//        actionButton = new FloatingActionButton(this);
//        actionButton = findViewById(R.id.fab);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    private void init(){
        Log.d(TAG,"init: initializing");

        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE || event.getAction() == KeyEvent.ACTION_DOWN
            || event.getAction() == KeyEvent.KEYCODE_ENTER){
                    /* Execute searching for a place in the db */
                    MessageBox("Search Attempted");
                }
                return false;
            }
        });

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnInfoWindowClickListener(
                new GoogleMap.OnInfoWindowClickListener(){
                    public void onInfoWindowClick(Marker marker){

                        /* These lines are for navigating through Google maps forcefully */
//                        Uri uri = Uri.parse(String.format(Locale.ENGLISH, "google.navigation:q=%f,%f", marker.getPosition().latitude,marker.getPosition().longitude));
//                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, uri);
//                        mapIntent.setPackage("com.google.android.apps.maps");
//                        startActivity(mapIntent);

                        /* Code to load DetailsPage for the place will sit here */
                        MessageBox(marker.getTitle() + " was pressed!");
                    }
                }
        );


        // Add a marker in Newcastle and move the camera
        LatLng newcastle = new LatLng(54.966667, -1.600000);
        mMap.addMarker(new MarkerOptions().position(newcastle).title("Marker in Newcastle").snippet("Population: 500,400 Also an incredibly long fucking snippet you know?"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(newcastle));

        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(MapsActivity.this));

        init();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void openTimeline(MenuItem item) {
        /* We have to fill this one in later */
        MessageBox("Timeline pressed");
        /* Open timeline view */
    }

    public void openSettings(MenuItem item){
        /* We have to fill this one in later */
        MessageBox("Settings pressed");
        /* Open Settings view */
    }

    public void openSearch(View view) {
        /* We have to fill this one in later */
        MessageBox("Search icon clicked");
        /* Transform toolbar into a search bar */
    }


    /* Message bubble */
    public void MessageBox(String message)
    {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
