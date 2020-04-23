package com.nearchitectural.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nearchitectural.R;
import com.nearchitectural.ui.fragments.HelpFragment;
import com.nearchitectural.utilities.models.HelpGuide;

/* Author:  Alistair Gimlette - Original Author
 * Since:   10/04/20
 * Version: 1.0
 * Purpose: Adapts all HelpGuide enumerator objects into a RecyclerView menu on the UI
 */
public class HelpGuideMenuAdapter extends RecyclerView.Adapter<HelpGuideMenuAdapter.HelpGuideMenuItem> {

    private HelpFragment helpFragment; // The help page hosting the recyclerview menu

    // ViewHolder class mapping Guide Title and Guide Menu Image to the appropriate layout
    static class HelpGuideMenuItem extends RecyclerView.ViewHolder {

        private TextView guideTitle; // The title of the guide menu item
        private ImageView guideImage; // The menu image for the guide

        HelpGuideMenuItem(@NonNull View itemView) {
            super(itemView);
            guideTitle = itemView.findViewById(R.id.help_guide_title);
            guideImage = itemView.findViewById(R.id.help_guide_main_image);
        }
    }

    public HelpGuideMenuAdapter(HelpFragment parentFragment) {
        this.helpFragment = parentFragment;
    }

    @NonNull
    @Override
    public HelpGuideMenuItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View helpGuideMenuItemLayout = LayoutInflater.from(parent.getContext()).inflate(R.layout.help_guide_menu_item, parent, false);
        return new HelpGuideMenuItem(helpGuideMenuItemLayout);
    }

    @Override
    public void onBindViewHolder(@NonNull HelpGuideMenuItem holder, int position) {
        HelpGuide guide = HelpGuide.values()[position]; // Get guide from HelpGuide enumerator
        holder.guideTitle.setText(guide.getTitle()); // Set menu item title
        // Set menu item image
        holder.guideImage.setImageDrawable(helpFragment.getResources().getDrawable(guide.getSingleImageID(guide.getMenuImageIndex())));
        holder.itemView.setTag(guide); // Set tag to the guide enumerator object itself
        // On click listener to show the guide upon tapping of a menu item
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                helpFragment.displayGuide((HelpGuide) v.getTag());
            }
        });
    }

    @Override
    public int getItemCount() {
        return HelpGuide.values().length;
    }
}
