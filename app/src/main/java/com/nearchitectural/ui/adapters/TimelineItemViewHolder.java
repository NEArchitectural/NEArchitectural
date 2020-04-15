package com.nearchitectural.ui.adapters;

import android.widget.TextView;

import com.github.wrdlbrnft.sortedlistadapter.SortedListAdapter;
import com.nearchitectural.R;
import com.nearchitectural.databinding.TimelineItemBinding;
import com.nearchitectural.ui.models.LocationModel;
import com.nearchitectural.utilities.Settings;

/* Author:  Taylor Stubbs
 * Since:   02/04/20
 * Version: 1.0
 * Purpose: Handles the displaying of the locations in the timeline (i.e. a list of locations)
 *          to the UI using binding
 */
public class TimelineItemViewHolder extends SortedListAdapter.ViewHolder<LocationModel> {

    private final TimelineItemBinding mBinding;

    TimelineItemViewHolder(TimelineItemBinding binding) {
        super(binding.getRoot());
        mBinding = binding;
    }

    @Override
    protected void performBind(final LocationModel locationModel) {
        mBinding.setLocation(locationModel);
        if (locationModel.isOddIndex()) {
            handleText(mBinding.timelineInfoBlockTop.timelineSnippet);
        } else {
            handleText(mBinding.timelineInfoBlockBottom.timelineSnippet);
        }
    }

    private void handleText(TextView snippet) {

        // Handles the displaying/hiding of textviews on the location card to cater for font-size
        switch (Settings.getInstance().getFontSize()) {
            case R.style.FontStyle_Large:
                snippet.setMaxLines(6);
                break;
            case R.style.FontStyle_Small:
                snippet.setMaxLines(4);
            default:
                snippet.setMaxLines(4);
                break;
        }
    }
}
