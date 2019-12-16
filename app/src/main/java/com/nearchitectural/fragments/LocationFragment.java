package com.nearchitectural.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.nearchitectural.R;

public class LocationFragment extends Fragment {
    public static final String TAG = "LocationFragment";
    private TextView title;
    private Bundle arguments;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        arguments = getArguments();
        /* Set values like name of the place, details, images and such passed from the activity
         * by using the following syntex */
//        String strtext = getArguments().getString("edttext");

        return inflater.inflate(R.layout.fragment_location, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        title = (TextView) view.findViewById(R.id.title);

        title.setText(arguments.getString("placeName") + " details");

//        super.onViewCreated(view, savedInstanceState);
    }
}
