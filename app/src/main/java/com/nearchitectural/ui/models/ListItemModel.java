package com.nearchitectural.ui.models;

import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import com.github.wrdlbrnft.sortedlistadapter.SortedListAdapter;
import com.nearchitectural.GlideApp;
import com.nearchitectural.R;
import com.nearchitectural.utilities.models.Location;

/* Author:  Kristiyan Doykov
 * Since:   TODO: Fill in date
 * Version: 1.0
 * Purpose: Internally uses a Location object to model location information to be displayed visually
 */
public class ListItemModel implements SortedListAdapter.ViewModel {

    // Location object containing all info for a given location
    private final Location locationInfo;
    // No setter for this field as it will necessarily be provided in the constructor
    private double mDistanceFromCurrentPosInMeters; // Distance from user's current location
    private String distanceStringForListItem; // String representation of distance from user

    public ListItemModel(Location locationInfo, double mDistanceFromCurrentPosInMeters) {

        final int KILOMETER_CONVERSION = 1000;
        this.locationInfo = locationInfo;
        this.mDistanceFromCurrentPosInMeters = mDistanceFromCurrentPosInMeters;
        // Meters if small distance, kilometers if large distance
        if ((int) mDistanceFromCurrentPosInMeters / KILOMETER_CONVERSION <= 0) {
            this.distanceStringForListItem = (int) mDistanceFromCurrentPosInMeters + " meters away";
        } else {

            this.distanceStringForListItem = (int) mDistanceFromCurrentPosInMeters / KILOMETER_CONVERSION + " km away";
        }
    }

    // Getter for location information
    public Location getLocationInfo() {
        return locationInfo;
    }

    public String getId() {
        return locationInfo.getId();
    }

    public String getTitle() {
        return locationInfo.getName();
    }

    public String getLocationType() {
        return locationInfo.getType();
    }

    public String getSummary() {
        return locationInfo.getSummary();
    }

    // Getters for distance from user (double value and string representation)
    public double getMDistanceFromCurrentPosInMeters() {
        return mDistanceFromCurrentPosInMeters;
    }

    public void setMDistanceFromCurrentPosInMeters(double distanceFromCurrentPosInMeters) {
        this.mDistanceFromCurrentPosInMeters = distanceFromCurrentPosInMeters;
    }

    public String getThumbnailURL() {
        return locationInfo.getThumbnailURL();
    }

    public String getDistanceStringForListItem() {
        return distanceStringForListItem;
    }

    public long getLikes() {
        return locationInfo.getLikes();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ListItemModel model = (ListItemModel) o;

        return locationInfo.equals(o) && distanceStringForListItem.equals(model.distanceStringForListItem);
    }

    @Override
    public int hashCode() {
        int result = locationInfo.hashCode();
        result = 31 * result + (distanceStringForListItem != null ? distanceStringForListItem.hashCode() : 0);
        return result;
    }

    // Loads image associated with Location List Item Model
    @BindingAdapter({"thumbnail"})
    public static void loadImage(ImageView imageView, String imageURL) {
        GlideApp.with(imageView.getContext())
                .load(imageURL)
                .override(500, 500)
                .error(R.drawable.ic_launcher_background)
                .placeholder(R.drawable.ic_launcher_background)
                .into(imageView);
    }
}
