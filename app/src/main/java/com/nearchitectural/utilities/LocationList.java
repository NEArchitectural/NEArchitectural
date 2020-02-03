package com.nearchitectural.utilities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/** @author Joel Bell-Wilding
 *  @version 1.0
 *  @since 28/01/20
 *  purpose: Store and manipulate a list of locations (both active and inactive) for displaying
 *  within application
 */
public class LocationList {

    private List<LocationInfo> activeLocations; // Locations to be displayed within the application
    private List<LocationInfo> inactiveLocations; // Locations to be hidden within the application
    private List<Tag> activeTags; // Currently active tags as chosen by the user
    private double distanceRadius; // Radius of distance within which locations should be shown
    // TODO: implement UserSettings class
    // private UserSettings userSettings;
    private UserLocation userLocation;

    // TODO: implement LocationList constructor with DatabaseManager class
    public LocationList(UserLocation userLocation) {
        this.userLocation = userLocation;
        filter();
        sort(new AlphabeticComparator());
    }

    // Returns the list containing currently active locations
    public List<LocationInfo> getActiveLocations() {
        return activeLocations;
    }

    // Sorts both the active and inactive list using the provided comparator
    public void sort(Comparator<LocationInfo> locationComparator) {
        Collections.sort(activeLocations, locationComparator);
        Collections.sort(inactiveLocations, locationComparator);
    }

    // Filters locations into active or inactive lists based on active tags, distance and user settings
    public void filter() {

        List<Tag> activeLocationTags; // Stores the union of active tags and a given locations tags

        // Swaps locations from inactive location list to active location list when appropriate
        for (LocationInfo l : inactiveLocations) {
            activeLocationTags = new ArrayList<>(l.getTags());
            activeLocationTags.retainAll(activeTags);
            // TODO: Add additional conditions for user settings
            if (!activeLocationTags.isEmpty() && userLocation.distanceFromUser(l) <= distanceRadius) {
                activeLocations.add(l);
                inactiveLocations.remove(l);
            }
        }

        // Swaps locations from active location list to inactive location list when appropriate
        for (LocationInfo l : activeLocations) {
            activeLocationTags = new ArrayList<>(l.getTags());
            activeLocationTags.retainAll(activeTags);
            // TODO: Add additional conditions for user settings
            if (activeLocationTags.isEmpty() || userLocation.distanceFromUser(l) > distanceRadius) {
                inactiveLocations.add(l);
                activeLocations.remove(l);
            }
        }
    }

    // Sets a new value for distance radius and re-filters locations
    public void changeRadius(double newRadius) {
        distanceRadius = newRadius;
        filter();
    }

    // Adds a tag to active tags and re-filters locations
    public void addTag(Tag newTag) {
        activeTags.add(newTag);
        filter();
    }

    // Removes a tag from active tags and re-filters locations (otherwise returns false if tag not present in active tags)
    public boolean removeTag(Tag tagToRemove) {
        if (activeTags.remove(tagToRemove)) {
            filter();
            return true;
        }
        return false;
    }
}
