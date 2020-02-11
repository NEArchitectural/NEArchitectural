package com.nearchitectural.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SortedList;

import com.nearchitectural.databinding.ListItemBinding;
import com.nearchitectural.ui.models.ListItemModel;

import java.util.Comparator;
import java.util.List;

/**author: Kristiyan Doykov
 * since: TODO: Fill in date
 * version: 1.0
 * purpose: TODO: Fill in purpose
 */
public class ListItemAdapter extends RecyclerView.Adapter<ListItemViewHolder> {

    private final SortedList<ListItemModel> mSortedList = new SortedList<>(ListItemModel.class, new SortedList.Callback<ListItemModel>() {

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
        public int compare(ListItemModel o1, ListItemModel o2) {
            return mComparator.compare(o1, o2);
        }

        @Override
        public void onChanged(int position, int count) {
            notifyItemRangeChanged(position, count);
        }

        @Override
        public boolean areContentsTheSame(ListItemModel oldItem, ListItemModel newItem) {
            return oldItem.equals(newItem);
        }

        @Override
        public boolean areItemsTheSame(ListItemModel item1, ListItemModel item2) {
            return item1.getLocationInfo().getId().equals(item2.getLocationInfo().getId());
        }
    });

    private LayoutInflater mInflater;
    private Comparator<ListItemModel> mComparator;

    public ListItemAdapter(Context context, Comparator<ListItemModel> comparator) {
        mInflater = LayoutInflater.from(context);
        mComparator = comparator;
    }

    public void add(ListItemModel model) {
        mSortedList.add(model);
    }

    public void remove(ListItemModel model) {
        mSortedList.remove(model);
    }

    public void add(List<ListItemModel> models) {
        mSortedList.addAll(models);
    }

    public void remove(List<ListItemModel> models) {
        mSortedList.beginBatchedUpdates();
        for (ListItemModel model : models) {
            mSortedList.remove(model);
        }
        mSortedList.endBatchedUpdates();
    }

    public void replaceAll(List<ListItemModel> models) {
        mSortedList.beginBatchedUpdates();
        for (int i = mSortedList.size() - 1; i >= 0; i--) {
            final ListItemModel model = mSortedList.get(i);
            if (!models.contains(model)) {
                mSortedList.remove(model);
            }
        }
        mSortedList.addAll(models);
        mSortedList.endBatchedUpdates();
    }

    @NonNull
    @Override
    public ListItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final ListItemBinding binding = ListItemBinding.inflate(mInflater, parent, false);
        return new ListItemViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ListItemViewHolder holder, int position) {
        final ListItemModel model = mSortedList.get(position);
        holder.performBind(model);
    }

    @Override
    public int getItemCount() {
        return mSortedList.size();
    }

}