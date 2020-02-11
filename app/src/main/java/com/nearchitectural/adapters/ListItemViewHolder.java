package com.nearchitectural.adapters;

import com.github.wrdlbrnft.sortedlistadapter.SortedListAdapter;
import com.nearchitectural.databinding.ListItemBinding;
import com.nearchitectural.ui.models.ListItemModel;

/**author: Kristiyan Doykov
 * since: TODO: Fill in date
 * version: 1.0
 * purpose: TODO: Fill in purpose
 */
public class ListItemViewHolder extends SortedListAdapter.ViewHolder<ListItemModel> {

    private final ListItemBinding mBinding;

    public ListItemViewHolder(ListItemBinding binding) {
        super(binding.getRoot());
        mBinding = binding;
    }

    @Override
    protected void performBind(ListItemModel listItemModel) {
        mBinding.setModel(listItemModel);
    }
}
