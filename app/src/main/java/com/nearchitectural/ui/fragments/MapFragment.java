package com.nearchitectural.ui.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.nearchitectural.R;
import com.nearchitectural.ui.activities.MapsActivity;
import com.nearchitectural.ui.adapters.MapMarkerWindowAdapter;
import com.nearchitectural.ui.interfaces.BackHandleFragment;
import com.nearchitectural.ui.interfaces.LocationHandleFragment;
import com.nearchitectural.utilities.DatabaseExtractor;
import com.nearchitectural.utilities.Filter;
import com.nearchitectural.utilities.Settings;
import com.nearchitectural.utilities.models.Location;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* Author:  Kristiyan Doykov, Joel Bell-Wilding
 * Since:   10/12/19
 * Version: 1.1
 * Purpose: Handles events and presentation related to the Google Maps section of the home screen
 */
public class MapFragment extends Fragment implements OnMapReadyCallback, BackHandleFragment, LocationHandleFragment {

    public static final String TAG = "MapFragment"; // Tag used for logging status of application

    private MapView mapView; // View object displaying the map
    private GoogleMap googleMap; // Object representing the map itself
    private DatabaseExtractor extractor; // Used to retrieve location information and create map markers
    private boolean mLocationPermissionsGranted; // Boolean representing if location permissions were granted
    private boolean introDialogNeeded; // Flag boolean to signal if the introductory dialog should show
    private Marker activeMarker; // The marker whose info window is currently showing (if any)
    private CameraUpdate defaultCameraPosition; // The default position the map camera will hover over
    private Map<Marker, String> markerIDMap; // Map of markers to corresponding location IDs
    // Map of markers to the thumbnail URLs for their locations
    private Map<Marker, String> markerThumbnailURLMap = new HashMap<>();
    // Bundle key for storing/retrieving the initial startup dialog boolean
    private static final String INTRO_DIALOG_KEY = "introDialogKey";

    // Stores a boolean indicating if the introductory dialog must be shown every new instance
    public static MapFragment newInstance(boolean introDialogNeeded) {
        Bundle introDialogBundle = new Bundle();
        introDialogBundle.putBoolean(INTRO_DIALOG_KEY, introDialogNeeded);
        MapFragment mapFragment = new MapFragment();
        mapFragment.setArguments(introDialogBundle);
        return mapFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Gets the boolean for the introductory dialog from the bundle
        assert getArguments() != null;
        introDialogNeeded = getArguments().getBoolean(INTRO_DIALOG_KEY);
        return inflater.inflate(R.layout.fragment_map, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set up map fragment using Map Activity information
        MapsActivity parentActivity = (MapsActivity) this.getActivity();
        assert parentActivity != null;
        parentActivity.getNavigationView().getMenu().findItem(R.id.nav_map).setChecked(true);
        parentActivity.setActionBarTitle(getString(R.string.navigation_map));

        extractor = new DatabaseExtractor();

        parentActivity.requestLocationPermissions(); // Request location permissions if needed
        mLocationPermissionsGranted = Settings.getInstance().locationPermissionsAreGranted();

        // Set up the map
        mapView = view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();

        // The first parameter means that the callback has been implemented in this class
        mapView.getMapAsync(this);
    }

    /*
     * Manipulates the map once available. This callback is triggered when the map is ready to be used.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onMapReady(GoogleMap map) {

        // Default camera position overlooking Newcastle and Northumberland before all map markers are created
        defaultCameraPosition = CameraUpdateFactory.newLatLngZoom(new LatLng(55.25, -1.9979), 9f);

        // Initialise map and move to default camera position
        googleMap = map;
        googleMap.moveCamera(defaultCameraPosition);

        addAllMarkers(); // Adds a marker for every location in the database

        // Opens a location page when a location info-window is tapped
        googleMap.setOnInfoWindowClickListener(
                new GoogleMap.OnInfoWindowClickListener() {
                    public void onInfoWindowClick(Marker marker) {
                        openLocationFragment(marker);
                    }
                }
        );

        // Enable all gestures
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setRotateGesturesEnabled(true);
        googleMap.getUiSettings().setScrollGesturesEnabled(true);
        googleMap.getUiSettings().setTiltGesturesEnabled(true);
        googleMap.getUiSettings().setZoomGesturesEnabled(true);
        googleMap.setMyLocationEnabled(mLocationPermissionsGranted);

        // Adjusts the padding of the map to account for the activity action bar (i.e. the top bar)
        int actionBarPadding  = ((MapsActivity) getActivity()).getSupportActionBar().getHeight()
                + (int) (40 * Resources.getSystem().getDisplayMetrics().density);
        googleMap.setPadding(0, actionBarPadding , 0, 0);

        /* Move the "Center on my location" button to the bottom left */
        View locationButton =
                ((View) mapView.findViewById(Integer.parseInt("1"))
                        .getParent())
                        .findViewById(Integer.parseInt("2"));
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();

        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);

        rlp.addRule(RelativeLayout.ALIGN_PARENT_END, 0);
        rlp.addRule(RelativeLayout.ALIGN_END, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        rlp.setMargins(0, 0, 0, 80);

        /* Navigation feature - when the user holds finger on the screen, hide the
         * current info window and return to the default position */
        googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                googleMap.animateCamera(defaultCameraPosition);
                if (activeMarker != null)
                    activeMarker.hideInfoWindow();
            }
        });

        /* Navigation feature - when the user taps a marker, display info window and
         * zoom into the marker's location */
        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 14.5f));
                marker.showInfoWindow();
                activeMarker = marker;
                return true;
            }
        });

        // If application is in start up for the first time, display intro dialog
        if (introDialogNeeded) {
            displayIntroDialog();
        }
    }

    // Adds all location markers to Google Map (if settings criteria is met)
    private void addAllMarkers() {

        // Allows the camera boundary for all map markers to be built
        final LatLngBounds.Builder cameraBoundBuilder = new LatLngBounds.Builder();
        // Map of markers to location IDs for opening a location page
        markerIDMap = new HashMap<>();
        markerThumbnailURLMap = new HashMap<>();

        // Cycle through all locations in database and set a marker if settings criteria is met
        extractor.extractAllLocations(new DatabaseExtractor.DatabaseCallback<List<Location>>() {
            @Override
            public void onDataRetrieved(List<Location> data) {
                if (data != null) {
                    for (Location location : data) {
                        if (Filter.locationMeetsSettingsCriteria(location)) {
                            // Add marker to map and ID and thumbnail maps
                            Marker marker = googleMap.addMarker(extractor.parseMapMarker(location));
                            markerIDMap.put(marker, location.getId());
                            markerThumbnailURLMap.put(marker, location.getThumbnailURL());
                            // Include marker in camera bounds for showing all locations on map
                            cameraBoundBuilder.include(marker.getPosition());
                        }
                    }
                    // Set the map to use the application custom info window
                    googleMap.setInfoWindowAdapter(new MapMarkerWindowAdapter(getActivity(), markerThumbnailURLMap));
                    // Show an informative dialog if no markers have been added to map
                    if (markerIDMap.isEmpty()) {
                        ((MapsActivity) getActivity()).displayNoLocationsDialog();
                    }
                    // Once all markers are added to map, create bound and move camera with bound
                    createDefaultCameraPosition(cameraBoundBuilder);
                    googleMap.moveCamera(defaultCameraPosition);
                }
            }
        });
    }

    /* Uses the positions of map markers to create a bound for the map camera such
     * that all markers will fit within the camera bounds. Taken and adapted from:
     * https://stackoverflow.com/questions/14828217/android-map-v2-zoom-to-show-all-the-markers */
    private void createDefaultCameraPosition(LatLngBounds.Builder boundBuilder) {
        try {
            LatLngBounds cameraBounds = boundBuilder.build();
            int padding = (int) (30 * Resources.getSystem().getDisplayMetrics().density);
            defaultCameraPosition = CameraUpdateFactory.newLatLngBounds(cameraBounds, padding);
        } catch (IllegalStateException ignored) {
            /* Exception ignored since a default position has already been defined
             * in cases where there are not enough markers to create a bound*/
        }
    }

    // Opens a new location fragment for the location corresponding to the provided marker
    private void openLocationFragment(Marker marker) {

        // Creates a new location fragment and opens it using the location ID
        LocationFragment lf = LocationFragment.newInstance(markerIDMap.get(marker));
        getActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, lf)
                .addToBackStack(LocationFragment.TAG)
                .commit();
    }

    /* Displays the introductory dialog on the first use of the map fragment during each
     * instance of the application */
    private void displayIntroDialog() {

        // Initially hide the Google Map UI elements
        googleMap.getUiSettings().setZoomControlsEnabled(false);
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);

        // Display the dialog
        Dialog introDialog = new MaterialAlertDialogBuilder(getActivity(), R.style.ThemeOverlay_MaterialComponents)
                .setIcon(R.mipmap.ic_launcher_round)
                .setTitle(R.string.intro_message_title)
                .setMessage(R.string.intro_message_body)
                .setPositiveButton(R.string.ok, null)
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        // Once the dialog is dismissed, show the Google Map UI elements
                        googleMap.getUiSettings().setZoomControlsEnabled(true);
                        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
                    }
                })
                .setBackgroundInsetBottom((int) (40 * Resources.getSystem().getDisplayMetrics().density))
                .show();

        /* Ensure that window is displayed at the bottom of the screen and map is not dimmed
         * Code taken and adapted from:
         * https://stackoverflow.com/questions/9467026/changing-position-of-the-dialog-on-screen-android
         */
        Window window = introDialog.getWindow();
        assert window != null;
        WindowManager.LayoutParams windowLayoutParams = window.getAttributes();
        windowLayoutParams.gravity = Gravity.BOTTOM;
        windowLayoutParams.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(windowLayoutParams);
    }

    @Override
    public boolean onBackPressed() {
        /* Checks if a marker is active, and if so closes info window and returns
         * to default camera position */
        if (activeMarker != null && activeMarker.isInfoWindowShown()) {
            activeMarker.hideInfoWindow();
            googleMap.animateCamera(defaultCameraPosition);
            return true;
        }
        return false;
    }

    @Override
    public void handleLocation(boolean permissionsGranted) {
        // Enables the map to use locations if permissions are granted
        mLocationPermissionsGranted = permissionsGranted;
        if (googleMap != null)
            googleMap.setMyLocationEnabled(mLocationPermissionsGranted);
    }
}