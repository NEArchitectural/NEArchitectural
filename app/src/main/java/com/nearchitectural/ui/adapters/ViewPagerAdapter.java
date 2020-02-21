package com.nearchitectural.ui.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.nearchitectural.GlideApp;
import com.nearchitectural.R;

import java.util.ArrayList;

/* Author:  Kristiyan Doykov
 * Since:   TODO: Fill in date
 * Version: 1.0
 * Purpose: TODO: Fill in purpose
 */
public class ViewPagerAdapter extends PagerAdapter {

    private Context context;
    private ArrayList<String> imageURLs;

    public ViewPagerAdapter(Context context, ArrayList<String> imageURLs) {
        this.context = context;
        this.imageURLs = imageURLs;
    }

    @Override
    public int getCount() {
        return imageURLs.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        ImageView imageView = new ImageView(context);

        GlideApp.with(context)
                .load(imageURLs.get(position))
                .override(400, 400)
                .placeholder(R.drawable.ic_launcher_background)
                .into(imageView);

        container.addView(imageView);

        return imageView;
    }


    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
