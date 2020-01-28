package com.nearchitectural;

import com.github.wrdlbrnft.sortedlistadapter.SortedListAdapter;
import com.nearchitectural.databinding.ListItemBinding;
import com.nearchitectural.ui.models.ListItemModel;

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
