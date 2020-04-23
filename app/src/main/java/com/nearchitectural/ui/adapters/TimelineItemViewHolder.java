package com.nearchitectural.ui.adapters;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.wrdlbrnft.sortedlistadapter.SortedListAdapter;
import com.nearchitectural.R;
import com.nearchitectural.databinding.TimelineItemBinding;
import com.nearchitectural.ui.models.TimelineModel;
import com.nearchitectural.utilities.Settings;

/* Author:  Taylor Stubbs - Original Author
 * Since:   11/04/20
 * Version: 1.1
 * Purpose: Handles the displaying of the locations in the timeline (i.e. a list of locations)
 *          to the UI using data binding
 */
public class TimelineItemViewHolder extends SortedListAdapter.ViewHolder<TimelineModel> {

    private final TimelineItemBinding mBinding;

    TimelineItemViewHolder(TimelineItemBinding binding) {
        super(binding.getRoot());
        mBinding = binding;
    }

    @Override
    protected void performBind(final TimelineModel model) {
        mBinding.setLocation(model);

        // If in landscape, alter timeline item layout to fit landscape layout
        if (model.isLandscapeLayout()) {
            ViewGroup.LayoutParams params;
            params = mBinding.timelineInfoBlockTop.cardView.getLayoutParams();
            params.height = ViewGroup.LayoutParams.MATCH_PARENT;
            params = mBinding.timelineImageBlockBottom.getRoot().getLayoutParams();
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        }

        handleText(mBinding.timelineInfoBlockTop.timelineSnippet);
        handleText(mBinding.timelineInfoBlockBottom.timelineSnippet);
    }

    // Handles the displaying/hiding of the location summary to cater for font-size
    private void handleText(TextView snippet) {

        switch (Settings.getInstance().getFontSize()) {
            case R.style.FontStyle_Large:
                snippet.setVisibility(View.GONE);
                break;
            case R.style.FontStyle_Small:
                snippet.setMaxLines(4);
            default:
                snippet.setMaxLines(3);
                break;
        }
    }
}
