package com.nearchitectural.ui.fragments;

import android.content.pm.PackageManager;
import android.location.Location;
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
import com.nearchitectural.ui.adapters.CustomInfoWindowAdapter;
import com.nearchitectural.utilities.CurrentCoordinates;
import com.nearchitectural.utilities.Settings;

/**author: Kristiyan Doykov
 * since: TODO: Fill in date
 * version: 1.0
 * purpose: Handles events and presentation related to the Google Maps section of the home screen
 */
public class MapFragment extends Fragment implements OnMapReadyCallback {

    public static final String TAG = "MapFragment"; // Tag used for logging status of application

    private MapView mapView; // View object displaying the map
    private GoogleMap googleMap; // Object representing the map itself
    private FirebaseFirestore db; // The firebase database containing location information
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
                            Location currentLocationFound = (Location) task.getResult();
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
        MapsActivity parentActivity = (MapsActivity) this.getActivity();
        parentActivity.getNavigationView().getMenu().findItem(R.id.nav_map).setChecked(true);
        parentActivity.setActionBarTitle("Map");
        // Instance of the db for requesting/updating data
        db = FirebaseFirestore.getInstance();
        // Check if the user has allowed us to use their location
        if (parentActivity != null) {
            mLocationPermissionsGranted = parentActivity.mLocationPermissionsGranted;
        }
        mLocationPermissionsGranted = Settings.getInstance().ismLocationPermissionsGranted();
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

        googleMap.setOnInfoWindowClickListener(
                new GoogleMap.OnInfoWindowClickListener() {
                    public void onInfoWindowClick(Marker marker) {

                        LocationFragment lf = new LocationFragment();
                        Bundle arguments = new Bundle();
                        arguments.putString("placeName", marker.getTitle());
                        lf.setArguments(arguments);
                        getActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, lf)
                                .addToBackStack(LocationFragment.TAG)
                                .commit();

                        /* Code to load DetailsPage for the place will sit here */
                        MessageBox(marker.getTitle() + " was pressed!");
                    }
                }
        );

        // Enable all gestures
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setRotateGesturesEnabled(true);
        googleMap.getUiSettings().setScrollGesturesEnabled(true);
        googleMap.getUiSettings().setTiltGesturesEnabled(true);
        googleMap.getUiSettings().setZoomGesturesEnabled(true);

        // Get all places from db and set a marker foreach here
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
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                Log.d(TAG, document.getId() + " => " + document.getData());

//                                if (DistanceCalculator.calculateDistance(currentLocation.latitude,
//                                        (double) document.getData().get("latitude"),
//                                        currentLocation.longitude,
//                                        (double) document.getData().get("longitude")) > Settings.getInstance().getMaxDistance()) {
//                                    // TODO: Move the googleMap.addMarker call here after the Settings Fragment has been finished
//                                }

                                // Gather only information needed for marker
                                String name = document.getData().get("name") == null ?
                                        "Unknown" : (String) document.getData().get("name");

                                String summary = document.getData().get("summary") == null ?
                                        "Unknown" : (String) document.getData().get("summary");

                                double latitude = document.getData().get("latitude") == null ?
                                        0 : (double) document.getData().get("latitude");

                                double longitude = document.getData().get("longitude") == null ?
                                        0 : (double) document.getData().get("longitude");

                                // Only add a marker if name and coordinates are identified (since both are necessary)
                                if (!(name.equals("Unknown") || (latitude == 0 && longitude == 0))) {
                                    googleMap.addMarker(new MarkerOptions().flat(false)
                                            .position(new LatLng(latitude, longitude))
                                            .title(name)
                                            .snippet(summary));
                                }
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });

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
        googleMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(getActivity()));

        /* Get the device's location again to update the current coordinates */
        getDeviceLocation();
    }


    /* Message bubble */
    private void MessageBox(String message) {
        Toast.makeText(this.getContext(), message, Toast.LENGTH_SHORT).show();
    }
}
