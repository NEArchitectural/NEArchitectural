package com.nearchitectural.ui.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.firestore.FirebaseFirestore;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.nearchitectural.GlideApp;
import com.nearchitectural.R;
import com.nearchitectural.databinding.FragmentLocationBinding;
import com.nearchitectural.ui.activities.MapsActivity;
import com.nearchitectural.ui.adapters.LocationSlideshowAdapter;
import com.nearchitectural.utilities.DatabaseExtractor;
import com.nearchitectural.utilities.Settings;
import com.nearchitectural.utilities.SettingsManager;
import com.nearchitectural.utilities.TagID;
import com.nearchitectural.utilities.models.Location;
import com.nearchitectural.utilities.models.Report;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/* Author:  Kristiyan Doykov, Joel Bell-Wilding
 * Since:   20/01/20
 * Version: 1.2
 * purpose: Presents information and images regarding a given location
 */
public class LocationFragment extends Fragment implements OnMapReadyCallback {

    public static final String TAG = "LocationFragment";

    // Binding between location fragment and layout
    private FragmentLocationBinding locationBinding;

    // LAYOUT ELEMENTS
    private List<TextView> tagsTextViews; // List of text views for important tags
    private ImageView thumbnail;
    private TextView title;
    private TextView locationType;
    private TextView summary;
    private ImageView allTagsIcon;
    private TextView referencesHeading;
    private TextView referencesBody;
    private TextView likesCount;
    private TextView reportText;
    private Button navigateButton;
    private Button showReferencesButton;
    private LikeButton likeButton;
    private ViewPager slideshow;
    private TextView slideshowNumber;

    private LocationSlideshowAdapter locationSlideshowAdapter; // Adapter for slideshow
    private DatabaseExtractor extractor; // Used to extract location/report information from database
    private Location location; // Location object to contain all the info
    private Report locationReport; // Report object to contain full location report and slideshow images
    private MarkerOptions marker; // Information representing the location marker to be placed on the google map

    // Assign locationID when fragment is instantiated
    public static LocationFragment newInstance(String locationID) {
        Bundle locationIDBundle = new Bundle();
        locationIDBundle.putString("locationID", locationID);
        LocationFragment locationFragment = new LocationFragment();
        locationFragment.setArguments(locationIDBundle);
        return locationFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // Data binding
        locationBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_location, container, false);

        // Bind layout elements
        thumbnail = locationBinding.thumbnail;
        title = locationBinding.title;
        locationType = locationBinding.locationType;
        tagsTextViews = new ArrayList<>();
        tagsTextViews.add(locationBinding.tagOne);
        tagsTextViews.add(locationBinding.tagTwo);
        tagsTextViews.add(locationBinding.tagThree);
        allTagsIcon = locationBinding.allTags;
        referencesHeading = locationBinding.referencesHeading;
        showReferencesButton = locationBinding.showReferencesButton;
        referencesBody = locationBinding.references;
        likesCount = locationBinding.likesCount;
        navigateButton = locationBinding.navigateButton;
        slideshow = locationBinding.slideshow;
        slideshowNumber = locationBinding.slideshowNumber;
        likeButton = locationBinding.likeButton;

        // Instantiate extractor for retrieval of location information from database
        extractor = new DatabaseExtractor();
        retrieveLocation();

        // Hides references by default
        referencesBody.setVisibility(View.GONE);
        referencesHeading.setVisibility(View.GONE);
        showReferencesButton.setTransformationMethod(null);

        // Listener for toggling between showing references and hiding them
        showReferencesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (referencesHeading.getVisibility() == View.GONE) {
                    referencesHeading.setVisibility(View.VISIBLE);
                    referencesBody.setVisibility(View.VISIBLE);
                    showReferencesButton.setText(R.string.hide_references);
                } else {
                    referencesHeading.setVisibility(View.GONE);
                    referencesBody.setVisibility(View.GONE);
                    showReferencesButton.setText(R.string.show_references);
                }
            }
        });

        /* Navigate to Google maps via the "Take Me Here" button,
        *  displaying a route to the current location */
        navigateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(String.format(Locale.ENGLISH,
                        "google.navigation:q=%f,%f", location.getLatitude(), location.getLongitude()));
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, uri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });
        navigateButton.setTransformationMethod(null);

        // Listener for liking or unliking a location
        likeButton.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                likeLocation();
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                unlikeLocation();
            }
        });

        // Adds a listener for clicking the additional tags arrows
        allTagsIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAllTags();
            }
        });

        return locationBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // Get a reference to the parent activity
        MapsActivity parentActivity = (MapsActivity) this.getActivity();
        // Set the title of the action bar
        assert parentActivity != null;
        parentActivity.setActionBarTitle(getString(R.string.navigation_location_details));
        // Unset the selection from the navigation menu
        int size = parentActivity.getNavigationView().getMenu().size();
        for (int i = 0; i < size; i++) {
            parentActivity.getNavigationView().getMenu().getItem(i).setChecked(false);
        }
    }

    // Getter for location
    public Location getLocation() {
        return location;
    }

    // Setter for location
    public void setLocation(Location location) {
        this.location = location;
    }

    // Displays up to three active high priority tags alongside thumbnail image
    private void displayImportantTags(Map<TagID, Boolean> tagValues) {
        // Cycle through each text view and set text to tag value and icon
        for (TextView tagTextView : tagsTextViews) {
            for (TagID tag : TagID.values())
                if (tagValues.get(tag)) {
                    tagTextView.setText(tag.displayName);
                    // Get icon associated with tag
                    int iconID = getResources().getIdentifier(tag.iconName,
                            "drawable", getActivity().getPackageName());
                    tagTextView.setCompoundDrawablesWithIntrinsicBounds(iconID, 0, 0, 0);
                    tagValues.put(tag, false); // Flag that tag no longer needs to be displayed
                    break;
                }
            // Hide text view if less than three tags are active
            if (tagTextView.getText().equals("")) {
                tagTextView.setVisibility(View.GONE);
            }
        }
        // Hide "more tags" button if exactly three tags are active
        if (!tagValues.containsValue(true)) {
            allTagsIcon.setVisibility(View.GONE);
        }
    }

    // Retrieves location from database using location ID and handles appropriate UI bindings
    private void retrieveLocation() {

        assert getArguments() != null;
        // ID of the location to be displayed
        String locationID = getArguments().getString("locationID");

        // Retrieve location info via callback and update UI with retrieved information
        extractor.extractLocationByID(locationID, new DatabaseExtractor.DatabaseCallback<Location>() {
            @Override
            public void onDataRetrieved(Location data) {
                if (data != null) {
                    location = data;
                    locationBinding.setLocation(location); // Set selected location as data binding model
                    // Handle displaying UI elements which use location values
                    likeButton.setLiked(Settings.getInstance().locationIsLiked(location.getId()));
                    displayImportantTags(new LinkedHashMap<>(location.getAllTags()));
                    displayThumbnail(location.getThumbnailURL());
                    retrieveReport(location.getReportID());
                    initialiseMap();
                }
            }
        });
    }

    // Retrieves report corresponding to location from database and handles appropriate UI bindings
    private void retrieveReport(String reportID) {
        extractor.extractReport(reportID, new DatabaseExtractor.DatabaseCallback<Report>() {
            @Override
            public void onDataRetrieved(Report data) {
                if (data != null) {
                    locationReport = data;
                    locationBinding.setReport(locationReport);
                    reportText = locationBinding.reportText;
                    createSlideshow();
                }
            }
        });
    }

    // Sets up the slideshow ViewPager and accompanying slideshow number text
    private void createSlideshow() {
        // Set up adapter for slideshow once slideshow URLs are retrieved
        locationSlideshowAdapter = new LocationSlideshowAdapter(
                LocationFragment.this.getContext(),
                new ArrayList<>(locationReport.getSlideshowURLs()));
        slideshow.setAdapter(locationSlideshowAdapter);

        // Changes the current image index value on the slideshow TextView
        slideshowNumber.setText(String.format(getString(R.string.slideshow_text), 1, locationSlideshowAdapter.getCount()));
        slideshow.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                // Set index value for image (i.e. 1/3 or 2/4)
                slideshowNumber.setText(String.format(getString(R.string.slideshow_text), slideshow.getCurrentItem()+1, locationSlideshowAdapter.getCount()));
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    // Displays location thumbnail
    private void displayThumbnail(String thumbnailURL) {
        GlideApp.with(getActivity())
                .load(thumbnailURL)
                .centerCrop()
                .override(525, 525)
                .error(R.drawable.ic_error_message)
                .placeholder(R.drawable.ic_loading_message)
                .into(thumbnail);
    }

    // Displays a popup for showing all active tags for the location
    private void showAllTags() {

        // Implement custom list adapter
        ListAdapter tagsAdapter = new ArrayAdapter<TagID>(getContext(), R.layout.tags_list_view, location.getActiveTags()) {

            public View getView(int position, View convertView, ViewGroup parent) {

                View tagView;

                // Initialise for the appropriate view
                if (convertView == null) {
                    LayoutInflater inflater = LayoutInflater.from((getContext()));
                    tagView = inflater.inflate(R.layout.tags_list_view, parent, false);
                } else {
                    tagView = convertView;
                }

                // Get text view for tag name and icon
                TextView mText = tagView.findViewById(R.id.tag_name);

                // Set the text view to contain the tag display name and icon
                TagID tag = getItem(position);
                mText.setText(tag.displayName);
                int iconID = getContext().getResources().getIdentifier(tag.iconName , "drawable", getContext().getPackageName());
                mText.setCompoundDrawablesWithIntrinsicBounds(iconID, 0, 0, 0);
                mText.setCompoundDrawablePadding(5);

                return tagView;
            }
        };

        // Display the tags list as a material dialog
        new MaterialAlertDialogBuilder(getContext(), R.style.ThemeOverlay_MaterialComponents)
                .setTitle(R.string.show_all_tags)
                .setAdapter(tagsAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setPositiveButton(R.string.ok, null)
                .show();

    }

    // Handles the incrementing of likes for location across database and application
    private void likeLocation() {
        if (location != null) {

            // Make local and UI changes to like count
            location.addLike();
            likesCount.setText(String.valueOf(location.getLikes()));
            // Update settings with new liked location
            Settings.getInstance().addLikedLocation(location.getId());
            new SettingsManager(getContext()).saveSettings();
            location.getAllTags().put(TagID.LIKED_BY_YOU, true);

            // Access location document via database
            FirebaseFirestore.getInstance()
                    .collection("locations")
                    .document(location.getId())
                    //Increment likes field by one
                    .update("likes", location.getLikes());
        }
    }

    // Handles the decrementing of likes for location across database and application
    private void unlikeLocation() {
        if (location != null) {

            // Make local and UI changes to like count
            location.removeLike();
            likesCount.setText(String.valueOf(location.getLikes()));
            // Update settings by removing location from liked locations
            Settings.getInstance().removeLikedLocation(location.getId());
            new SettingsManager(getContext()).saveSettings();
            location.getAllTags().put(TagID.LIKED_BY_YOU, false);

            // Access location document via database
            FirebaseFirestore.getInstance()
                    .collection("locations")
                    .document(location.getId())
                    // Decrement likes field by one
                    .update("likes", location.getLikes());
        }
    }

    // Initialises up the Google Map fragment and map marker
    private void initialiseMap() {
        marker = extractor.parseMapMarker(location);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Once map is ready, add marker and move camera to appropriate position
        googleMap.addMarker(marker);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 12.5f));
    }

    @Override
    public void onPause() {
        super.onPause();
        // Destroy instance of database to prevent unwanted callbacks
        extractor.cancelCallbacksAndDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Create new instance of database if previous instance is destroyed
        extractor.restoreInstance();
    }
}
