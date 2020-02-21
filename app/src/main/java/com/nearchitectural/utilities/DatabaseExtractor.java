package com.nearchitectural.utilities;

import android.util.Log;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.nearchitectural.utilities.models.Location;
import com.nearchitectural.utilities.models.Report;

import java.util.ArrayList;

/* Author:  Kristiyan Doykov, Joel Bell-Wilding
 * Since:   TODO: Fill in date
 * Version: 1.0
 * Purpose: Extracts information from the database using the provided document
 *          and produces and returns a model object containing the necessary information
 */
public class DatabaseExtractor {

    private static final String TAG = "DBExtractor";

    // Takes all fields from the db report item and converts them into a report object
    public static Report extractReport(DocumentSnapshot document) {

        String id = document.getId();
        ArrayList<String> paragraphs = new ArrayList<>();
        ArrayList<String> slideshowURLs = new ArrayList<>();

        if (document.getData() != null) {
             paragraphs = document.getData().get("paragraphs") == null ?
                    new ArrayList<String>() : (ArrayList<String>) document.getData().get("paragraphs");

            slideshowURLs = document.getData().get("slideshowURLs") == null ?
                    new ArrayList<String>() : (ArrayList<String>) document.getData().get("slideshowURLs");
        }

        Log.d(TAG, document.getId() + " => " + document.getData());

        // All the information about the current location
        return new Report(id, paragraphs, slideshowURLs);
    }

    // Takes all fields from the db object and converts them to a Location object
    public static Location extractLocation(QueryDocumentSnapshot document) {

        // For each location in database create a new Location instance and add it to the list
        String id = document.getId();

        String name = document.getData().get("name") == null ?
                "Unknown" : (String) document.getData().get("name");

        String placeType = document.getData().get("placeType") == null ?
                "Unknown" : (String) document.getData().get("placeType");

        Long yearOpened = document.getData().get("yearOpened") == null ?
                0 : (Long) document.getData().get("yearOpened");

        String summary = document.getData().get("summary") == null ?
                "Unknown" : (String) document.getData().get("summary");

        double latitude = document.getData().get("latitude") == null ?
                0 : (double) document.getData().get("latitude");

        double longitude = document.getData().get("longitude") == null ?
                0 : (double) document.getData().get("longitude");

        // Stores information about which tags are active for this location
        TagMapper locationTagMapper = new TagMapper(document);

        String thumbnailAddress = document.getData().get("thumbnail") == null ?
                "" : (String) document.getData().get("thumbnail");

        String reportID = document.getData().get("reportID") == null ?
                "Unknown" : (String) document.getData().get("reportID");

        Long likes = document.getData().get("likes") == null ? 0 : (Long) document.getData().get("likes");

        Log.d(TAG, document.getId() + " => " + document.getData());


        // All the information about the current location
        return new Location(
                id,
                name,
                yearOpened.intValue(),
                likes.intValue(),
                placeType,
                summary,
                latitude,
                longitude,
                locationTagMapper.getTagValuesMap(),
                thumbnailAddress,
                reportID);
    }
}
