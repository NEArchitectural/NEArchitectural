package com.nearchitectural.ui.models;

import com.github.wrdlbrnft.sortedlistadapter.SortedListAdapter;

public class ListItemModel implements SortedListAdapter.ViewModel {
    /* Set this to the id from the db */
    private final long mId;
    private final String mTitle;
    private final String mPlaceType;

    public ListItemModel(long id, String title, String placeType) {
        this.mId = id;
        this.mTitle = title;
        this.mPlaceType = placeType;
    }

    public long getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getPlaceType() {
        return mPlaceType;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ListItemModel model = (ListItemModel) o;

        if (mId != model.mId) return false;
        return mPlaceType != null ? mPlaceType.equals(model.getPlaceType()) : model.getPlaceType() == null
                && mTitle != null ? mTitle.equals(model.getTitle()) : model.getTitle() == null;

    }

    @Override
    public int hashCode() {
        int result = (int) (mId ^ (mId >>> 32));
        result = 31 * result + (mTitle != null ? mTitle.hashCode() : 0) +
                (mPlaceType != null ? mPlaceType.hashCode() : 0);
        return result;
    }


}
