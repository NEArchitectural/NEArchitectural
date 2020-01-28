package com.nearchitectural.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.slider.Slider;
import com.nearchitectural.CurrentCoordinates;
import com.nearchitectural.ListItemAdapter;
import com.nearchitectural.Place;
import com.nearchitectural.R;
import com.nearchitectural.databinding.ActivitySearchBinding;
import com.nearchitectural.databinding.ListItemBinding;
import com.nearchitectural.fragments.OptionsDialogFragment;
import com.nearchitectural.ui.models.ListItemModel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SearchableActivity extends AppCompatActivity implements OptionsDialogFragment.OptionsDialogListener {

    private boolean wheelchairAccess;
    private boolean childFriendly;
    private boolean cheapEntry;
    private boolean freeEntry;
    private LatLng currentLocation;
    public static final String TAG = "SearchableActivity";

    private RecyclerView places;
    private ActivitySearchBinding searchBinding;
    private TextView seekbarProg;
    private Slider slider;
    private List<ListItemModel> mModels;
    private ListItemBinding mBinding;
    private ListItemAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private static final Comparator<ListItemModel> ALPHABETICAL_COMPARATOR = new Comparator<ListItemModel>() {
        @Override
        public int compare(ListItemModel a, ListItemModel b) {
            return a.getTitle().compareTo(b.getTitle());
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        searchBinding = DataBindingUtil.setContentView(this, R.layout.activity_search);
//        setContentView(R.layout.activity_search);

        places = (RecyclerView) searchBinding.placesList;
//        places = (RecyclerView) this.findViewById(R.id.places_list);

//        seekbarProg = (TextView) this.findViewById(R.id.seekbar_progress);
        seekbarProg = (TextView) searchBinding.seekbarProgress;

//        slider = (Slider) this.findViewById(R.id.slider);
        slider = (Slider) searchBinding.slider;

        mAdapter = new ListItemAdapter(this, ALPHABETICAL_COMPARATOR);

        places.setAdapter(mAdapter);

        seekbarProg.setText("Distance: " + (int) slider.getValue() + "km");
        seekbarProg.setTextColor(getResources().getColor(R.color.colorPrimaryDark));

        slider.setOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(Slider slider, float value) {
                seekbarProg.setText("Distance: " + (int) slider.getValue() + "km");
            }
        });


//        mBinding = DataBindingUtil.inflate(, R.layout.list_item, viewGroup, false);
//        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_search);


        List<Place> placesToShow = new ArrayList<>();
        placesToShow.add(new Place((long) (Math.random() * 100 * 32), "First place", "Castle"));
        placesToShow.add(new Place((long) (Math.random() * 100 * 32), "Second place", "Castle"));
        placesToShow.add(new Place((long) (Math.random() * 100 * 32), "Third place", "Museum"));
        placesToShow.add(new Place((long) (Math.random() * 100 * 32), "A fortress", "Fortress"));
        placesToShow.add(new Place((long) (Math.random() * 100 * 32), "Gallery", "Art"));

        mModels = new ArrayList<>();
//        for (String place : placesNames)
        for (Place place : placesToShow) {
            /* This Math.random call is gonna be replaced by the db id of each place */
            mModels.add(new ListItemModel(place.getId(), place.getName(), place.getPlaceType()));
        }
        mAdapter.add(mModels);

//        Toolbar searchViewToolbar = findViewById(R.id.search_toolbar);
        Toolbar searchViewToolbar = searchBinding.searchToolbar;

        setSupportActionBar(searchViewToolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);

        /* We will use this to calculate distance from each place to the device and check the range */
        this.currentLocation = CurrentCoordinates.getCoords();

        Intent intent = getIntent();
        String value = intent.getStringExtra("key"); //if it's a string you stored.


        handleIntent(getIntent());
    }

    @Override
    public void onPostCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
//        super.onPostCreate(savedInstanceState, persistentState);
        /* The recycler view for our layout */


    }

    /**
     * Calculate distance between two points in latitude and longitude taking
     * into account height difference. If you are not interested in height
     * difference pass 0.0. Uses Haversine method as its base.
     * <p>
     * lat1, lon1 Start point lat2, lon2 End point el1 Start altitude in meters
     * el2 End altitude in meters
     *
     * @returns Distance in Meters
     */
    public static double calculateDistance(double lat1, double lat2, double lon1,
                                           double lon2, double el1, double el2) {

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        double height = el1 - el2;

        distance = Math.pow(distance, 2) + Math.pow(height, 2);

        return Math.sqrt(distance);
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            //use the query to search your data somehow
            attemptSearch(query);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);

        // Associate searchable configuration with the SearchView
        MenuItem searchItem = menu.findItem(R.id.search_item);
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) searchItem.getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
            public boolean onQueryTextChange(String newText) {
                final List<ListItemModel> filteredModelList = filter(mModels, newText);
                mAdapter.replaceAll(filteredModelList);
                places.scrollToPosition(0);
                return true;
            }

            public boolean onQueryTextSubmit(String query) {
                // **Here you can get the value "query" which is entered in the search box.**
                return false;
            }
        };
        searchView.setOnQueryTextListener(queryTextListener);


        return true;
    }


    /**
     * Performs a search and passes the results to the container
     * Activity that holds your Fragments.
     */
    public void attemptSearch(String query) {
        // TODO: implement this
    }


    public void openMaps(View view) {
        Intent myIntent = new Intent(SearchableActivity.this, MapsActivity.class);
        myIntent.putExtra("key", "yolo"); //Optional parameters
        SearchableActivity.this.startActivity(myIntent);
    }

    public void openOptions(View view) {
        // Create an instance of the dialog fragment and show it
        /* Get the values for each filter and send them to the popup as an argument (order matters) */
        DialogFragment dialog = new OptionsDialogFragment(this.cheapEntry, this.freeEntry);
        dialog.show(getSupportFragmentManager(), "OptionsDialogFragment");
    }

    @Override
    public void onDialogPositiveClick(Bundle bundle) {

    }

    @Override
    public void onDialogNegativeClick(Bundle bundle) {

    }

    public void setWheelchairAccess(boolean wheelchairAccess) {
        this.wheelchairAccess = wheelchairAccess;
    }

    public void setChildFriendly(boolean childFriendly) {
        this.childFriendly = childFriendly;
    }

    public void setCheapEntry(boolean cheapEntry) {
        this.cheapEntry = cheapEntry;
    }

    public void setFreeEntry(boolean freeEntry) {
        this.freeEntry = freeEntry;
    }

    public void setChildFriendly(View view) {
        AppCompatCheckBox cb = (AppCompatCheckBox) view;

        if (cb.isChecked()) {
            setChildFriendly(true);
            return;
        }
        this.setChildFriendly(false);
    }

    public void setAccessible(View view) {
        AppCompatCheckBox cb = (AppCompatCheckBox) view;

        if (cb.isChecked()) {
            setWheelchairAccess(true);
            return;
        }
        this.setWheelchairAccess(false);
    }


    private static List<ListItemModel> filter(List<ListItemModel> models, String query) {
        final String lowerCaseQuery = query.toLowerCase();

        final List<ListItemModel> filteredModelList = new ArrayList<>();
        for (ListItemModel model : models) {
            final String titleText = model.getTitle().toLowerCase();
            final String placeTypeText = model.getPlaceType().toLowerCase();
            if (titleText.contains(lowerCaseQuery) || placeTypeText.contains(lowerCaseQuery)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }

}