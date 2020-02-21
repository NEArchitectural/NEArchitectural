package com.nearchitectural.ui.fragments;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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
import com.nearchitectural.ui.adapters.ViewPagerAdapter;
import com.nearchitectural.utilities.DatabaseExtractor;
import com.nearchitectural.utilities.TagID;
import com.nearchitectural.utilities.models.Location;
import com.nearchitectural.utilities.models.Report;

import java.util.ArrayList;
import java.util.Locale;

/* Author:  Kristiyan Doykov
 * Since:   TODO: Fill in date
 * Version: 1.0
 * purpose: Presents information and images regarding a given location
 */
public class LocationFragment extends Fragment {
    public static final String TAG = "LocationFragment";

    private String name;
    FragmentLocationBinding locationBinding;

    private ImageView thumbnail;
    private TextView title;
    private TextView locationType;
    private TextView summary;
    private TextView referencesHeading;
    private TextView referencesBody;
    private TextView wheelChairTag;
    private TextView childFriendlyTag;
    private TextView cheapTag;
    private TextView freeTag;
    private TextView reportText;
    private String reportTextString;
    private TextView likesCount;
    private Drawable ic_accessible;
    private Drawable ic_child;
    private Drawable ic_cheap;
    private Drawable ic_free;
    private ViewPager slideshow;
    private Button navigateButton;
    private Button showReferencesButton;
    private ViewPagerAdapter viewPagerAdapter;
    private com.like.LikeButton likeButton;
    // Arguments that came in with the intent
    private Bundle arguments;
    // Database reference field
    private FirebaseFirestore db;

    // Location object to contain all the info
    private Location location;
    private Report locationReport;

    public LocationFragment(Location location) {
        this.location = location;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Initialize the arguments field with what came in from the previous activity
        arguments = getArguments();

        locationBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_location, container, false);

        MapsActivity parentActivity = (MapsActivity) this.getActivity();

        // Get data for location here -> db call or passed in in the Bundle

        locationBinding.setModel(location);

        thumbnail = locationBinding.thumbnail;

        title = locationBinding.title;

        locationType = locationBinding.locationType;

        wheelChairTag = locationBinding.wheelChairTag;

        childFriendlyTag = locationBinding.childTag;

        cheapTag = locationBinding.cheapTag;

        freeTag = locationBinding.freeTag;

        reportText = locationBinding.reportText;

        referencesHeading = locationBinding.referencesHeading;

        referencesBody = locationBinding.references;

        referencesBody.setVisibility(View.GONE);

        referencesHeading.setVisibility(View.GONE);

        showReferencesButton = locationBinding.showReferencesButton;

        showReferencesButton.setTransformationMethod(null);

        showReferencesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(referencesHeading.getVisibility() == View.GONE){
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

        ic_accessible = getResources().getDrawable(R.drawable.ic_accessible);

        ic_child = getResources().getDrawable(R.drawable.ic_child_friendly);

        ic_cheap = getResources().getDrawable(R.drawable.ic_cheap);

        ic_free = getResources().getDrawable(R.drawable.ic_free);

        likesCount = locationBinding.likesCount;

        navigateButton = locationBinding.navigateButton;

        /* These lines are for navigating through Google maps forcefully
         * will be used when someone presses "Take me here" button or whatever we call it */
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

        slideshow = locationBinding.slideshow;

        /* Get an instance of the database in order to
         retrieve/update the data for the specific location */
        db = FirebaseFirestore.getInstance();

        getLocationReport();

        likeButton = locationBinding.likeButton;

        likeButton.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                final long[] likes = new long[1];
                db.collection("locations")
                        .document(location.getId())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    if (task.getResult() != null) {
                                        likes[0] = (long) task.getResult().getData().get("likes");
                                        db.collection("locations")
                                                .document(location.getId())
                                                .update("likes", likes[0] + 1)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        likesCount.setText(String.valueOf(likes[0] + 1));
                                                    }
                                                });
                                    }
                                }
                            }
                        });


            }

            @Override
            public void unLiked(LikeButton likeButton) {
                final long[] likes = new long[1];
                db.collection("locations")
                        .document(location.getId())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    if (task.getResult() != null) {
                                        likes[0] = (long) task.getResult().getData().get("likes");
                                        db.collection("locations")
                                                .document(location.getId())
                                                .update("likes", likes[0] - 1)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        likesCount.setText(String.valueOf(likes[0] - 1));
                                                    }
                                                });
                                    }
                                }
                            }
                        });
            }
        });

        if (location.getTagValue(TagID.WHEELCHAIR_ACCESSIBLE)) {
            wheelChairTag.setText(R.string.wheelChair_accessibility_text);
            wheelChairTag.setCompoundDrawablesWithIntrinsicBounds(ic_accessible, null, null, null);
        } else {
            wheelChairTag.setVisibility(View.GONE);
        }

        if (location.getTagValue(TagID.CHILD_FRIENDLY)) {
            childFriendlyTag.setText(R.string.child_friendly_text);
            childFriendlyTag.setCompoundDrawablesWithIntrinsicBounds(ic_child, null, null, null);
        } else {
            childFriendlyTag.setVisibility(View.GONE);
        }

        if (location.getTagValue(TagID.CHEAP_ENTRY)) {
            cheapTag.setText(R.string.cheap_entry_text);
            cheapTag.setCompoundDrawablesWithIntrinsicBounds(ic_cheap, null, null, null);
        } else {
            cheapTag.setVisibility(View.GONE);
        }

        if (location.getTagValue(TagID.FREE_ENTRY)) {
            freeTag.setText(R.string.free_entry_text);
            freeTag.setCompoundDrawablesWithIntrinsicBounds(ic_free, null, null, null);
        } else {
            freeTag.setVisibility(View.GONE);
        }

        GlideApp.with(parentActivity)
                .load(location.getThumbnailURL())
                .override(600, 600)
                .error(R.drawable.ic_launcher_background)
                .placeholder(R.drawable.ic_launcher_background)
                .into(thumbnail);

        return locationBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // Get a reference to the parent activity
        MapsActivity parentActivity = (MapsActivity) this.getActivity();
        // Set the title of the action bar
        parentActivity.setActionBarTitle("Details");

    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    private void getLocationReport() {
        db.collection("reports")
                .document(location.getReportID())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            locationReport = DatabaseExtractor.extractReport(task.getResult());

                            if (!locationReport.getParagraphs().isEmpty()) {

                                StringBuilder sBuilder = new StringBuilder();
                                sBuilder.append("\n");
                                for (String paragraph : locationReport.getParagraphs()) {
                                    sBuilder.append(paragraph + "\n\n");
                                }

                                reportTextString = sBuilder.toString();
                                reportTextString = reportTextString.substring(0, reportTextString.length()-2);
                                reportText.setText(reportTextString);

                            } else {
                                reportText.setVisibility(View.GONE);
                            }

                            viewPagerAdapter = new ViewPagerAdapter(LocationFragment.this.getContext(), new ArrayList<>(locationReport.getSlideshowURLs()));
                            slideshow.setAdapter(viewPagerAdapter);
                        } else {
                            Log.w(TAG, "Error getting report.", task.getException());
                        }
                    }
                });
    }
}
