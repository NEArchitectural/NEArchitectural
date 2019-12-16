package com.nearchitectural.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.slider.Slider;
import com.nearchitectural.R;

import java.util.ArrayList;

public class SearchFragment extends Fragment {

    public static final String TAG = "SearchFragment";
    private RecyclerView places;
    private TextView seekbarProg;
    private Slider slider;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        /* Grab the list of places from container.getPlaces() and populate the ListView */
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ArrayList<String> placesText = new ArrayList<>();

        placesText.add("Hey");
        placesText.add("this");
        placesText.add("is");
        placesText.add("not");
        placesText.add("working");
//
//        ArrayAdapter<String> itemsAdapter =
//                 new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, placesText);
        places = (RecyclerView) this.getView().findViewById(R.id.places_list);

        seekbarProg = (TextView) this.getView().findViewById(R.id.seekbar_progress);

        slider = (Slider) this.getView().findViewById(R.id.slider);

        seekbarProg.setText("Distance: " + (int) slider.getValue() + "km");

        slider.setOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(Slider slider, float value) {
                seekbarProg.setText("Distance: " + (int) slider.getValue() + "km");
            }
        });
    }
}
