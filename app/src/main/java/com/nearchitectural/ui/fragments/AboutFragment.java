package com.nearchitectural.ui.fragments;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.nearchitectural.R;
import com.nearchitectural.ui.activities.MapsActivity;
import com.nearchitectural.utilities.DatabaseExtractor;
import com.nearchitectural.utilities.models.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/* Author:  Kristiyan Doykov
 * Since:   10/12/19
 * Version: 1.0
 * Purpose: To display information about the app
 *          and developer team.
 */
public class AboutFragment extends Fragment {

    public static final String TAG = "AboutFragment";

    // LAYOUT ELEMENTS
    private ImageView activeImage;
    private ImageView inactiveImage;
    private TextView title;

    private int position; // The position of the currently displayed image from the URL array
    private ArrayList<String> imageURLs; // A list of all location URLs
    private static final int DISPLAY_DURATION = 5000; // The time for which to display each image
    private static final int FADE_DURATION = 1500; // The time each image will transition via cross-fade

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_about,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        // Initialise layout elements
        activeImage = view.findViewById(R.id.header_image_one);
        inactiveImage = view.findViewById(R.id.header_image_two);
        title = view.findViewById(R.id.about_ne_architectural);

        // Gather the thumbnail URLs for all locations in the database
        DatabaseExtractor extractor = new DatabaseExtractor();
        extractor.extractAllLocations(new DatabaseExtractor.DatabaseCallback<List<Location>>() {
            @Override
            public void onDataRetrieved(List<Location> data) {
                // Check if database retrieval has failed
                if (data != null) {
                    imageURLs = new ArrayList<>();
                    for (Location location : data) {
                        imageURLs.add(location.getThumbnailURL());
                        Log.d(TAG, location.getThumbnailURL());
                    }
                    // Once all URLs gathered, animate header image
                    animateHeaderImage();
                } // Else default image will be displayed
            }
        });

        super.onViewCreated(view, savedInstanceState);
        MapsActivity parentActivity = (MapsActivity) this.getActivity();
        assert parentActivity != null;
        parentActivity.getNavigationView().getMenu().findItem(R.id.nav_info).setChecked(true);
        parentActivity.setActionBarTitle(getString(R.string.navigation_about));
    }

    /* Cross-fade between two image views over a duration
     * idea taken and adapted from the following link:
     * https://stackoverflow.com/questions/38906818/best-way-to-cross-fade-imageviews */
    private void fade(ImageView firstImage, ImageView secondImage) {
        firstImage.animate().alpha(0).setDuration(FADE_DURATION);
        secondImage.animate().alpha(1f).setDuration(FADE_DURATION);
        secondImage.bringToFront();
        title.bringToFront();
    }

    // Handles animation, transitioning and displaying of images in the header over a set interval
    private void animateHeaderImage() {

        // Get a random image URL to prevent same initial image being shown
        position = new Random().nextInt(imageURLs.size()-1);

        /* Handler and Runnable used to repeat changing images across intervals of time
        *  Following code taken and adapted from the following link:
        *  https://stackoverflow.com/questions/37905013/how-can-load-the-images-with-the-glide-in-imageswitcher */
        final Handler handler = new Handler();
        final Runnable changeImage = new Runnable() {
            @Override
            public void run() {
                if (getContext() != null) {
                    Glide.with(getContext())
                        .load(imageURLs.get(position))
                        .error(R.drawable.launcher_icon)
                        .placeholder(R.drawable.ic_loading_message)
                        .centerCrop()
                        .addListener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                // If image load failed, increment position but don't show next image
                                position++;
                                // If position reaches end of list, reset
                                if (position == imageURLs.size())
                                    position = 0;
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                // Increment position
                                position++;
                                // If position reaches end of list, reset
                                if (position == imageURLs.size())
                                    position = 0;
                                // Cross-fade images and swap active image
                                fade(activeImage, inactiveImage);
                                ImageView temp = activeImage;
                                activeImage = inactiveImage;
                                inactiveImage = temp;
                                return false;
                            }
                        })
                        .into(inactiveImage);
                }
            }
        };

        // Run the changeImage Runnable after every DISPLAY_DURATION milliseconds
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(changeImage);
            }
        }, 0, DISPLAY_DURATION);
    }
}
