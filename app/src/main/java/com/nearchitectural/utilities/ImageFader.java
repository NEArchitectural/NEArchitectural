package com.nearchitectural.utilities;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.nearchitectural.R;

import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/* Author:  Joel Bell-Wilding - Original author
 * Since:   29/03/20
 * Version: 1.1
 * Purpose: UI Utility used to create an animated fading slideshow from two ImageViews
 */
public class ImageFader {

    private Context context; // The context the ImageFader is to be used in
    private ImageView activeImage; // The ImageView currently being displayed
    private ImageView inactiveImage; // The ImageView currently being hidden
    private View overlapView; // Any view that must overlap (brought to front) of slideshow - null indicates no overlap
    private int position; // The position of the currently displayed image from the URL array.xml

    private Timer timer;

    public ImageFader(ImageView firstImageView, ImageView secondImageView, View overlapView, Context context) {
        activeImage = firstImageView;
        inactiveImage = secondImageView;
        this.overlapView = overlapView;
        this.context = context;
    }

    /* Cross-fade between two image views over a duration
     * idea taken and adapted from the following link:
     * https://stackoverflow.com/questions/38906818/best-way-to-cross-fade-imageviews */
    private void fade(ImageView firstImage, ImageView secondImage, int fadeDuration) {
        firstImage.animate().alpha(0).setDuration(fadeDuration);
        secondImage.animate().alpha(1f).setDuration(fadeDuration);
        secondImage.bringToFront();
        if (overlapView != null) {
            overlapView.bringToFront();
        }
    }

    // Handles animation, transitioning and displaying of images in the header over a set interval
    public void animateLocationSlideshow(final List<String> imageURLs, int displayDuration, final int fadeDuration) {

        // Get a random image URL to prevent same initial image being shown
        position = new Random().nextInt(imageURLs.size()-1);

        /* Handler and Runnable used to repeat changing images across intervals of time
         *  Following code taken and adapted from the following link:
         *  https://stackoverflow.com/questions/37905013/how-can-load-the-images-with-the-glide-in-imageswitcher */
        final Handler handler = new Handler();
        final Runnable changeImage = new Runnable() {
            @Override
            public void run() {
                if (context != null) {
                    Glide.with(context)
                            .load(imageURLs.get(position))
                            .error(R.drawable.launcher_icon)
                            .placeholder(R.drawable.ic_loading_message)
                            .centerCrop()
                            .addListener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    // If image load failed, increment position but don't show next image
                                    position++;
                                    // If position reaches end of list, reset
                                    if (position == imageURLs.size())
                                        position = 0;
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    // Increment position
                                    position++;
                                    // If position reaches end of list, reset
                                    if (position == imageURLs.size())
                                        position = 0;
                                    // Cross-fade images and swap active image
                                    fade(activeImage, inactiveImage, fadeDuration);
                                    ImageView temp = activeImage;
                                    activeImage = inactiveImage;
                                    inactiveImage = temp;
                                    return false;
                                }
                            })
                            .into(inactiveImage);
                }
            }
        };

        // Cancels the animating of a previously animated set of images
        if (timer != null) {
            timer.cancel();
        }

        // Sets a timer to cross-fade to new images from the list every displayDuration seconds
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(changeImage);
            }
        }, 0, displayDuration);
    }

    // Method used to cancel the current animation and prevent the timer from acting on a detached fragment
    public void finishAnimating() {
        if (timer != null)
            timer.cancel();
    }
}
