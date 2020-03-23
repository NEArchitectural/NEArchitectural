package com.nearchitectural.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.firebase.firestore.FirebaseFirestore;
import com.like.LikeButton;
import com.nearchitectural.GlideApp;
import com.nearchitectural.R;
import com.nearchitectural.databinding.FragmentLocationBinding;
import com.nearchitectural.ui.activities.MapsActivity;
import com.nearchitectural.ui.adapters.LocationSlideshowAdapter;
import com.nearchitectural.utilities.models.Location;
import com.nearchitectural.utilities.models.Report;

import java.util.List;


/* Author:  Kristiyan Doykov
 * Since:   10/12/19
 * Version: 1.0
 * Purpose: To visually represent the established timeline
 *          of the creation of each of the locations in the database.
 */
public class TimelineFragment extends Fragment {

    public static final String TAG = "TimelineFragment";

    private ImageView thumbnail;
    private TextView title;
    private FirebaseFirestore db; // Represents the database storing location information

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_timeline,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MapsActivity parentActivity = (MapsActivity) this.getActivity();
        parentActivity.getNavigationView().getMenu().findItem(R.id.nav_timeline).setChecked(true);
        parentActivity.setActionBarTitle("Timeline");

    }




}

