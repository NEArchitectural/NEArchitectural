package com.nearchitectural.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nearchitectural.R;
import com.nearchitectural.databinding.FragmentTimelineBinding;
import com.nearchitectural.ui.activities.MapsActivity;
import com.nearchitectural.ui.adapters.TimelineItemAdapter;
import com.nearchitectural.ui.models.LocationModel;
import com.nearchitectural.ui.models.ModelProducer;
import com.nearchitectural.utilities.comparators.OldestToNewestComparator;
import com.nearchitectural.utilities.models.Location;

import java.util.ArrayList;
import java.util.List;

/* Author:  Kristiyan Doykov, Joel Bell-Wilding
 * Since:   10/12/19
 * Version: 1.1
 * Purpose: Visually display a timeline showing locations in the order in which they were created.
 */
public class TimelineFragment extends Fragment {

    public static final String TAG = "TimelineFragment";

    // LAYOUT ELEMENTS
    private RecyclerView timelineRecyclerView; // The recycler representing the timeline
    private ModelProducer locationModels; // UI model for list of location search results
    private TimelineItemAdapter timelineItemAdapter; // Recycler adapter for location models

    private List<LocationModel> mModels; // Location cards
    private List<Location> locationsToShow; // List of all locations to show

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // Get data binding layout elements
        FragmentTimelineBinding timelineBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_timeline, container, false);

        // Retrieve timeline recycler from binding
        timelineRecyclerView = timelineBinding.timelineRecycler;
        timelineRecyclerView.setHasFixedSize(true); // Set true since all item layouts are same dimensions

        // Set the view model for displaying locations in the timeline
        locationModels = ViewModelProviders.of(this).get(ModelProducer.class);

        // Defines a layout manager which allows the timeline recycler to be scrolled horizontally
        LinearLayoutManager recyclerManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        timelineRecyclerView.setLayoutManager(recyclerManager); // Set manager

        // Creates the timeline adapter, initially ordering locations in chronological order of creation
        timelineItemAdapter = new TimelineItemAdapter(getActivity(), new OldestToNewestComparator());
        timelineRecyclerView.setAdapter(timelineItemAdapter); // Set adapter for recycler view

        // Retrieve locations from database using live data (i.e. results will appear when retrieved from database)
        locationsToShow = new ArrayList<>();
        locationModels.getLocationsToShow().observe(this, new Observer<List<Location>>() {
            @Override
            public void onChanged(List<Location> locations) {
                locationsToShow = locations;
            }
        });

        // Create locations models from database using live data (i.e. results will appear when retrieved from database)
        mModels = new ArrayList<>();
        locationModels.getLocationModels().observe(this, new Observer<List<LocationModel>>() {
            @Override
            public void onChanged(List<LocationModel> locationModels) {
                mModels = locationModels;
                timelineItemAdapter.add(mModels); // Add models to adapter
            }
        });

        return timelineBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MapsActivity parentActivity = (MapsActivity) this.getActivity();
        assert parentActivity != null;
        parentActivity.getNavigationView().getMenu().findItem(R.id.nav_timeline).setChecked(true);
        parentActivity.setActionBarTitle(getString(R.string.navigation_timeline));
    }
}
