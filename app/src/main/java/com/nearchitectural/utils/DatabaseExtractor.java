package com.nearchitectural.utils;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.nearchitectural.models.Location;

import java.util.ArrayList;

public class DatabaseExtractor {
    private static final String TAG = "DBExtractor";

    // Takes all fields from the db object and converts them to a Location object
    public static Location extractLocation(QueryDocumentSnapshot document) {
        String id = document.getId();

        String name = document.getData().get("name") == null ?
                "Unknown" : (String) document.getData().get("name");

        String placeType = document.getData().get("placeType") == null ?
                "Unknown" : (String) document.getData().get("placeType");

        String summary = document.getData().get("summary") == null ?
                "Unknown" : (String) document.getData().get("summary");

        String firstParagraph = document.getData().get("firstParagraph") == null ?
                "Unknown" : (String) document.getData().get("firstParagraph");

        String secondParagraph = document.getData().get("secondParagraph") == null ?
                "Unknown" : (String) document.getData().get("secondParagraph");

        String thirdParagraph = document.getData().get("thirdParagraph") == null ?
                "Unknown" : (String) document.getData().get("thirdParagraph");

        boolean wheelChairAccessible =
                document.getData().get("wheelChairAccessible") != null
                        && (boolean) document.getData().get("wheelChairAccessible");

        boolean childFriendly = document.getData().get("childFriendly") != null
                && (boolean) document.getData().get("childFriendly");

        boolean cheapEntry = document.getData().get("cheapEntry") != null
                && (boolean) document.getData().get("cheapEntry");

        boolean freeEntry = document.getData().get("freeEntry") != null
                && (boolean) document.getData().get("freeEntry");

        String thumbnailAddress = document.getData().get("thumbnail") == null ?
                "" : (String) document.getData().get("thumbnail");

        double latitude = document.getData().get("latitude") == null ?
                0 : (double) document.getData().get("latitude");

        double longitude = document.getData().get("longitude") == null ?
                0 : (double) document.getData().get("longitude");

        ArrayList<String> imageURLs = document.getData().get("imageURLs") == null ?
                new ArrayList<String>() : (ArrayList<String>) document.getData().get("imageURLs");

        long likes = document.getData().get("likes") == null ? 0 : (long) document.getData().get("likes");

        Log.d(TAG, document.getId() + " => " + document.getData());


        // All the information about the current location
        return new Location(
                id,
                name,
                placeType,
                summary,
                firstParagraph,
                secondParagraph,
                thirdParagraph,
                new LatLng(latitude, longitude),
                wheelChairAccessible,
                childFriendly,
                cheapEntry,
                freeEntry,
                thumbnailAddress,
                imageURLs,
                likes);
    }
}
