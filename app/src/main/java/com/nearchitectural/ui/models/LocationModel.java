package com.nearchitectural.ui.models;

import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.github.wrdlbrnft.sortedlistadapter.SortedListAdapter;
import com.nearchitectural.GlideApp;
import com.nearchitectural.R;
import com.nearchitectural.utilities.Settings;
import com.nearchitectural.utilities.models.Location;


/* Author:  Kristiyan Doykov, Joel Bell-Wilding
 * Since:   10/12/19
 * Version: 1.1
 * Purpose: Internally uses a Location object to model location information to be adapted
 *          for a given layout
 */
public class LocationModel implements SortedListAdapter.ViewModel {

    private final Location locationInfo; // Location object containing all info for a given location
    private double distanceFromUser; // Distance from user's current location
    private String distanceStringForListItem; // String representation of distance from user
    private boolean isFirstInList = false; // Flags if model is the first in list (i.e. index 0)
    private boolean isLastInList = false; // Flags if model is the last in list (i.e. index n-1)
    private boolean oddIndex = false; // Flags if model has an odd index (i.e. index % 2 is false)

    public LocationModel(Location locationInfo, double distanceFromUser) {

        // Set the conversion rate to be used (for kilometers/miles) from settings
        int conversionRate = Settings.getInstance().getDistanceUnit().getConversionRate();
        this.locationInfo = locationInfo;
        this.distanceFromUser = distanceFromUser;
        int distance = (int) distanceFromUser /conversionRate;

        // If distance from user is less than 1 measure of the distance unit, show a smaller measure
        if (distance <= 0) {
            if (Settings.getInstance().getDistanceUnit() == Settings.DistanceUnit.KILOMETER) {
                // If kilometers, show distance in meters
                this.distanceStringForListItem = (int) distanceFromUser + " meters away";
            } else {
                // If miles, show distance as a decimal of a mile (i.e. 0.32 miles away)
                this.distanceStringForListItem = "0." + (int) (distanceFromUser/conversionRate*100) + " miles away";
            }
        } else {
            // Else show the measure and the distance unit
            String displayName = Settings.getInstance().getDistanceUnit().getDisplayName();
            if (distance == 1) {
                displayName = displayName.substring(0, displayName.length()-1);
            }
            this.distanceStringForListItem = distance + " " + displayName + " away";
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
        return locationInfo.getYearOpenedString();
    }

    public String getThumbnailURL() {
        return locationInfo.getThumbnailURL();
    }

    public long getLikes() {
        return locationInfo.getLikes();
    }

    // Getters for distance from user (double value and string representation)
    public double getMDistanceFromCurrentPos() {
        return distanceFromUser;
    }

    public String getDistanceStringForListItem() {
        return distanceStringForListItem;
    }

    // Getters and setters for isFirstInList and isLastInList booleans
    public boolean isFirstInList() {
        return isFirstInList;
    }

    public void setFirstInList(boolean firstInList) {
        isFirstInList = firstInList;
    }

    public boolean isLastInList() {
        return isLastInList;
    }

    public void setLastInList(boolean lastInList) {
        isLastInList = lastInList;
    }

    // Getter and setter for whether location model is odd number in list
    public boolean isOddIndex() {
        return oddIndex;
    }

    public void setOddIndex(boolean oddIndex) {
        this.oddIndex = oddIndex;
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
    @BindingAdapter({"thumbnailSquare"})
    public static void loadImageSquare(ImageView imageView, String imageURL) {

        GlideApp.with(imageView.getContext())
                .load(imageURL)
                .override(500, 500)
                .centerCrop()
                .error(R.drawable.ic_error_message)
                .placeholder(R.drawable.ic_loading_message)
                .transition(DrawableTransitionOptions.withCrossFade(600))
                .into(imageView);
    }

    // Loads thumbnail image associated with Location
    @BindingAdapter({"thumbnailCircle"})
    public static void loadImageCircle(ImageView imageView, String imageURL) {

        GlideApp.with(imageView.getContext())
                .load(imageURL)
                .override(500, 500)
                .circleCrop()
                .error(R.drawable.ic_error_message)
                .placeholder(R.drawable.ic_loading_message)
                .transition(DrawableTransitionOptions.withCrossFade(600))
                .into(imageView);
    }
}
