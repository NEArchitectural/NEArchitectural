package com.nearchitectural.utilities;

import android.util.Log;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.nearchitectural.utilities.models.Location;
import com.nearchitectural.utilities.models.Report;

import java.util.ArrayList;
import java.util.Map;

/* Author:  Kristiyan Doykov, Joel Bell-Wilding
 * Since:   10/02/20
 * Version: 1.2
 * Purpose: Extracts information from the database using the provided document
 *          and produces and returns a model object containing the necessary information
 */
public class DatabaseExtractor {

    private static final String TAG = "DBExtractor";

    // Takes all fields from the database report document and converts them into a report object
    public static Report extractReport(DocumentSnapshot document) {

        String id = document.getId();
        ArrayList<String> paragraphs = new ArrayList<>();
        ArrayList<String> slideshowURLs = new ArrayList<>();
        ArrayList<String> references = new ArrayList<>();

        if (document.getData() != null) {
            try {
                paragraphs = (ArrayList<String>) document.getData().get("paragraphs");
                slideshowURLs = (ArrayList<String>) document.getData().get("slideshowURLs");
                references = (ArrayList<String>) document.getData().get(("references"));
            } catch (ClassCastException ignored) {
                // Ignored since report data has already been instantiated as empty lists
            }
        }

        Log.d(TAG, document.getId() + " => " + document.getData());

        // All the information about the current location
        return new Report(id, paragraphs, slideshowURLs, references);
    }

    // Takes necessary fields from the database location document and converts them to a Map Marker
    public static MarkerOptions extractMapMarker(QueryDocumentSnapshot document) {

        // Gather only information needed for marker
        String name = document.getData().get("name") == null ?
                "Unknown" : (String) document.getData().get("name");

        String summary = document.getData().get("summary") == null ?
                "Unknown" : (String) document.getData().get("summary");

        // Evade accidental use of Strings in number fields in the database
        double latitude = 0;
        if (document.getData().get("latitude") != null) {
            try {
                latitude = (double) document.getData().get("latitude");
            } catch (Exception ignored) {
                latitude = 0;
            }
        }
        double longitude = 0;
        if (document.getData().get("longitude") != null) {
            try {
                longitude = (double) document.getData().get("longitude");
            } catch (Exception ignored) {
                longitude = 0;
            }
        }

        Log.d(TAG, document.getId() + " => " + document.getData());

        // Only add a marker if name and coordinates are identified (since both are necessary)
        Marker newMarker;
        assert name != null;
        if (!(name.equals("Unknown") || (latitude == 0 && longitude == 0))) {
            return new MarkerOptions().flat(false)
                    .position(new LatLng(latitude, longitude))
                    .title(name)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                    .snippet(summary);
        }
        return null;
    }

    // Takes all fields from the database location document and converts them to a Location object
    public static Location extractLocation(String documentID, Map<String, Object> document) {

        // For each location in database create a new Location instance and add it to the list
        String name = document.get("name") == null ?
                "Unknown" : (String) document.get("name");

        String placeType = document.get("placeType") == null ?
                "Unknown" : (String) document.get("placeType");

        long yearOpened = 0;
        if (document.get("yearOpened") != null) {
            try {
                yearOpened = (long) document.get("yearOpened");
            } catch (Exception ignored) {
            }
        }

        String summary = document.get("summary") == null ?
                "Unknown" : (String) document.get("summary");

        // Evade accidental use of Strings in number fields in the database
        double latitude = 0;
        if (document.get("latitude") != null) {
            try {
                latitude = (double) document.get("latitude");
            } catch (Exception ignored) {
                latitude = 0;
            }
        }
        double longitude = 0;
        if (document.get("longitude") != null) {
            try {
                longitude = (double) document.get("longitude");
            } catch (Exception ignored) {
                longitude = 0;
            }
        }

        // Stores information about which tags are active for this location
        TagMapper locationTagMapper = new TagMapper(documentID, document);

        String thumbnailAddress = document.get("thumbnail") == null ?
                "" : (String) document.get("thumbnail");

        String reportID = document.get("reportID") == null ?
                "Unknown" : (String) document.get("reportID");

        long likes = 0;
        if (document.get("likes") != null) {
            try {
                likes = (long) document.get("likes");
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
