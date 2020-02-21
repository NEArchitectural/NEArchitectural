package com.nearchitectural.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.nearchitectural.R;
import com.nearchitectural.ui.activities.MapsActivity;

/* Author:  Kristiyan Doykov
 * Since:   TODO: Fill in date
 * Version: 1.0
 * purpose: TODO: Fill in purpose
 */
public class HelpFragment extends Fragment {

    public static final String TAG = "HelpFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_help,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MapsActivity parentActivity = (MapsActivity) this.getActivity();
        parentActivity.getNavigationView().getMenu().findItem(R.id.nav_help).setChecked(true);
        parentActivity.setActionBarTitle("Help");
    }
}
