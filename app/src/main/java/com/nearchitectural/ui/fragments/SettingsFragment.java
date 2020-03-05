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
 * Since:   10/12/19
 * Version: 1.0
 * Purpose: To display an interface, which enables the user
 *          to set app-wide settings such as font size.
 */
public class SettingsFragment extends Fragment {

    public static final String TAG = "SettingsFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MapsActivity parentActivity = (MapsActivity) this.getActivity();
        parentActivity.getNavigationView().getMenu().findItem(R.id.nav_settings).setChecked(true);
        parentActivity.setActionBarTitle("Settings");
    }
}
