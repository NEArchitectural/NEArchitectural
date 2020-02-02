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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.nearchitectural.R;

public class LocationFragment extends Fragment {
    public static final String TAG = "LocationFragment";
    private TextView title;
    private Bundle arguments;
    // Database reference field
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        arguments = getArguments();
        /* Set values like name of the place, details, images and such passed from the activity
         * by using the following syntax */
//        String placeName = getArguments().getString("name");

        return inflater.inflate(R.layout.fragment_location, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        title = (TextView) view.findViewById(R.id.title);

        // Name of the current place the user clicked on
        String placeName = arguments.getString("placeName");

        // This is just a test to verify that the right info is passed in
        title.setText(placeName + " details");

        /* Get an instance of the database in order to
         retrieve/update the data for the specific location */
        db = FirebaseFirestore.getInstance();

        // Retrieve all the information about the current location via its name
        db.collection("locations").whereEqualTo("name", placeName)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // If you get here it means the query has been successful
                                /* You can now work with the documents data
                                like name, type, summary and so on
                                 by using document.getData.get("key") */
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                        } else {
                            /* If this block executes, either no document was found
                             * matching the search name or some other error occurred*/
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
        ;
    }
}
