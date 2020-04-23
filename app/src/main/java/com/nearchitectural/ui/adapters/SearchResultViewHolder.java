package com.nearchitectural.ui.adapters;

import android.view.View;
import android.widget.TextView;

import com.github.wrdlbrnft.sortedlistadapter.SortedListAdapter;
import com.nearchitectural.R;
import com.nearchitectural.databinding.ListItemBinding;
import com.nearchitectural.ui.models.LocationModel;
import com.nearchitectural.utilities.Settings;

/* Authors: Dimo Dimchev - Original Author
 *          Kristiyan Doykov - Refinements for inclusion of font-size
 * Since:   13/12/19
 * Version: 1.1
 * Purpose: Handles the displaying of the search results (i.e. a list of locations) to the UI
 *          using binding
 */
public class SearchResultViewHolder extends SortedListAdapter.ViewHolder<LocationModel> {

    private final ListItemBinding mBinding;

    SearchResultViewHolder(ListItemBinding binding) {
        super(binding.getRoot());
        mBinding = binding;
    }

    @Override
    protected void performBind(final LocationModel locationModel) {
        mBinding.setLocation(locationModel);

        // Get layout elements
        final TextView summary = mBinding.listItemSummary;
        final TextView yearOpened = mBinding.listItemYearOpened;
        final TextView distanceToUser = mBinding.listItemDistance;
        int viewVisibility = View.GONE; // Used to alter visibility of layout elements

        // If location permissions are granted, display distance to user
        if (!Settings.getInstance().locationPermissionsAreGranted()) {
               distanceToUser.setVisibility(View.GONE);
               viewVisibility = View.VISIBLE;
        }

        // Handles the displaying/hiding of textviews on the location card to cater for font-size
        switch (Settings.getInstance().getFontSize()) {
            case R.style.FontStyle_Large:
                summary.setVisibility(viewVisibility);
                summary.setMaxLines(2);
                break;
            case R.style.FontStyle_Small:
                summary.setMaxLines(4);
            default:
                summary.setMaxLines(4);
                yearOpened.setVisibility(viewVisibility);
                break;
        }
    }
}
