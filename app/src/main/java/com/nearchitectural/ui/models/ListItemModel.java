package com.nearchitectural.ui.models;

import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import com.github.wrdlbrnft.sortedlistadapter.SortedListAdapter;
import com.nearchitectural.GlideApp;
import com.nearchitectural.R;

public class ListItemModel implements SortedListAdapter.ViewModel {
    /* After each entry in the db contains all the
     necessary fields they can all be added here,
      but for now only these are being used */
    private final String mId;
    private final String mTitle;
    private final String mLocationType;
    private final boolean mIsWheelChairAccessible;
    private final boolean mIsChildFriendly;
    private final boolean mHasCheapEntry;
    private final boolean mHasFreeEntry;
    private String thumbnailURL;
    // No setter for this field as it will necessarily be provided in the constructor
    private double mDistanceFromCurrentPosInMeters;

    public ListItemModel(String mId, String mTitle, String mLocationType, boolean mIsWheelChairAccessible,
                         boolean mIsChildFriendly, boolean mHasCheapEntry, boolean mHasFreeEntry,
                         String thumbnailURL, double mDistanceFromCurrentPosInMeters) {
        this.mId = mId;
        this.mTitle = mTitle;
        this.mLocationType = mLocationType;
        this.mIsWheelChairAccessible = mIsWheelChairAccessible;
        this.mIsChildFriendly = mIsChildFriendly;
        this.mHasCheapEntry = mHasCheapEntry;
        this.mHasFreeEntry = mHasFreeEntry;
        this.thumbnailURL = thumbnailURL;
        this.mDistanceFromCurrentPosInMeters = mDistanceFromCurrentPosInMeters;
    }

    public String getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getLocationType() {
        return mLocationType;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ListItemModel model = (ListItemModel) o;

        if (!mId.equals(model.mId)) return false;
        return mLocationType != null ? mLocationType.equals(model.getLocationType()) : model.getLocationType() == null
                && mTitle != null ? mTitle.equals(model.getTitle()) : model.getTitle() == null;

    }

    @Override
    public int hashCode() {
        int result = (mId.hashCode());
        result = 31 * result + (mTitle != null ? mTitle.hashCode() : 0) +
                (mLocationType != null ? mLocationType.hashCode() : 0);
        return result;
    }


    public double getmDistanceFromCurrentPosInMeters() {
        return mDistanceFromCurrentPosInMeters;
    }

    public boolean mIsWheelChairAccessible() {
        return mIsWheelChairAccessible;
    }

    public boolean mIsChildFriendly() {
        return mIsChildFriendly;
    }

    public boolean mHasCheapEntry() {
        return mHasCheapEntry;
    }

    public boolean mHasFreeEntry() {
        return mHasFreeEntry;
    }

    public String getThumbnailURL() {
        return thumbnailURL;
    }

    public void setThumbnailURL(String thumbnailURL) {
        this.thumbnailURL = thumbnailURL;
    }

    @BindingAdapter({"thumbnail"})
    public static void loadImage(ImageView imageView, String imageURL) {
        GlideApp.with(imageView.getContext())
                .load(imageURL)
                .error(R.drawable.ic_launcher_background)
                .fitCenter()
                .into(imageView);

    }
}
