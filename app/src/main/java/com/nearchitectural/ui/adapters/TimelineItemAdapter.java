package com.nearchitectural.ui.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SortedList;

import com.nearchitectural.databinding.TimelineItemBinding;
import com.nearchitectural.ui.activities.MapsActivity;
import com.nearchitectural.ui.fragments.LocationFragment;
import com.nearchitectural.ui.models.LocationModel;

import java.util.Comparator;
import java.util.List;

/* Author:  Taylor Stubbs
 * Since:   02/04/20
 * Version: 1.0
 * Purpose: Handles operations for the timeline results recycler (i.e. a list of locations)
 *          for the timeline fragment
 */
public class TimelineItemAdapter extends RecyclerView.Adapter<TimelineItemViewHolder> {

    // Sorted list which ensures all locations are sorted by a provided comparator
    private final SortedList<LocationModel> mSortedList = new SortedList<>(LocationModel.class, new SortedList.Callback<LocationModel>() {

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
        public int compare(LocationModel o1, LocationModel o2) {
            return mComparator.compare(o1, o2);
        }

        @Override
        public void onChanged(int position, int count) {
            notifyItemRangeChanged(position, count);
        }

        @Override
        public boolean areContentsTheSame(LocationModel oldItem, LocationModel newItem) {
            return oldItem.equals(newItem);
        }

        @Override
        public boolean areItemsTheSame(LocationModel item1, LocationModel item2) {
            return item1.getLocationInfo().getId().equals(item2.getLocationInfo().getId());
        }
    });

    private LayoutInflater mInflater; // Handles inflating the search results to the UI
    private Comparator<LocationModel> mComparator; // Comparator used to sort the location models
    private Activity parentActivity; // Used to open location fragment when timeline item is pressed
    private View.OnClickListener openLocationListener; // Listener to open location page when item is clicked

    public TimelineItemAdapter(Activity parentActivity, Comparator<LocationModel> comparator) {
        this.parentActivity = parentActivity;
        this.mInflater = LayoutInflater.from(parentActivity);
        this.mComparator = comparator;
        // Listener is instantiated in constructor to enabled better performance when binding
        openLocationListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLocationPage(v);
            }
        };
        setHasStableIds(true);
    }

    // Adds model to sorted list
    public void add(LocationModel model) {
        mSortedList.add(model);
    }

    // Removes model from sorted list
    public void remove(LocationModel model) {
        mSortedList.remove(model);
    }

    // Adds multiple models to sorted list
    public void add(List<LocationModel> models) {
        mSortedList.addAll(models);
    }

    // Removes multiple models from sorted list
    public void remove(List<LocationModel> models) {
        mSortedList.beginBatchedUpdates();
        for (LocationModel model : models) {
            mSortedList.remove(model);
        }
        mSortedList.endBatchedUpdates();
    }

    // Replaces all models in list with new model
    public void replaceAll(List<LocationModel> models) {
        mSortedList.beginBatchedUpdates();
        for (int i = mSortedList.size() - 1; i >= 0; i--) {
            final LocationModel model = mSortedList.get(i);
            if (!models.contains(model)) {
                mSortedList.remove(model);
            }
        }
        mSortedList.addAll(models);
        mSortedList.endBatchedUpdates();
    }

    // Handle a location card being pressed and take the user to the according Location page
    private void openLocationPage(View view) {
        // Creates a new location fragment and opens it using the location ID
        LocationFragment lf = LocationFragment.newInstance(view.getTag().toString());
        ((MapsActivity) parentActivity).openFragment(lf, true);
    }

    @NonNull
    @Override
    public TimelineItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final TimelineItemBinding binding = TimelineItemBinding.inflate(mInflater, parent, false);
        return new TimelineItemViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(TimelineItemViewHolder holder, int position) {
        final LocationModel model = mSortedList.get(position);
        model.setOddIndex((position % 2) == 0); // Determines if model is at an odd index in list
        model.setFirstInList(position == 0); // Determines if model is first in list
        model.setLastInList(position == mSortedList.size()-1); // Determines if model is last in list
        // Set listener to open location page for the selected location
        holder.itemView.setOnClickListener(openLocationListener);
        holder.performBind(model);
    }

    @Override
    public int getItemCount() {
        return mSortedList.size();
    }

    @Override
    public void onViewAttachedToWindow(@NonNull TimelineItemViewHolder holder) {
        super.onViewAttachedToWindow(holder);
    }

    @Override
    public long getItemId(int position) {
        return mSortedList.get(position).hashCode();
    }
}
