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
import androidx.fragment.app.Fragment;

import com.nearchitectural.R;
import com.nearchitectural.ui.activities.MapsActivity;
import com.nearchitectural.utilities.DatabaseExtractor;
import com.nearchitectural.utilities.ImageFader;
import com.nearchitectural.utilities.models.Location;

import java.util.ArrayList;
import java.util.List;

/* Author:  Joel Bell Wilding, Krystian Doykov
 * Since:   10/12/19
 * Version: 1.1
 * Purpose: Display information about the application and the people behind it
 */
public class AboutFragment extends Fragment {

    public static final String TAG = "AboutFragment";

    // LAYOUT ELEMENTS
    private ImageView headerImageViewOne;
    private ImageView headerImageViewTwo;
    private TextView title;

    private ImageFader headerImageFader;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_about,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        // Initialise layout elements
        headerImageViewOne = view.findViewById(R.id.header_image_one);
        headerImageViewTwo = view.findViewById(R.id.header_image_two);
        title = view.findViewById(R.id.about_ne_architectural);

        // Create ImageFader to handle animation for header image
        headerImageFader = new ImageFader(headerImageViewOne, headerImageViewTwo, title, getContext());

        // Gather the thumbnail URLs for all locations in the database
        DatabaseExtractor extractor = new DatabaseExtractor();
        extractor.extractAllLocations(new DatabaseExtractor.DatabaseCallback<List<Location>>() {
            @Override
            public void onDataRetrieved(List<Location> data) {
                // Check if database retrieval has failed
                if (data != null) {
                    List<String> imageURLs = new ArrayList<>();
                    for (Location location : data) {
                        imageURLs.add(location.getThumbnailURL());
                        Log.d(TAG, location.getThumbnailURL());
                    }
                    // Once all URLs gathered, animate header image
                    headerImageFader.animateLocationSlideshow(imageURLs, 5000, 1500);
                } // Else default image will be displayed
            }
        });

        super.onViewCreated(view, savedInstanceState);
        MapsActivity parentActivity = (MapsActivity) this.getActivity();
        assert parentActivity != null;
        parentActivity.getNavigationView().getMenu().findItem(R.id.nav_info).setChecked(true);
        parentActivity.setActionBarTitle(getString(R.string.navigation_about));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        headerImageFader.finishAnimating();
    }
}
