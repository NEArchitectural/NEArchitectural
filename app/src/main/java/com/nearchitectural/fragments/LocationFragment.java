package com.nearchitectural.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.nearchitectural.R;
import com.nearchitectural.models.Location;
import com.nearchitectural.utilities.TagMapper;

/**author: Kristiyan Doykov
 * since: TODO: Fill in date
 * version: 1.0
 * purpose: Presents information and images regarding a given location
 */
public class LocationFragment extends Fragment {
    public static final String TAG = "LocationFragment";

    private TextView title;
    // Arguments that came in with the intent
    private Bundle arguments;
    // Database reference field
    private FirebaseFirestore db;
    // Location object to contain all the info
    private Location location;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Initialize the arguments field with what came in from the previous activity
        arguments = getArguments();

        return inflater.inflate(R.layout.fragment_location, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        title = view.findViewById(R.id.title);

        // Name of the current place the user clicked on
        String placeName = arguments.getString("placeName");

        // This is just a test to verify that the right info is passed in
        title.setText(placeName + " details");

        /* Get an instance of the database in order to
         retrieve/update the data for the specific location */
        db = FirebaseFirestore.getInstance();


        /* You can increment/decrement the likes count by finding the location in the db and
         * setting the likes field like shown below - use this code in the OnClick listener for the
         * like button */
//        db.collection("locations").whereEqualTo("name",placeName)
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                // If you get here it means the query has been successful
//
//                                Map<String, Object> newData = new HashMap<>();
//                                newData.put("likes", ((int) document.getData().get("likes") + 1));
//
//                                db.collection("locations")
//                                        .document(document.getId())
//                                        .set(newData)
//                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                            @Override
//                                            public void onSuccess(Void aVoid) {
//                                                Log.d(TAG, "DocumentSnapshot successfully written!");
//                                            }
//                                        })
//                                        .addOnFailureListener(new OnFailureListener() {
//                                            @Override
//                                            public void onFailure(@NonNull Exception e) {
//                                                Log.w(TAG, "Error writing document", e);
//                                            }
//                                        });
//
//                                Log.d(TAG, document.getId() + " => " + document.getData());
//                            }
//                        } else {
//                            /* If this block executes, either no document was found
//                             * matching the search name or some other error occurred*/
//                            Log.d(TAG, "Error getting documents: ", task.getException());
//                        }
//                    }
//                });


        // Retrieve all the information about the current location via its name
        db.collection("locations").whereEqualTo("name", placeName)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String name = document.getData().get("name") == null ? "Unknown" : (String) document.getData().get("name");
                                String placeType = document.getData().get("placeType") == null ? "Unknown" : (String) document.getData().get("placeType");
                                String id = document.getId();

                                TagMapper locationTagMapper = new TagMapper(document); // Get Tag information from document

                                String thumbnailAddress = document.getData().get("thumbnail") == null ?
                                        "" : (String) document.getData().get("thumbnail");

                                double latitude = document.getData().get("latitude") == null ?
                                        0 : (double) document.getData().get("latitude");

                                double longitude = document.getData().get("longitude") == null ?
                                        0 : (double) document.getData().get("longitude");

                                // All the information about the current location
                                location = new Location(id,
                                        name,
                                        placeType,
                                        new LatLng(latitude, longitude),
                                        locationTagMapper.getTagValuesMap(),
                                        thumbnailAddress);

                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                        } else {
                            /* If this block executes, either no document was found
                             * matching the search name or some other error occurred*/
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }


    /* These lines are for navigating through Google maps forcefully
     * will be used when someone presses "Take me here" button or whatever we call it */

//                        Uri uri = Uri.parse(String.format(Locale.ENGLISH,
//                        "google.navigation:q=%f,%f", location.getLatitude(),location.getLongitude()));
//                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, uri);
//                        mapIntent.setPackage("com.google.android.apps.maps");
//                        startActivity(mapIntent);
}
