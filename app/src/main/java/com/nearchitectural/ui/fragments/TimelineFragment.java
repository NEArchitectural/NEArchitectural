package com.nearchitectural.ui.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.nearchitectural.R;
import com.nearchitectural.databinding.FragmentTimelineBinding;
import com.nearchitectural.ui.activities.MapsActivity;
import com.nearchitectural.utilities.DatabaseExtractor;
import com.nearchitectural.utilities.models.TimeLine;

import java.util.ArrayList;
import java.util.List;


/* Author:  James Allwood-Panter,Kristiyan Doykov
 * Since:   25/03/20
 * Version: 1.2
 * Purpose: To visually represent the established timeline
 *          of the creation of each of the locations in the database.
 */
public class TimelineFragment extends Fragment {

    public static final String TAG = "TimelineFragment";

    private ImageView thumbnail;
    private TextView title;
    private FirebaseFirestore db; // Represents the database storing location information
    private Bundle arguments;
    private TextView summaryTimeLine;
    private TimeLine timeLine;
    private TextView yearOpened;
    private FragmentTimelineBinding timeLineBinding;


    public TimeLine getTimeLine() {
        return timeLine;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        timeLineBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_timeline, container, false);
        timeLineBinding.setModel(timeLine); // Set selected location as data binding model

        title = timeLineBinding.timelineTitle;
        summaryTimeLine = timeLineBinding.timelineSummary;
        thumbnail = timeLineBinding.castleTest;
        yearOpened = timeLineBinding.yearOpened;


        title.findViewById(R.id.timelineTitle);
        summaryTimeLine.findViewById(R.id.timelineSummary);
        thumbnail.findViewById(R.id.castleTest);
        db = FirebaseFirestore.getInstance();

        db.collection("locations")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        // List to store all locations in
                        List<TimeLine> timelineList = new ArrayList<>();

                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // For each location create a new Location instance and add it to the list
                                TimeLine timelineTemp = DatabaseExtractor.extractTimeline(document);
                                timelineList.add(timelineTemp);
                            }

                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });

        return inflater.inflate(R.layout.fragment_timeline, container, false);
    }

    @Override
    public void onViewCreated( View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MapsActivity parentActivity = (MapsActivity) this.getActivity();
        parentActivity.getNavigationView().getMenu().findItem(R.id.nav_timeline).setChecked(true);
        parentActivity.setActionBarTitle("Timeline");

    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);







        }

}











