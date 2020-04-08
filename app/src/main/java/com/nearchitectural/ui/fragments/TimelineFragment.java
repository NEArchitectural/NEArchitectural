package com.nearchitectural.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

<<<<<<< Updated upstream
=======
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
>>>>>>> Stashed changes
import com.nearchitectural.R;
import com.nearchitectural.ui.activities.MapsActivity;

/* Author:  Kristiyan Doykov
 * Since:   10/12/19
 * Version: 1.0
 * Purpose: To visually represent the established timeline
 *          of the creation of each of the locations in the database.
 */
public class TimelineFragment extends Fragment {

    public static final String TAG = "TimelineFragment";

<<<<<<< Updated upstream
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_timeline,container,false);
    }
=======
    private ImageView thumbnail;
    private TextView title;
    private FirebaseFirestore db; // Represents the database storing location information
    private Query dbq;
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

        arguments = getArguments();

        timeLineBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_timeline, container, false);
        timeLineBinding.setModel(timeLine); // Set selected location as data binding model

        title = timeLineBinding.timelineTitle;
        summaryTimeLine = timeLineBinding.timelineSummary;
        thumbnail = timeLineBinding.thumbnail;
        yearOpened = timeLineBinding.yearOpened;

        return inflater.inflate(R.layout.fragment_timeline,container,false);
}

>>>>>>> Stashed changes

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MapsActivity parentActivity = (MapsActivity) this.getActivity();
        parentActivity.getNavigationView().getMenu().findItem(R.id.nav_timeline).setChecked(true);
        parentActivity.setActionBarTitle("Timeline");
<<<<<<< Updated upstream
=======

    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);




>>>>>>> Stashed changes
    }
}
<<<<<<< Updated upstream
=======




>>>>>>> Stashed changes
