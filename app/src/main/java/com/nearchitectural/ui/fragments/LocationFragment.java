package com.nearchitectural.ui.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.nearchitectural.GlideApp;
import com.nearchitectural.R;
import com.nearchitectural.databinding.FragmentLocationBinding;
import com.nearchitectural.ui.activities.MapsActivity;
import com.nearchitectural.ui.adapters.AllTagsAdapter;
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
public class LocationFragment extends Fragment {
    public static final String TAG = "LocationFragment";

    // Binding between location fragment and layout
    FragmentLocationBinding locationBinding;

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

    private String locationID; // ID of the location to be displayed
    private LocationSlideshowAdapter locationSlideshowAdapter; // Adapter for slideshow
    private FirebaseFirestore db; // Database reference field
    private Location location; // Location object to contain all the info
    private Report locationReport; // Report object to contain full location report and slideshow images

    // Assign locationID when fragment is instantiated
    public LocationFragment(String locationID) {
        this.locationID = locationID;
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
        likeButton = locationBinding.likeButton;

        /* Get an instance of the database in order to
         retrieve/update the data for the specific location */
        db = FirebaseFirestore.getInstance();
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

                // Increments value of likes attribute for location document in database
                db.collection("locations")
                        .document(location.getId())
                        .update("likes", location.getLikes() + 1)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                            /* If database update is successful, add like to
                            location object and update value on UI */
                                location.addLike();
                                likesCount.setText(String.valueOf(location.getLikes()));
                                // Update settings with new liked location
                                Settings.getInstance().addLikedLocation(location.getId());
                                new SettingsManager(getContext()).saveSettings();
                                location.getAllTags().put(TagID.LIKED_BY_YOU, true);
                            }
                        });
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                // Decrements value of likes attribute for location document in database
                db.collection("locations")
                        .document(location.getId())
                        .update("likes", location.getLikes() - 1)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                /* If database update is successful, remove like from
                                location object and update value on UI */
                                location.removeLike();
                                likesCount.setText(String.valueOf(location.getLikes()));
                                // Update settings by removing location from liked locations
                                Settings.getInstance().removeLikedLocation(location.getId());
                                new SettingsManager(getContext()).saveSettings();
                                location.getAllTags().put(TagID.LIKED_BY_YOU, false);
                            }
                        });
            }
        });

        // Adds a listener for clicking the additional tags arrows
        allTagsIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAllFilters();
            }
        });

        return locationBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // Get a reference to the parent activity
        MapsActivity parentActivity = (MapsActivity) this.getActivity();
        // Set the title of the action bar
        parentActivity.setActionBarTitle("Details");
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
        db.collection("locations")
                .document(locationID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            // Set up binding for location info once report is retrieved
                            location = DatabaseExtractor.extractLocation(task.getResult().getId(), task.getResult().getData());
                            locationBinding.setLocation(location); // Set selected location as data binding model
                            // Handle displaying UI elements which use location values
                            likeButton.setLiked(Settings.getInstance().locationIsLiked(location.getId()));
                            displayImportantTags(new LinkedHashMap<>(location.getAllTags()));
                            // Display location thumbnail and retrieve location report
                            displayThumbnail();
                            retrieveReport();
                        } else {
                            Log.w(TAG, "Error getting report.", task.getException());
                        }
                    }
                });
    }

    // Retrieves report corresponding to location from database and handles appropriate UI bindings
    private void retrieveReport() {
        db.collection("reports")
                .document(location.getReportID())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            // Set up binding for location info once report is retrieved
                            locationReport = DatabaseExtractor.extractReport(task.getResult());
                            locationBinding.setReport(locationReport);
                            reportText = locationBinding.reportText;

                            // Set up adapter for slideshow once slideshow URLs are retrieved
                            locationSlideshowAdapter = new LocationSlideshowAdapter(
                                    LocationFragment.this.getContext(),
                                    new ArrayList<>(locationReport.getSlideshowURLs()));

                            slideshow.setAdapter(locationSlideshowAdapter);
                        } else {
                            Log.w(TAG, "Error getting report.", task.getException());
                        }
                    }
                });
    }

    // Displays location thumbnail
    private void displayThumbnail() {
        GlideApp.with(getActivity())
                .load(location.getThumbnailURL())
                .centerCrop()
                .override(525, 525)
                .error(R.drawable.ic_launcher_background)
                .placeholder(R.mipmap.ic_launcher_round)
                .into(thumbnail);
    }

    /* Handle the popup for showing all tags - taken and adapted
     * from the following link: https://mkyong.com/android/android-custom-dialog-example/ */
    private void showAllFilters() {
        // Create a custom dialog
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.all_tags_dialog);

        // Use custom adapter to display all active tags and icons
        ListView listOfTags = dialog.findViewById(R.id.all_tags_list);
        listOfTags.setAdapter(new AllTagsAdapter(getContext(), location.getActiveTags()));

        Button dialogButton = dialog.findViewById(R.id.dialog_button_ok);
        // If button is clicked, close the custom dialog
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
