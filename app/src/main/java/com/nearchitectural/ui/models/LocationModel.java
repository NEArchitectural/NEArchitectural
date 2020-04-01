package com.nearchitectural.ui.models;

import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import com.github.wrdlbrnft.sortedlistadapter.SortedListAdapter;
import com.nearchitectural.GlideApp;
import com.nearchitectural.R;
import com.nearchitectural.utilities.Settings;
import com.nearchitectural.utilities.models.Location;


/* Author:  Kristiyan Doykov, Joel Bell-Wilding
 * Since:   10/12/19
 * Version: 1.0
 * Purpose: Internally uses a Location object to model location information to be adapted
 *          for a given layout
 */
public class LocationModel implements SortedListAdapter.ViewModel {

    // Location object containing all info for a given location
    private final Location locationInfo;
    // No setter for this field as it will necessarily be provided in the constructor
    private double mDistanceFromCurrentPos; // Distance from user's current location
    private String distanceStringForListItem; // String representation of distance from user

    public LocationModel(Location locationInfo, double mDistanceFromCurrentPos) {

        // Set the conversion rate to be used (for kilometers/miles) from settings
        int conversionRate = Settings.getInstance().getDistanceUnit().getConversionRate();
        this.locationInfo = locationInfo;
        this.mDistanceFromCurrentPos = mDistanceFromCurrentPos;
        int distance = (int) mDistanceFromCurrentPos/conversionRate;

        // If distance from user is less than 1 measure of the distance unit, show a smaller measure
        if (distance <= 0) {
            if (Settings.getInstance().getDistanceUnit() == Settings.DistanceUnit.KILOMETER) {
                // If kilometers, show distance in meters
                this.distanceStringForListItem = (int) mDistanceFromCurrentPos + " meters away";
            } else {
                // If miles, show distance as a decimal of a mile (i.e. 0.32 miles away)
                this.distanceStringForListItem = "0." + distance*100 + " miles away";
            }
        } else {
            // Else show the measure and the distance unit
            this.distanceStringForListItem = distance + " " + Settings.getInstance().getDistanceUnit().getDisplayName() + " away";
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

    public String getYearOpenedString() {
        return "Opened: " + locationInfo.getYearOpenedString();
    }

    public String getThumbnailURL() {
        return locationInfo.getThumbnailURL();
    }

    public long getLikes() {
        return locationInfo.getLikes();
    }

    // Getters for distance from user (double value and string representation)
    public double getMDistanceFromCurrentPos() {
        return mDistanceFromCurrentPos;
    }

    public String getDistanceStringForListItem() {
        return distanceStringForListItem;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LocationModel model = (LocationModel) o;

        return locationInfo.equals(o) && distanceStringForListItem.equals(model.distanceStringForListItem);
    }

    @Override
    public int hashCode() {
        int result = locationInfo.hashCode();
        result = 31 * result + (distanceStringForListItem != null ? distanceStringForListItem.hashCode() : 0);
        return result;
    }

    // Loads thumbnail image associated with Location
    @BindingAdapter({"thumbnail"})
    public static void loadImage(ImageView imageView, String imageURL) {

        GlideApp.with(imageView.getContext())
                .load(imageURL)
                .override(500, 500)
                .centerCrop()
                .error(R.mipmap.ic_launcher_round)
                .placeholder(R.mipmap.ic_launcher_round)
                .into(imageView);
    }
}
