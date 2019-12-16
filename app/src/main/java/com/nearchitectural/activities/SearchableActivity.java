package com.nearchitectural.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.maps.model.LatLng;
import com.nearchitectural.CurrentCoordinates;
import com.nearchitectural.R;
import com.nearchitectural.fragments.OptionsDialogFragment;

public class SearchableActivity extends AppCompatActivity implements OptionsDialogFragment.OptionsDialogListener, SearchView.OnQueryTextListener {

    private boolean wheelchairAccess;
    private boolean childFriendly;
    private boolean cheapEntry;
    private boolean freeEntry;
    private LatLng currentLocation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Toolbar searchViewToolbar = findViewById(R.id.search_toolbar);

        setSupportActionBar(searchViewToolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);

        /* We will use this to calculate distance from each place to the device and check the range */
        this.currentLocation = CurrentCoordinates.getCoords();

        Intent intent = getIntent();
        String value = intent.getStringExtra("key"); //if it's a string you stored.


        handleIntent(getIntent());
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
//        searchView.setSearchableInfo(
//                searchManager.getSearchableInfo(getComponentName()));


        searchView.setIconifiedByDefault(false);


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

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }
}