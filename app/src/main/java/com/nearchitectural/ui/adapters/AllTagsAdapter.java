package com.nearchitectural.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nearchitectural.R;
import com.nearchitectural.utilities.TagID;

import java.util.List;

/* Author:  Joel Bell-Wilding
 * Since:   25/02/20
 * Version: 1.0
 * Purpose: Handles the displaying of all tags and their icons when user taps drop-down menu
 *          on Location page
 */
public class AllTagsAdapter extends BaseAdapter {

    List<TagID> activeTags; // List of active tags
    Context mContext; // Context from which adapter is to be used

    public AllTagsAdapter(Context context, List<TagID> activeTags) {
        this.mContext = context;
        this.activeTags = activeTags;
    }

    @Override
    public int getCount() {
        return activeTags.size();
    }

    @Override
    public Object getItem(int position) {
        return activeTags.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    /* Method implementation taken and adapted from the following link:
    https://stackoverflow.com/questions/37084792/how-do-add-images-next-to-text-items-in-listview-in-android
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View mView;

        // Initialise for the appropriate view
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from((mContext));
            mView = inflater.inflate(R.layout.tags_list_view, parent, false);
        } else {
            mView = convertView;
        }

        // Get text view for tag name and icon
        TextView mText = mView.findViewById(R.id.tag_name);

        // Set the text view to contain the tag display name and icon
        TagID tag = (TagID) getItem(position);
        mText.setText(tag.displayName);
        int iconID = mContext.getResources().getIdentifier(tag.iconName , "drawable", mContext.getPackageName());
        mText.setCompoundDrawablesWithIntrinsicBounds(iconID, 0, 0, 0);

        return mView;

    }
}
