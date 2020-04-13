package com.nearchitectural.ui.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.signature.ObjectKey;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.nearchitectural.GlideApp;
import com.nearchitectural.R;
import com.nearchitectural.utilities.Settings;

import java.util.Map;

/* Author:  Kristiyan Doykov
 * Since:   13/12/19
 * Version: 1.0
 * Purpose: Handles the retrieval of information for and rendering of a custom information
 *          window on the Map Activity when a marker is tapped
 */
public class MapMarkerWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private final String TAG = "MapMarkerWindowAdapter"; // Tag used for logging status of application

    private final View window; // The window view itself
    private ImageView thumbnailImage; // View holding the thumbnail image
    private Map<Marker, String> markerThumbnailMap; // Map of markers to their corresponding thumbnailURLs

    // Constructor initialises necessary attributes
    public MapMarkerWindowAdapter(Context context, Map<Marker, String> markerThumbnailMap) {
        this.markerThumbnailMap = markerThumbnailMap;
        window = View.inflate(context, R.layout.custom_info_panel, null);
    }

    // Renders a window containing the location title and summary for the selected marker
    private void renderWindowText(final Marker marker, View view) {

        String title = marker.getTitle(); // Get marker title (location name)
        final Context context = view.getContext(); // Initialise view

        // Initialise UI layout references
        TextView textViewTitle = view.findViewById(R.id.title);
        thumbnailImage = view.findViewById(R.id.picture);
        displayImage(context, marker, markerThumbnailMap.get(marker));

        // Set window information
        textViewTitle.setText(title);
        String snippet = marker.getSnippet();
        TextView textViewSnippet = view.findViewById(R.id.snippet);

        // Display empty string for snippet if no snippet found
        if (snippet != null && !snippet.equals("")) {
            textViewSnippet.setText(snippet);
        } else {
            textViewSnippet.setText("");
        }

        if (Settings.getInstance().getFontSize() == R.style.FontStyle_Large) {
            textViewSnippet.setMaxLines(4 - textViewTitle.getLineCount());
        } else {
            textViewSnippet.setMaxLines(6 - textViewTitle.getLineCount());
        }

        thumbnailImage.setContentDescription(title);
    }

    // Handles the displaying of the thumbnail image associated with the location
    private void displayImage(Context context, final Marker marker, String thumbnailURL) {

        Log.d(TAG, thumbnailURL);

        // Display and render thumbnail as an image inside window using URL
        GlideApp.with(context.getApplicationContext())
                .load(thumbnailURL)
                .centerCrop()
                .override(520, 520)
                .error(R.drawable.ic_error_message)
                .placeholder(R.drawable.ic_loading_message)
                .signature(new ObjectKey(thumbnailURL.hashCode()))
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        /* When image is loaded, post to main thread via runnable
                        *  taken and adapted from the following link:
                        *  https://github.com/bumptech/glide/issues/2920 */
                        new Handler().post(new Runnable() {
                            @Override
                            public void run() {
                                if (marker.isInfoWindowShown()) {
                                    marker.showInfoWindow();
                                }
                            }
                        });
                        return false;
                    }
                })
                .into(thumbnailImage);

    }

    @Override
    public View getInfoWindow(Marker marker) {
        renderWindowText(marker, window);
        return window;
    }

    @Override
    public View getInfoContents(Marker marker) {
        renderWindowText(marker, window);
        return window;
    }
}