package com.nearchitectural.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.FirebaseFirestore;
import com.nearchitectural.R;
import com.nearchitectural.databinding.FragmentTimelineBinding;
import com.nearchitectural.ui.activities.MapsActivity;
import com.nearchitectural.utilities.models.TimeLine;



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

        arguments = getArguments();

        timeLineBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_timeline, container, false);
        timeLineBinding.setModel(timeLine); // Set selected location as data binding model

        title = timeLineBinding.timelineTitle;
        summaryTimeLine = timeLineBinding.timelineSummary;
        thumbnail = timeLineBinding.castleTest;
        yearOpened = timeLineBinding.yearOpened;
        MapsActivity parentActivity = (MapsActivity) this.getActivity();

        db = FirebaseFirestore.getInstance();

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



