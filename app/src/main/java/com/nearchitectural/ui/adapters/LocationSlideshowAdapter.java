package com.nearchitectural.ui.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.nearchitectural.GlideApp;
import com.nearchitectural.R;

import java.util.ArrayList;

/* Author:  Kristiyan Doykov
 * Since:   TODO: Fill in date
 * Version: 1.0
 * Purpose: Handles the displaying of slideshow images on the Location page
 */
public class LocationSlideshowAdapter extends PagerAdapter {

    private Context context;
    private ArrayList<String> imageURLs; // The list of URLs for images to be displayed

    public LocationSlideshowAdapter(Context context, ArrayList<String> imageURLs) {
        this.context = context;
        this.imageURLs = imageURLs;
    }

    @Override
    public int getCount() {
        return imageURLs.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    // Creates the next image to be displayed and returns it
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        ImageView imageView = new ImageView(context);

        // Formats and places the current image being viewed into an image view
        GlideApp.with(context)
                .load(imageURLs.get(position))
                .override(400, 400)
                .placeholder(R.drawable.ic_launcher_background)
                .into(imageView);

        container.addView(imageView);

        return imageView;
    }


    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
