package com.nearchitectural.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;

import com.nearchitectural.R;
import com.nearchitectural.utilities.models.HelpGuide;

/* Author:  Joel Bell-Wilding
 * Since:   10/04/20
 * Version: 1.0
 * Purpose: Handles the adaptation of a HelpGuide to the help screen ViewPager to show
 *          all guide text and images one after another. Outline of class taken and
 *          adapted from: https://androidclarified.com/viewpager-example-sliding-images/
 */
public class HelpGuidePagerAdapter extends PagerAdapter {

    private Context context; // The context through which the adapter is used
    private HelpGuide helpGuide; // The help guide to be adapted

    public HelpGuidePagerAdapter(HelpGuide helpGuide, Context context) {
        this.helpGuide = helpGuide;
        this.context = context;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        // Get the view
        View view = LayoutInflater.from(context).inflate(R.layout.help_guide_page, container, false);
        // Retrieve the current page image and text
        ImageView currentImage = view.findViewById(R.id.guide_image);
        TextView currentText = view.findViewById(R.id.guide_text);
        // Set the current page image and text
        currentImage.setImageDrawable(context.getResources().getDrawable(helpGuide.getSingleImageID(position)));
        currentText.setText(helpGuide.getSingleText(position));
        container.addView(view);
        return view;
    }

    @Override
    public int getCount() {
        return helpGuide.getGuideLength();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return helpGuide.getSingleText(position);
    }
}
