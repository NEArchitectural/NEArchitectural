package com.nearchitectural.ui.fragments;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.google.android.gms.maps.model.LatLng;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.nearchitectural.R;
import com.nearchitectural.databinding.FragmentTimelineBinding;
import com.nearchitectural.databinding.FragmentTimelineLandscapeBinding;
import com.nearchitectural.ui.activities.MapsActivity;
import com.nearchitectural.ui.adapters.TimelineItemAdapter;
import com.nearchitectural.ui.models.LocationModel;
import com.nearchitectural.ui.models.ModelController;
import com.nearchitectural.ui.models.TimelineModel;
import com.nearchitectural.utilities.CurrentCoordinates;
import com.nearchitectural.utilities.DatabaseExtractor;
import com.nearchitectural.utilities.Filter;
import com.nearchitectural.utilities.ImageFader;
import com.nearchitectural.utilities.Settings;
import com.nearchitectural.utilities.comparators.NewestToOldestComparator;
import com.nearchitectural.utilities.comparators.OldestToNewestComparator;
import com.nearchitectural.utilities.models.Report;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/* Author:  Kristiyan Doykov, Joel Bell-Wilding
 * Since:   10/12/19g
 * Version: 1.2
 * Purpose: Visually display a timeline showing locations in both chronological and reverse chronological
 *          order, as well as displaying additional architectural/timeline information
 */
public class TimelineFragment extends Fragment {

    public static final String TAG = "TimelineFragment";

    // LAYOUT ELEMENTS
    private RecyclerView timelineRecyclerView; // The recycler representing the timeline
    private ImageView activeImage;
    private ImageView inactiveImage;
    private TextView locationTitle;
    private TextView locationInfo;
    private Button openLocationPageButton;
    private TextView initialTextview;
    private LinearLayout locationInfoContainer;
    private Switch settingsFilterSwitch;
    private Spinner orderSpinner;
    private RelativeLayout progressBarContainer;

    private LinearLayoutManager recyclerManager; // Layout manager for the timeline recycler
    private ModelController timelineModelController; // ModelController to maintain/update timeline models
    private TimelineItemAdapter timelineItemAdapter; // Recycler adapter for location models
    private Map<String, TimelineModel> modelIDMap; // Map of location IDs to their corresponding timeline models
    private ImageFader locationImageFader; // Utility to display cross-fade slideshow of location images

    private boolean locationsRetrieved = false; // Indicates if locations have been retrieved initially
    private boolean isLandscape = false; // Indicates if device is in landscape orientation
    private boolean mustApplyFilters = true; // Indicates if filters must be applied to locations
    // Indicates if spinner listener should fire based on whether it is triggered by user or not
    private boolean fireSpinnerListener = false;
    private boolean isOldestToNewest = true; // Indicates if timeline order is default oldest to newest or not

    /* Variables used to determine if search results must be updated (i.e. if a
     * current value is different from its 'last' value, search results need updating */
    private HashSet<String> lastLikedLocationIDs;
    private double lastLatitude;
    private double lastLongitude;

    private Report displayedReport; // Report currently being displayed when timeline item pressed (used for caching)
    private String displayedLocationID; // Location ID of location being viewed when timeline item pressed
    private String openedLocationID; // Location ID of location opened after pressing "find out more" button

    // Bundle keys for retrieving and storing UI state restoration information
    private static final String SCROLL_KEY = "SCROLL_POSITION";
    private static final String DISPLAYED_LOCATION_KEY = "DISPLAYED_LOCATION_ID";
    private static final String APPLY_FILTERS_KEY = "MUST_APPLY_FILTERS";
    private static final String REPORT_ID_KEY = "DISPLAYED_REPORT_ID";
    private static final String REPORT_SNIPPET_KEY = "TIMELINE_SNIPPET";
    private static final String REPORT_URLS_KEY = "REPORT_SLIDESHOW_URLS";
    private static final String ORIGINAL_ORDER_KEY = "IS_ORIGINAL_ORDER";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Determine if the user's device is in landscape
        if (getActivity() != null) {
            isLandscape = getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
        }

        // Get values for applying filters and initial timeline order if a configuration change occurred
        if (getArguments() != null) {
            mustApplyFilters = getArguments().getBoolean(APPLY_FILTERS_KEY);
            isOldestToNewest = getArguments().getBoolean(ORIGINAL_ORDER_KEY);
        }

        // Set the view model for displaying locations in the timeline
        timelineModelController = ViewModelProviders.of(this).get(ModelController.class);

        // Set the initial comparator for the timeline (oldest to newest unless a configuration change has occurred)
        if (isOldestToNewest) {
            timelineItemAdapter = new TimelineItemAdapter(this, new OldestToNewestComparator());
        } else {
            timelineItemAdapter = new TimelineItemAdapter(this, new NewestToOldestComparator());
        }

        modelIDMap = new HashMap<>(); // Instantiate map for use

        // Create timeline models using live data (i.e. results will appear when retrieved from database)
        timelineModelController.updateAllLocationModels().observe(this, new Observer<Map<String, LocationModel>>() {
            @Override
            public void onChanged(Map<String, LocationModel> locationModelIDMap) {
                updateTimeline(locationModelIDMap); // When locations are retrieved from db, update the timeline
                // Upon first retrieval of locations, restore recycler state
                if (!locationsRetrieved) {
                    restoreUIState();
                }
                locationsRetrieved = true; // Indicate initial db retrieval is successful
            }
        });
        // Get the initial state of the user's liked locations
        lastLikedLocationIDs = Settings.getInstance().getLikedLocations();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // Get data binding layout elements
        FragmentTimelineBinding timelineBinding = null;
        FragmentTimelineLandscapeBinding timelineLandscapeBinding = null;

        // Indicate to models to display information appropriate for the layout orientation
        for (TimelineModel model : modelIDMap.values()) {
            model.setLandscapeLayout(isLandscape);
        }

        // Bind UI elements to landscape layout or portrait layout depending on user's device orientation
        if (isLandscape) {
            timelineLandscapeBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_timeline_landscape, container, false);
            // Bind all UI elements (landscape)
            timelineRecyclerView = timelineLandscapeBinding.timelineRecycler;
            activeImage = timelineLandscapeBinding.locationImageviewOne;
            inactiveImage = timelineLandscapeBinding.locationImageviewTwo;
            locationTitle = timelineLandscapeBinding.locationTitle;
            locationInfo = timelineLandscapeBinding.timelineInfo;
            openLocationPageButton = timelineLandscapeBinding.openLocationButton;
            locationInfoContainer = timelineLandscapeBinding.timelineLocationInfo;
            initialTextview = timelineLandscapeBinding.timelineInitialTextview;
            settingsFilterSwitch = timelineLandscapeBinding.settingsFiltersSwitch;
            orderSpinner = timelineLandscapeBinding.timelineOrderSpinner;
            progressBarContainer = timelineLandscapeBinding.progressBarContainer;
        } else {
            timelineBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_timeline, container, false);
            // Bind all UI elements (portrait)
            timelineRecyclerView = timelineBinding.timelineRecycler;
            activeImage = timelineBinding.locationImageviewOne;
            inactiveImage = timelineBinding.locationImageviewTwo;
            locationTitle = timelineBinding.locationTitle;
            locationInfo = timelineBinding.timelineInfo;
            openLocationPageButton = timelineBinding.openLocationButton;
            locationInfoContainer = timelineBinding.timelineLocationInfo;
            initialTextview = timelineBinding.timelineInitialTextview;
            settingsFilterSwitch = timelineBinding.settingsFiltersSwitch;
            orderSpinner = timelineBinding.timelineOrderSpinner;
            progressBarContainer = timelineBinding.progressBarContainer;
        }

        // Create an image fader to perform slideshow in timeline info snippet box
        locationImageFader = new ImageFader(activeImage, inactiveImage, null, getActivity());

        // Turn animations off to prevent flicker upon changed like/dislike count
        if (timelineRecyclerView.getItemAnimator() != null) {
            ((SimpleItemAnimator) timelineRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        }

        // Defines a layout manager which allows the timeline recycler to be scrolled horizontally
        recyclerManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        timelineRecyclerView.setLayoutManager(recyclerManager); // Set manager
        timelineRecyclerView.setAdapter(timelineItemAdapter); // Set adapter for recycler view

        // Initialise the order spinner
        initialiseSpinner();

        // Open location page when the "Find out more" button is pressed
        openLocationPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLocationPage();
            }
        });

        // Apply filters upon creation of view if needed
        if (locationsRetrieved && mustApplyFilters)
            applyFilters();
        // Set app-wide filters switch to on/off
        settingsFilterSwitch.setChecked(mustApplyFilters);
        // Listener used to apply/unapply app-wide filters if switch is on/off
        settingsFilterSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mustApplyFilters = isChecked;
                if (isChecked) {
                    applyFilters(); // Apply filters if checked
                } else {
                    // If not checked, display all locations on timeline
                    for (TimelineModel model : modelIDMap.values()) {
                        timelineItemAdapter.add(model);
                    }
                    timelineItemAdapter.notifyDataSetChanged();
                }
                // Scroll to start of timeline
                timelineRecyclerView.scrollToPosition(0);
            }
        });

        // Return the correct binding based on device orientation
        return isLandscape ? timelineLandscapeBinding.getRoot() : timelineBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set the navigation bar title and navigation menu item
        MapsActivity parentActivity = (MapsActivity) this.getActivity();
        assert parentActivity != null;
        parentActivity.getNavigationView().getMenu().findItem(R.id.nav_timeline).setChecked(true);
        parentActivity.setActionBarTitle(getString(R.string.navigation_timeline));
    }

    @Override
    public void onPause() {
        // When activity is paused, record the last known values of each variable
        lastLikedLocationIDs = new HashSet<>(Settings.getInstance().getLikedLocations());
        lastLatitude = CurrentCoordinates.getCoords().latitude;
        lastLongitude = CurrentCoordinates.getCoords().longitude;
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();

        // Update locations if model map values have not been retained
        if (modelIDMap.isEmpty()) {
            timelineModelController.updateAllLocationModels();
        }

        // Display timeline info for the previously selected/opened location open return to fragment
        if (displayedLocationID != null) {
            if (displayedReport == null) {
                String showLocation = displayedLocationID;
                displayedLocationID = null;
                getReportInfoAndDisplay(showLocation);
            } else {
                displayReportInfo(displayedLocationID, displayedReport);
            }
        } else if (openedLocationID != null && displayedReport != null) {
            displayReportInfo(openedLocationID, displayedReport);
        }

        // Check locations are retrieved to guard against initial launch errors
        if (locationsRetrieved) {
            // If user likes/unlikes locations, update the timeline to reflect this
            if (!lastLikedLocationIDs.equals(Settings.getInstance().getLikedLocations())) {
                // Gets all newly liked/unliked locations
               Object[] updateIDs = Sets.symmetricDifference(lastLikedLocationIDs, Settings.getInstance().getLikedLocations()).toArray();
               // Cycle through locations and update
                for (Object locationID : updateIDs) {
                    timelineModelController.updateLocationModel((String) locationID);
                }
                applyFilters();
            }

            // Determine if user's location has significantly changed from previous interaction and update results if so
            CurrentCoordinates.getInstance().getDeviceLocation(getActivity(), new CurrentCoordinates.LocationCallback<LatLng>() {
                @Override
                public void onLocationRetrieved(LatLng coordinates) {
                    double threshold = 0.001;
                    /* Update results if there is a significant difference between user's last known position
                     * and user's current position */
                    if ((Math.abs(lastLatitude-CurrentCoordinates.getCoords().latitude) > threshold)
                            || Math.abs(lastLongitude-CurrentCoordinates.getCoords().longitude) > threshold) {
                        timelineModelController.updateAllLocationModels();
                    }
                }
            });
        }
    }

    /* Updates the information of models on the timeline when ModelController detects
     * a change (e.g. call back from database provides location info) */
    private void updateTimeline(Map<String, LocationModel> locationModelIDMap) {
        // Cycle through models and produce TimelineModel for each
        for (LocationModel locationModel : locationModelIDMap.values()) {
            TimelineModel model = new TimelineModel(locationModel);
            model.setLandscapeLayout(isLandscape);
            modelIDMap.put(model.getId(), model);
        }
        applyFilters(); // Apply filters to account for changes to location data
    }

    // Sets up the spinner in the appropriate state to deal with timeline order
    private void initialiseSpinner() {
        // Create a new adapter with Oldest to Newest and Newest to Oldest options
        ArrayAdapter<CharSequence> orderAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.timeline_order_spinner_options, R.layout.custom_spinner_layout);
        orderAdapter.setDropDownViewResource(R.layout.custom_spinner_item);
        // Set adapter
        orderSpinner.setAdapter(orderAdapter);
        // Set item to Newest to Oldest initially if needed
        if (!isOldestToNewest) {
            orderSpinner.setSelection(1, false);
        }
        /* Listener to only trigger item selected listener when user touches spinner
         * Idea taken and adapted from the following:
         * https://stackoverflow.com/questions/27745948/android-spinner-onitemselected-called-multiple-times-after-screen-rotation*/
        orderSpinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                fireSpinnerListener = true; // Item selected code can now run
                return false;
            }
        });

        // Listener to handle changing of comparator for recycler adapter upon item selected
        orderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Only fire if listener is triggered by user input
                if (fireSpinnerListener) {
                    if (position == 0) {
                        timelineItemAdapter.changeComparator(new OldestToNewestComparator());
                        isOldestToNewest = true;
                    } else if (position == 1) {
                        timelineItemAdapter.changeComparator(new NewestToOldestComparator());
                        isOldestToNewest = false;
                    }
                    fireSpinnerListener = false; // Reset user input trigger boolean
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing since timeline does not need to be updated
            }
        });
    }

    // Restores the state of the UI from a configuration change
    private void restoreUIState() {
        // Check if configuration has changed
        if (getArguments() != null && recyclerManager != null) {
            // Restore scroll position
            recyclerManager.scrollToPosition(getArguments().getInt(SCROLL_KEY));
            // Retrieves the ID of the location being displayed if any
            displayedLocationID = getArguments().getString(DISPLAYED_LOCATION_KEY);
            // Display cached location/report information if present
            if (displayedLocationID != null) {
                String reportID = getArguments().getString(REPORT_ID_KEY);
                String timelineSnippet = getArguments().getString(REPORT_SNIPPET_KEY);
                String[] reportSlideshowURLs = getArguments().getStringArray(REPORT_URLS_KEY);
                displayedReport = new Report(reportID, null, Arrays.asList(reportSlideshowURLs), null, timelineSnippet);
                displayReportInfo(displayedLocationID, displayedReport);
            }
        }
    }

    // Retrieves the report for the provided location ID and displays information on the timeline
    public void getReportInfoAndDisplay(final String locationID) {
        // Ensure no location or provided location is not already selected
        if (displayedLocationID == null || !displayedLocationID.equals(locationID)) {
            initialTextview.setVisibility(View.GONE);
            progressBarContainer.setVisibility(View.VISIBLE); // Show progress bar while retrieving from db
            displayedLocationID = locationID;
            String reportID = modelIDMap.get(locationID).getLocationInfo().getReportID();
            // Get report from database
            new DatabaseExtractor().extractReport(reportID, new DatabaseExtractor.DatabaseCallback<Report>() {
                @Override
                public void onDataRetrieved(Report data) {
                    // Check db retrieval was successful
                    if (data != null) {
                        displayReportInfo(locationID, data);
                    } else {
                        // If unsuccessful db call, show error message
                        initialTextview.setText(R.string.location_report_error);
                        initialTextview.setVisibility(View.VISIBLE);
                        progressBarContainer.setVisibility(View.GONE);
                    }
                }
            });
        }
    }

    // Handles displaying the report content on the UI
    private void displayReportInfo(String locationID, Report report) {
        // Hide placeholder message and show location info
        locationInfoContainer.setVisibility(View.VISIBLE);
        progressBarContainer.setVisibility(View.GONE); // Hide progress bar once report is displayed
        initialTextview.setVisibility(View.GONE);
        this.locationTitle.setText(modelIDMap.get(locationID).getTitle());
        locationInfo.setText(report.getTimelineSnippet());
        // Start fading slideshow for location URLs
        locationImageFader.animateLocationSlideshow(report.getSlideshowURLs(), 5000, 1500);
        displayedLocationID = locationID;
        displayedReport = report;
    }

    // Handle a location card being pressed and take the user to the according Location page
    private void openLocationPage() {
        // Check location has been selected
        if (displayedLocationID != null) {
            openedLocationID = displayedLocationID;
            // Creates a new location fragment and opens it using the location ID
            LocationFragment lf = LocationFragment.newInstance(displayedLocationID);
            if (getActivity() != null) {
                ((MapsActivity) getActivity()).openFragment(lf, true);
                fireSpinnerListener = false;
            }
        }
        displayedLocationID = null;
    }

    // Filters locations on the timeline according to the settings criteria
    private void applyFilters() {

        // Cycle through all models and add/remove from adapter if they fit the criteria
        for (TimelineModel model : modelIDMap.values()) {
            if (Filter.locationMeetsSettingsCriteria(model.getLocationInfo()) || !mustApplyFilters) {
                timelineItemAdapter.add(model);
            } else {
                timelineItemAdapter.remove(model);
            }
        }
        timelineItemAdapter.notifyDataSetChanged(); // Inform adapter to redraw items on the recycler

        // Displays the placeholder text on the info box if selected location is filtered out
        if (openedLocationID != null && mustApplyFilters
                && !Filter.locationMeetsSettingsCriteria(modelIDMap.get(openedLocationID).getLocationInfo())) {
            locationInfoContainer.setVisibility(View.GONE);
            initialTextview.setVisibility(View.VISIBLE);
            initialTextview.setText(R.string.timeline_find_out_more);
        }

        // Indicates to the user that no locations match the specified criteria
        if (timelineItemAdapter.getItemCount() == 0) {
            ((MapsActivity) getActivity()).displayNoLocationsDialog();
            locationInfoContainer.setVisibility(View.GONE);
            initialTextview.setVisibility(View.VISIBLE);
            initialTextview.setText(R.string.no_locations_found);
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        /* Stores UI state information and creates a new timeline fragment to be displayed
         * upon configuration change. Allows UI to be redrawn for landscape/portrait layout
         * upon changing orientation of device */
        Bundle timelineState = new Bundle();
        // Saves the position of the recycler for restoring timeline state in new fragment
        timelineState.putInt(SCROLL_KEY, recyclerManager.findFirstCompletelyVisibleItemPosition());
        // Saves the boolean for the must apply filters switch
        timelineState.putBoolean(APPLY_FILTERS_KEY, mustApplyFilters);
        // Saves the state of the timeline order (i.e. oldest to newest vice versa)
        timelineState.putBoolean(ORIGINAL_ORDER_KEY, isOldestToNewest);
        // Saves the necessary report information to be displayed
        timelineState.putString(DISPLAYED_LOCATION_KEY, displayedLocationID);
        if (displayedReport != null) {
            String reportID = displayedReport.getReportID();
            String timelineSnippet = displayedReport.getTimelineSnippet();
            String[] reportSlideshowURLs = Iterables.toArray(displayedReport.getSlideshowURLs(), String.class);
            timelineState.putString(REPORT_ID_KEY, reportID);
            timelineState.putString(REPORT_SNIPPET_KEY, timelineSnippet);
            timelineState.putStringArray(REPORT_URLS_KEY, reportSlideshowURLs);
        }
        // Creates a new timeline fragment to use portrait/landscape layout upon configuration change
        TimelineFragment timelineFragment = new TimelineFragment();
        timelineFragment.setArguments(timelineState);

        // Launch new fragment with UI state restoration variables
        if (getFragmentManager() != null)
            getFragmentManager().beginTransaction().replace(R.id.fragment_container, timelineFragment).commit();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        locationImageFader.finishAnimating();
    }
}
