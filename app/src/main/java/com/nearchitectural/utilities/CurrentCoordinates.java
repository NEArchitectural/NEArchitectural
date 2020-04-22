package com.nearchitectural.utilities;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.Serializable;

/* Author:  Kristiyan Doykov
 * Since:   20/12/19
 * Version: 1.1
 * Purpose: Singleton class which stores and manipulates the user's current coordinates
 */
public class CurrentCoordinates implements Serializable {

    private static final String TAG = "CurrentCoordinates"; // Tag used for logging status of application

    private static volatile CurrentCoordinates soleInstance;
    private static LatLng coords;

    // The default location used in cases where user's actual location cannot be determined
    private static final LatLng DEFAULT_LOCATION = new LatLng(54.9695, -1.6074);

    /* Callback interface used to pass coordinates back upon retrieval.
     * Null can be used as an indicator that callback is not necessary */
    public interface LocationCallback<LatLng> {
        void onLocationRetrieved(LatLng coordinates);
    }

    //private constructor
    private CurrentCoordinates(){

        //Prevent form the reflection api.
        if (soleInstance != null){
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        }
    }

    public static CurrentCoordinates getInstance() {
        if (soleInstance == null) { //if there is no instance available... create new one
            synchronized (CurrentCoordinates.class) {
                if (soleInstance == null) soleInstance = new CurrentCoordinates();
            }
        }
        // Sets current location to default position in case actual location cannot be determined
        if (coords != null) {
            setCoords(DEFAULT_LOCATION);
        } else {
            coords = DEFAULT_LOCATION;
        }
        return soleInstance;
    }

    public static LatLng getCoords() {
        return new LatLng(coords.latitude,coords.longitude);
    }

    private static void setCoords(LatLng coords) {
        CurrentCoordinates.coords = coords;
    }

    //Make singleton from serialize and deserialize operation.
    protected CurrentCoordinates readResolve() {
        return getInstance();
    }

    // Attempts to get device's location if permissions are granted, otherwise returns a default location via callback
    public void getDeviceLocation(Context context, final LocationCallback<LatLng> locationRetrievedCallback) {
        FusedLocationProviderClient mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);

        // Set coords to default position as fallback for location retrieval fail
        coords = DEFAULT_LOCATION;

        // Attempts to get device's location initially
        try {
            if (Settings.getInstance().locationPermissionsAreGranted()) {
                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            android.location.Location currentLocationFound = (android.location.Location) task.getResult();
                            if (currentLocationFound != null) {
                                // If device location successfully retrieved, update coordinates
                                coords = new LatLng(currentLocationFound.getLatitude(), currentLocationFound.getLongitude());
                            }
                        }
                        // If coords are needed, pass back through callback
                        if (locationRetrievedCallback != null)
                            locationRetrievedCallback.onLocationRetrieved(coords);
                    }
                });
            }
        } catch (SecurityException se) {
            Log.d(TAG, "onComplete: current location is null. Fallback to default location");
            Log.e(TAG, "getDeviceLocation: SecurityException: " + se.getMessage());
        }
    }
}
