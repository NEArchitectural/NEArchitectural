package com.nearchitectural.ui.models;

import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import com.github.wrdlbrnft.sortedlistadapter.SortedListAdapter;
import com.nearchitectural.GlideApp;
import com.nearchitectural.R;
import com.nearchitectural.utilities.models.TimeLine;

public class TimeLineModel implements SortedListAdapter.ViewModel {

    private final TimeLine TimeLineInfo;

    public TimeLineModel (TimeLine TimeLineInfo){

        this.TimeLineInfo = TimeLineInfo;

    }

    public TimeLine getTimeLineInfo() {
        return  TimeLineInfo;
    }

    public String getName() {
        return TimeLineInfo.getName();
    }
    public String getSummary() {
        return TimeLineInfo.getSummaryTimeLine();
    }

    public String getYearOpened() {
        return TimeLineInfo.getYearOpenedString();
    }

    public String getThumbnailURL() {
        return TimeLineInfo.getThumbnailURL();
    }
    @BindingAdapter({"thumbnail"})
    public static void loadImage(ImageView imageView, String imageURL) {
        GlideApp.with(imageView.getContext())
                .load(imageURL)
                .override(500, 500)
                .centerCrop()
                .error(R.drawable.ic_launcher_background)
                .placeholder(R.drawable.ic_launcher_background)
                .into(imageView);
    }

}
