package com.nearchitectural.ui.adapters;

import com.github.wrdlbrnft.sortedlistadapter.SortedListAdapter;
import com.nearchitectural.databinding.ListItemBinding;
import com.nearchitectural.ui.models.LocationModel;

/* Author:  Kristiyan Doykov
 * Since:   TODO: Fill in date
 * Version: 1.0
 * Purpose: Handles the displaying of the search results (i.e. a list of locations) to the UI
 *          using binding
 */
public class LocationSearchResultViewHolder extends SortedListAdapter.ViewHolder<LocationModel> {

    private final ListItemBinding mBinding;

    public LocationSearchResultViewHolder(ListItemBinding binding) {
        super(binding.getRoot());
        mBinding = binding;
    }

    @Override
    protected void performBind(LocationModel locationModel) {
        mBinding.setModel(locationModel);
    }
}
