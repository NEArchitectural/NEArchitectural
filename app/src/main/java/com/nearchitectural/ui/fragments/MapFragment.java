package com.nearchitectural.ui.fragments;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.nearchitectural.R;
import com.nearchitectural.ui.activities.MapsActivity;
import com.nearchitectural.ui.adapters.MapMarkerWindowAdapter;
import com.nearchitectural.utilities.CurrentCoordinates;
import com.nearchitectural.utilities.DatabaseExtractor;
import com.nearchitectural.utilities.Settings;
import com.nearchitectural.utilities.TagMapper;
import com.nearchitectural.utilities.models.Location;

/* Author:  Kristiyan Doykov, Joel Bell-Wilding
 * Since:   TODO: Fill in date
 * Version: 1.0
 * Purpose: Handles events and presentation related to the Google Maps section of the home screen
 */
public class MapFragment extends Fragment implements OnMapReadyCallback {

    public static final String TAG = "MapFragment"; // Tag used for logging status of application

    private MapView mapView; // View object displaying the map
    private GoogleMap googleMap; // Object representing the map itself
    private FirebaseFirestore db; // The Firebase database containing location information
    private boolean mLocationPermissionsGranted; // Boolean representing if location permissions were granted
    private FusedLocationProviderClient mFusedLocationProviderClient; // Used to get user's current location
    private static final int LOCATION_PERMISSIONS_REQUEST_CODE = 1234;
    private LatLng currentLocation; // User's current location
    // The default location used in cases where user's actual location cannot be determined
    private static final LatLng DEFAULT_LOCATION = new LatLng(54.9695, -1.6074);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    // Attempts to get device's location if permissions are granted, otherwise returns a default location
    private void getDeviceLocation() {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        // Sets current location to default position in case actual location cannot be determined
        if (currentLocation != null) {
            CurrentCoordinates.setCoords(DEFAULT_LOCATION);
        } else {
            currentLocation = DEFAULT_LOCATION;
            CurrentCoordinates.setCoords(currentLocation);
        }

        // Attempts to get device's location initially
        try {
            if (mLocationPermissionsGranted) {
                // This warning cannot be evaded as we're using the Task api
                Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: found location! ");
                            android.location.Location currentLocationFound = (android.location.Location) task.getResult();
                            if (currentLocationFound != null) {
                                currentLocation = new LatLng(currentLocationFound.getLatitude(), currentLocationFound.getLongitude());
                                CurrentCoordinates.setCoords(new LatLng(currentLocationFound.getLatitude(), currentLocationFound.getLongitude()));
                            } else {
                                // TODO: Remove log tags and else statements at end of development
                                Log.d(TAG, "onComplete: current location is null. Fallback to default location");
                            }
                        } else {
                            Log.d(TAG, "onComplete: current location is null. Fallback to default location");
                            Toast.makeText(getActivity(), "Unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException se) {
            Log.d(TAG, "onComplete: current location is null. Fallback to default location");
            Log.e(TAG, "getDeviceLocation: SecurityException: " + se.getMessage());
        }

        // Moves camera to user's location
        if (googleMap != null) {
            moveCamera(CurrentCoordinates.getCoords());
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set up map fragment using Map Activity information
        MapsActivity parentActivity = (MapsActivity) this.getActivity();
        parentActivity.getNavigationView().getMenu().findItem(R.id.nav_map).setChecked(true);
        parentActivity.setActionBarTitle("Map");

        db = FirebaseFirestore.getInstance(); // Instance of the db for requesting/updating data

        // Check if the user has allowed us to use their location from settings
        mLocationPermissionsGranted = Settings.getInstance().isLocationPermissionsGranted();

        // Whenever the map gets created - update the current location
        getDeviceLocation();

        // Set up the map
        mapView = view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();

        // The first parameter means that the callback has been implemented in this class
        mapView.getMapAsync(this);
    }

    // Move camera overlooking map to be positioned over provided location
    private void moveCamera(LatLng latLng) {
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, (float) 15.0));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissionsGranted = false;

        if (requestCode == LOCATION_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length > 0) {
                for (int i : grantResults) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        // If the result is not PERMISSION_GRANTED do nothing
                        return;
                    }
                }
                // Else set it to true
                mLocationPermissionsGranted = true;
            }
        }
    }

    /*
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onMapReady(GoogleMap map) {

        googleMap = map;
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

        addAllMarkers(); // Adds a marker for every location in the database

        googleMap.setMyLocationEnabled(true);

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

        /* Set the map to use our custom info window */
        googleMap.setInfoWindowAdapter(new MapMarkerWindowAdapter(getActivity()));

        /* Get the device's location again to update the current coordinates */
        getDeviceLocation();
    }

    private void addAllMarkers() {
        // Get all locations from database and set a marker for each
         /* TODO: Get the settings currently applied from the Settings singleton and
             only add the necessary locations to the map that answer the criteria
              (e.g. maximum distance from current location, child friendly,
               wheelchair accessible locations only and so on) by calling the calculateDistance()
                method on each location before adding it to the map*/
        db.collection("locations")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            // Cycles through all location documents in database and adds a map marker
                            for (QueryDocumentSnapshot document : task.getResult()) {

//                                if (DistanceCalculator.calculateDistance(currentLocation.latitude,
//                                        (double) document.getData().get("latitude"),
//                                        currentLocation.longitude,
//                                        (double) document.getData().get("longitude")) > Settings.getInstance().getMaxDistance()) {
//                                    // TODO: Move the googleMap.addMarker call here after the Settings Fragment has been finished
//                                }

                                // Creates a marker using database extractor
                                MarkerOptions marker = DatabaseExtractor.extractMapMarker(document);
                                // If retrieval of info from database is successful, adds new marker to map
                                if (marker != null)
                                    googleMap.addMarker(marker);
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    // Opens a new location fragment for the location corresponding to the provided marker
    private void openLocationFragment(Marker marker) {

        // Attempts to retrieve location from database
        db.collection("locations").whereEqualTo("name", marker.getTitle())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        // Instantiates selected location to a default location of empty/unknown fields
                        Location selectedLocation = new Location(
                                "Unknown", "Unknown", 0,
                                0, "Unknown", "Unknown",
                                0, 0, new TagMapper().getTagValuesMap(),
                                "", "");

                        // Retrieves location from database and sets it as location to pass to fragment
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                selectedLocation = DatabaseExtractor.extractLocation(document);
                            }
                        }

                        // Creates a new location fragment and opens it
                        LocationFragment lf = new LocationFragment(selectedLocation);
                        getActivity()
                                .getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.fragment_container, lf)
                                .addToBackStack(LocationFragment.TAG)
                                .commit();
                    }
                });
    }

    /* Message bubble */
    private void MessageBox(String message) {
        Toast.makeText(this.getContext(), message, Toast.LENGTH_SHORT).show();
    }
}
