package com.nearchitectural.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SortedList;

import com.nearchitectural.databinding.TimelineItemBinding;
import com.nearchitectural.ui.fragments.TimelineFragment;
import com.nearchitectural.ui.models.LocationModel;
import com.nearchitectural.ui.models.TimelineModel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/* Author:  James Allwood-Panter - Original Author
 * Since:   11/04/20
 * Version: 1.0
 * Purpose: Handles operations for the timeline results recycler (i.e. a list of locations)
 *          for the timeline fragment
 */
public class TimelineItemAdapter extends RecyclerView.Adapter<TimelineItemViewHolder> {

    // Sorted list which ensures all locations are sorted by a provided comparator
    private final SortedList<TimelineModel> mSortedList = new SortedList<>(TimelineModel.class, new SortedList.Callback<TimelineModel>() {

        @Override
        public void onInserted(int position, int count) {
            notifyItemRangeInserted(position, count);
        }

        @Override
        public void onRemoved(int position, int count) {
            notifyItemRangeRemoved(position, count);
        }

        @Override
        public void onMoved(int fromPosition, int toPosition) {
            notifyItemMoved(fromPosition, toPosition);
        }

        @Override
        public int compare(TimelineModel o1, TimelineModel o2) {
            return mComparator.compare(o1, o2);
        }

        @Override
        public void onChanged(int position, int count) {
            notifyItemRangeChanged(position, count);
        }

        @Override
        public boolean areContentsTheSame(TimelineModel oldItem, TimelineModel newItem) {
            return oldItem.equals(newItem);
        }

        @Override
        public boolean areItemsTheSame(TimelineModel item1, TimelineModel item2) {
            return item1.getLocationInfo().getId().equals(item2.getLocationInfo().getId());
        }
    });

    private LayoutInflater mInflater; // Handles inflating the search results to the UI
    private Comparator<LocationModel> mComparator; // Comparator used to sort the location models
    private View.OnClickListener displayTimelineInfoListener; // Listener to open location page when item is clicked

    public TimelineItemAdapter(final TimelineFragment timelineFragment, Comparator<LocationModel> comparator) {
        this.mInflater = LayoutInflater.from(timelineFragment.getActivity());
        this.mComparator = comparator;
        // Listener is instantiated in constructor to enabled better performance when binding
        displayTimelineInfoListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timelineFragment.getReportInfoAndDisplay(v.getTag().toString());
            }
        };
        setHasStableIds(true);
    }

    // Changes the comparator being used by the sorted list and resorts the list according to comparator
    public void changeComparator(Comparator<LocationModel> newComparator) {
        mComparator = newComparator;
        List<TimelineModel> locations = new ArrayList<>();
        for (int i = 0; i < mSortedList.size(); i++) {
            locations.add(mSortedList.get(i));
        }
        mSortedList.clear();
        mSortedList.addAll(locations);
    }

    // Adds model to sorted list
    public void add(TimelineModel model) {
        mSortedList.add(model);
    }

    // Removes model from sorted list
    public void remove(TimelineModel model) {
        mSortedList.remove(model);
    }

    // Adds multiple models to sorted list
    public void add(List<TimelineModel> models) {
        mSortedList.addAll(models);
    }

    // Removes multiple models from sorted list
    public void remove(List<TimelineModel> models) {
        mSortedList.beginBatchedUpdates();
        for (TimelineModel model : models) {
            mSortedList.remove(model);
        }
        mSortedList.endBatchedUpdates();
    }

    // Replaces all models in list with new model
    public void replaceAll(List<TimelineModel> models) {
        mSortedList.beginBatchedUpdates();
        for (int i = mSortedList.size() - 1; i >= 0; i--) {
            final TimelineModel model = mSortedList.get(i);
            if (!models.contains(model)) {
                mSortedList.remove(model);
            }
        }
        mSortedList.addAll(models);
        mSortedList.endBatchedUpdates();
    }

    @NonNull
    @Override
    public TimelineItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final TimelineItemBinding binding = TimelineItemBinding.inflate(mInflater, parent, false);
        return new TimelineItemViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(TimelineItemViewHolder holder, int position) {
        final TimelineModel model = mSortedList.get(position);
        model.setOddIndex((position % 2) == 0); // Determines if model is at an odd index in list
        model.setFirstInList(position == 0); // Determines if model is first in list
        model.setLastInList(position == mSortedList.size()-1); // Determines if model is last in list
        holder.performBind(model);
    }

    @Override
    public int getItemCount() {
        return mSortedList.size();
    }

    @Override
    public void onViewAttachedToWindow(@NonNull TimelineItemViewHolder holder) {
        // Set listener to open location page for the selected location
        holder.itemView.setOnClickListener(displayTimelineInfoListener);
        super.onViewAttachedToWindow(holder);
    }

    @Override
    public long getItemId(int position) {
        return mSortedList.get(position).hashCode();
    }
}
