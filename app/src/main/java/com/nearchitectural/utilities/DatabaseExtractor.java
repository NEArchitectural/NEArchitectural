package com.nearchitectural.utilities;

import android.util.Log;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
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

    // Placeholder string for failed database retrieval
    private static final String UNKNOWN = "Unknown";

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


    // Takes all fields from the database report document and converts them into a report object
    public static Report extractReport(String reportID, Map<String, Object> reportData) {

        ArrayList<String> paragraphs = new ArrayList<>();
        ArrayList<String> slideshowURLs = new ArrayList<>();
        ArrayList<String> references = new ArrayList<>();

        if (reportData != null) {
            try {
                paragraphs = (ArrayList<String>) reportData.get(PARAGRAPHS);
                slideshowURLs = (ArrayList<String>) reportData.get(SLIDESHOW_URLS);
                references = (ArrayList<String>) reportData.get((REFERENCES));
            } catch (ClassCastException ignored) {
                // Ignored since report data has already been instantiated as empty lists
            }
        }

        Log.d(TAG, reportID + " => " + reportData);

        // All the information about the current location
        return new Report(reportID, paragraphs, slideshowURLs, references);
    }

    // Takes necessary fields from the database location document and converts them to a Map Marker
    public static MarkerOptions extractMapMarker(String locationID, Map<String, Object> locationData) {

        // Gather only information needed for marker
        String name = locationData.get(NAME) == null ?
                UNKNOWN : (String) locationData.get(NAME);

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

        Log.d(TAG, locationID + " => " + locationData);

        // Only add a marker if name and coordinates are identified (since both are necessary)
        assert name != null;
        if (!(name.equals(UNKNOWN) || (latitude == 0 && longitude == 0))) {
            return new MarkerOptions().flat(false)
                    .position(new LatLng(latitude, longitude))
                    .title(name)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                    .snippet(summary);
        }
        return null;
    }

    // Takes all fields from the database location document and converts them to a Location object
    public static Location extractLocation(String documentID, Map<String, Object> locationData) {

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
