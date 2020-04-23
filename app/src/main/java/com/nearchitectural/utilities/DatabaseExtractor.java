package com.nearchitectural.utilities;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.nearchitectural.utilities.models.Location;
import com.nearchitectural.utilities.models.Report;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/* Authors: Kristiyan Doykov - Original author (for Firebase calling code)
 *          Joel Bell-Wilding - Refinements and re-purposed to use callbacks
 * Since:   10/02/20
 * Version: 1.3
 * Purpose: Extracts information from the Firebase Database and produces and returns
 *          the appropriate object or list of objects needed for the chosen purpose
 *          via callbacks
 */
public class DatabaseExtractor {

    private static final String TAG = "DBExtractor";

    // Placeholder string for failed database retrieval
    private static final String UNKNOWN = "Unknown";

    // Keys for the Firebase collections
    private static final String LOCATION_COLLECTION_KEY = "locations";
    private static final String REPORT_COLLECTION_KEY = "reports";

    // Database field strings for Location collection
    private static final String NAME = "name";
    private static final String PLACE_TYPE = "placeType";
    private static final String YEAR_OPENED = "yearOpened";
    private static final String SUMMARY = "summary";
    private static final String LONGITUDE = "longitude";
    private static final String LATITUDE = "latitude";
    private static final String THUMBNAIL = "thumbnail";
    private static final String REPORT_ID = "reportID";
    private static final String LIKES = "likes";

    // Database field strings for Report collection
    private static final String PARAGRAPHS = "paragraphs";
    private static final String SLIDESHOW_URLS = "slideshowURLs";
    private static final String REFERENCES = "references";
    private static final String TIMELINE_SNIPPET = "timeLineSnippet";

    // Instance of the Firebase database in which information is stored
    private FirebaseFirestore db;

    /* Callback interface for retrieving information from the Firestore database
       Idea taken and adapted from the following link:
       https://stackoverflow.com/questions/42128909/return-value-from-valueeventlistener-java
     */
    public interface DatabaseCallback<T> {
        void onDataRetrieved(T data);
    }

    // Retrieve new instance of database when instantiated
    public DatabaseExtractor() {
        db = FirebaseFirestore.getInstance();
    }

    // Retrieves a new instance of Firestore in case current instance is terminated
    public void restoreInstance() {
        db = FirebaseFirestore.getInstance();
    }

    // Terminates the instance of the Firestore and prevents callbacks from firing
    public void cancelCallbacksAndDestroy() {
        db.terminate();
    }

    // Returns a list of all locations in the database location collection via callback
    public void extractAllLocations(@NonNull final DatabaseCallback<List<Location>> finishedCallback) {

        db.collection(LOCATION_COLLECTION_KEY)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        // List to store all locations in
                        List<Location> locationList = new ArrayList<>();

                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, String.valueOf(document.getData().get(NAME)));
                                // For each location create a new Location instance and add it to the list
                                Location locationTemp = parseLocation(document.getId(), document.getData());
                                locationList.add(locationTemp);
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                            finishedCallback.onDataRetrieved(locationList);
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                            finishedCallback.onDataRetrieved(null);
                        }
                    }
                });
    }

    // Returns a specific location corresponding to a provided location ID in the database via callback
    public void extractLocationByID(String locationID, @NonNull final DatabaseCallback<Location> finishedCallback) {

        db.collection(LOCATION_COLLECTION_KEY)
                .document(locationID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            Location location = parseLocation(task.getResult().getId(), task.getResult().getData());
                            finishedCallback.onDataRetrieved(location);
                        } else {
                            Log.w(TAG, "Error getting report.", task.getException());
                            finishedCallback.onDataRetrieved(null);
                        }
                    }
                });
    }

    // Returns a specific report corresponding to a provided report ID in the database via callback
    public void extractReport(String reportID, @NonNull final DatabaseCallback<Report> finishedCallback) {

        db.collection(REPORT_COLLECTION_KEY)
                .document(reportID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            Report report = parseReport(task.getResult().getId(), task.getResult().getData());
                            finishedCallback.onDataRetrieved(report);
                        } else {
                            Log.w(TAG, "Error getting report.", task.getException());
                            finishedCallback.onDataRetrieved(null);
                        }
                    }
                });
    }

    // Takes all fields from the database report document and converts them into a report object
    private Report parseReport(String reportID, Map<String, Object> reportData) {

        ArrayList<String> paragraphs = new ArrayList<>();
        ArrayList<String> slideshowURLs = new ArrayList<>();
        ArrayList<String> references = new ArrayList<>();
        String timelineSnippet = "";

        if (reportData != null) {
            try {
                paragraphs = (ArrayList<String>) reportData.get(PARAGRAPHS);
                slideshowURLs = (ArrayList<String>) reportData.get(SLIDESHOW_URLS);
                references = (ArrayList<String>) reportData.get(REFERENCES);
                timelineSnippet = (String) reportData.get(TIMELINE_SNIPPET);
            } catch (ClassCastException ignored) {
                // Ignored since report data has already been instantiated as empty lists
            }
        }

        Log.d(TAG, reportID + " => " + reportData);

        // All the information about the current location
        return new Report(reportID, paragraphs, slideshowURLs, references, timelineSnippet);
    }

    // Takes necessary fields from a location object and converts them to a Map Marker
    public MarkerOptions parseMapMarker(Location location) {

        // Gather only information needed for marker
        String name = location.getName();
        String summary = location.getSummary();
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        return new MarkerOptions().flat(false)
                .position(new LatLng(latitude, longitude))
                .title(name)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                .snippet(summary);
    }

    // Takes all fields from the database location document and converts them to a Location object
    private Location parseLocation(String documentID, Map<String, Object> locationData) {

        // For each location in database create a new Location instance and add it to the list
        String name = locationData.get(NAME) == null ?
                UNKNOWN : (String) locationData.get(NAME);

        String placeType = locationData.get(PLACE_TYPE) == null ?
                UNKNOWN : (String) locationData.get(PLACE_TYPE);

        long yearOpened = 0;
        if (locationData.get(YEAR_OPENED) != null) {
            try {
                yearOpened = (long) locationData.get(YEAR_OPENED);
            } catch (Exception ignored) {
            }
        }

        String summary = locationData.get(SUMMARY) == null ?
                UNKNOWN : (String) locationData.get(SUMMARY);

        // Evade accidental use of Strings in number fields in the database
        double latitude = 0;
        if (locationData.get(LATITUDE) != null) {
            try {
                latitude = (double) locationData.get(LATITUDE);
            } catch (Exception ignored) {
                latitude = 0;
            }
        }
        double longitude = 0;
        if (locationData.get(LONGITUDE) != null) {
            try {
                longitude = (double) locationData.get(LONGITUDE);
            } catch (Exception ignored) {
                longitude = 0;
            }
        }

        // Stores information about which tags are active for this location
        TagMapper locationTagMapper = new TagMapper(documentID, locationData);

        String thumbnailAddress = locationData.get(THUMBNAIL) == null ?
                "" : (String) locationData.get(THUMBNAIL);

        String reportID = locationData.get(REPORT_ID) == null ?
                UNKNOWN : (String) locationData.get(REPORT_ID);

        long likes = 0;
        if (locationData.get(LIKES) != null) {
            try {
                likes = (long) locationData.get(LIKES);
            } catch (Exception ignored) {
            }
        }

        // All the information about the current location
        return new Location(
                documentID,
                name,
                (int) yearOpened,
                (int) likes,
                placeType,
                summary,
                latitude,
                longitude,
                locationTagMapper.getTagValuesMap(),
                thumbnailAddress,
                reportID);
    }
}
