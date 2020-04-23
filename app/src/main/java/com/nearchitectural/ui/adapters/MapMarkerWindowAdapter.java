package com.nearchitectural.ui.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.nearchitectural.R;
import com.nearchitectural.utilities.Settings;

import java.util.HashMap;
import java.util.Map;

/* Authors: Kristiyan Doykov - Original Author
 *          Joel Bell-Wilding - Changes made to improve display time performance
 * Since:   13/12/19
 * Version: 1.1
 * Purpose: Handles the retrieval of information for and rendering of a custom information
 *          window on the Map Activity when a marker is tapped
 */
public class MapMarkerWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private final String TAG = "MapMarkerWindowAdapter"; // Tag used for logging status of application

    private final View window; // The window view itself
    private Context context; // The context in which the info window is being created
    private Map<Marker, String> markerThumbnailMap; // Map of markers to their corresponding thumbnailURLs
    private Map<Marker, Drawable> thumbnails = new HashMap<>(); // Caches the location thumbnails once loaded
    private Map<Marker, Target<Drawable>> targets = new HashMap<>(); // Caches the target (ImageView) for thumbnails
    private static final int THUMBNAIL_DIMENSION = 520; // The height and width of the thumbnail

    // Constructor initialises necessary attributes
    public MapMarkerWindowAdapter(Context context, Map<Marker, String> markerThumbnailMap) {
        this.context = context;
        this.markerThumbnailMap = markerThumbnailMap;
        window = View.inflate(context, R.layout.custom_info_panel, null);
    }

    // Renders a window containing the location title and summary for the selected marker
    private void renderWindowText(final Marker marker, View view) {

        String title = marker.getTitle(); // Get marker title (location name)

        // Initialise UI layout references
        TextView textViewTitle = view.findViewById(R.id.title);

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

        // Handles displaying the max lines of text for the chosen font size
        if (Settings.getInstance().getFontSize() == R.style.FontStyle_Large) {
            textViewSnippet.setMaxLines(4 - textViewTitle.getLineCount());
        } else {
            textViewSnippet.setMaxLines(6 - textViewTitle.getLineCount());
        }
    }

    /* Following code taken and adapted from the following link:
     * https://github.com/bumptech/glide/issues/290#issuecomment-163421121
     * Ensures that a performance drop does not occur from recursive info
     * window display calls when retrieving and displaying a downloaded image */

    // Gets the cached target when provided with a marker, otherwise returns a new target
    private Target<Drawable> getTarget(Marker marker) {
        Target<Drawable> target = targets.get(marker);
        if (target == null) {
            target = new InfoTarget(marker);
            targets.put(marker, target);
        }
        return target;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        ImageView thumbnailView = window.findViewById(R.id.picture);
        // Set image to show loading message initially
        thumbnailView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_loading_message));

        Drawable thumbnail = thumbnails.get(marker); // Gets cached thumbnail from map
        // Check whether thumbnail has been loaded before
        if (thumbnail == null) {
            // If not cached, get thumbnail from web and place it into appropriate target
            Glide.with(context)
                    .load(markerThumbnailMap.get(marker))
                    .centerCrop()
                    .override(THUMBNAIL_DIMENSION, THUMBNAIL_DIMENSION)
                    .placeholder(R.drawable.ic_loading_message)
                    .error(R.drawable.ic_error_message)
                    .dontAnimate()
                    .into(getTarget(marker));
        } else {
            // If cached, set the thumbnail for this location
            thumbnailView.setImageDrawable(thumbnail);
        }
        renderWindowText(marker, window); // Handle display of text for window
        return window;
    }


    @Override
    public View getInfoContents(Marker marker) {
        return null; // Returns null to ensure view is inflated
    }

    // Custom target class to handle the placement of thumbnail drawables
    private class InfoTarget extends CustomTarget<Drawable> {

        Marker marker; // The marker corresponding to the target

        InfoTarget(Marker marker) {
            super(THUMBNAIL_DIMENSION, THUMBNAIL_DIMENSION);
            this.marker = marker;
        }

        @Override
        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
            // Prevents performance drop from recursive call since images is only loaded if null
            thumbnails.put(marker, resource);
            marker.showInfoWindow(); // Gets the new info window with updated image
        }

        @Override
        public void onLoadCleared(Drawable placeholder) {
            thumbnails.remove(marker); // Remove image once it is invalid
        }
    }
}