package com.nearchitectural.utilities.models;

import com.nearchitectural.R;

/* Author:  Alistair Gimlette, Joel Bell-Wilding
 * Since:   10/04/20
 * Version: 1.1
 * Purpose: Enumerator class to store all Help Guide information and images
 */
public enum HelpGuide {

    // Intro guide to be shown upon first time launch - also provides map walk through
    INTRO_AND_MAP("Introduction To NE Architectural and Map Screen", 0,
            new int[]{
                    R.drawable.intro_guide_1,
                    R.drawable.intro_guide_2,
                    R.drawable.intro_guide_3,
                    R.drawable.intro_guide_4,
                    R.drawable.intro_guide_5,
                    R.drawable.intro_guide_6,
                    R.drawable.intro_guide_7,
                    R.drawable.intro_guide_8,
                    R.drawable.intro_guide_9,
                    R.drawable.intro_guide_10,
                    R.drawable.intro_guide_11},
            new String[]{
                    "Welcome to NE Architectural - swipe along to find out how to use the app!",
                    "From the home screen, tap a map marker to show a popup window of summary information about a location",
                    "To view all map markers again, simply hold your finger on the screen or press the back button whilst a popup is open",
                    "To find out more about a location, click on the pop up window to open a location page",
                    "To search through locations, click the magnifying glass icon in the top right corner of the screen to open the search screen",
                    "To navigate around the app, click the the barred icon (hamburger button) in the top left corner to open a navigation menu",
                    "Select the page you wish to view",
                    "Should you wish to change your settings, navigate to the settings page via the navigation menu",
                    "From here, you can change any settings to suit your needs. Settings will be saved automatically upon changing",
                    "For more in-depth guides on how to use the features of NE Architectural, navigate to the help page and select the guide you wish to use",
                    "Enjoy exploring the North East!"
        }),

    // Search guide to explain how to use the search feature
    SEARCH("How to use the Search Feature", 5,
            new int[]{
                    R.drawable.intro_guide_5,
                    R.drawable.search_guide_2,
                    R.drawable.search_guide_3,
                    R.drawable.search_guide_4,
                    R.drawable.search_guide_5,
                    R.drawable.search_guide_6,
                    R.drawable.search_guide_7,
                    R.drawable.search_guide_8,
                    R.drawable.search_guide_9},
            new String[]{
                    "Click the magnifying glass icon in the top right corner of the screen from any page",
                    "Locations are ordered their distance to your last known location if location is enabled",
                    "If location is not enabled, locations are ordered alphabetically",
                    "Tap the two checkboxes to filter locations by your liked locations or wheelchair accessible locations respectively",
                    "Tap the downward arrow to see more tags to filter by. Multiple tags can be selected at any time to filter your search further",
                    "Use the distance slider to see only locations that are close to you up to a maximum distance of 10 kilometres/miles",
                    "To change the distance unit you are using, access the settings page and select the desired distance unit",
                    "Tap the magnifying glass a second time to use a textual search. Search for locations by name or their location type.",
                    "Click on any of the search result boxes to access the location page for that location"
            }),

    // Guide to explain how to find directions to a given location
    DIRECTIONS("How to find directions to a Location", 3,
            new int[] {
                    R.drawable.directions_guide_1,
                    R.drawable.directions_guide_2,
                    R.drawable.directions_guide_3,
                    R.drawable.directions_guide_4},
            new String[]{
                    "Access a location page from either the map/home, search or timeline pages",
                    "Once on the location page, scroll to the bottom of the page to find the map",
                    "Tap the 'Take me here!' button above the map",
                    "The Google Maps app will promptly open with a route directing you from your current position to the selected location"
            }),

    // Guide explaining how to like/unlike a location
    LIKE("How to 'like' a Location", 3,
            new int[] {
                    R.drawable.directions_guide_1,
                    R.drawable.like_guide_2,
                    R.drawable.like_guide_3,
                    R.drawable.like_guide_4},
            new String[]{
                    "Access a location page from either the map/home, search or timeline pages",
                    "On the location page, tap the heart icon to ‘like’ that location",
                    "You will now see this location as a ‘liked’ location when filtering application-wide or in the search feature",
                    "To unlike a location, simply return to the location page and tap the heart icon again to remove the like"
            }),

    // Guide explaining to apply filters across the application via the settings
    FILTERS("How to apply Application-wide Filters", 5,
            new int[] {
                    R.drawable.intro_guide_8,
                    R.drawable.filters_guide_2,
                    R.drawable.filters_guide_3,
                    R.drawable.filters_guide_4,
                    R.drawable.filters_guide_5,
                    R.drawable.filters_guide_6,
                    R.drawable.filters_guide_7,
                    R.drawable.filters_guide_8},
            new String[] {
                    "Navigate to the settings page via the navigation menu",
                    "From here, locations can be filtered application-wise in two ways: by distance and by tags",
                    "To filter by distance, adjust the slider to only see locations within a radius of the selected distance relative to your position",
                    "If location is not enabled, distance filtering will not be applied",
                    "To filter by tags, tap the ‘Select Tags’ button which will prompt a popup menu to appear",
                    "Check any boxes which correspond to the tags you wish to filter by - multiple tags can be selected to further filter your search",
                    "Once your filters have been selected, only locations matching your filtering criteria will be shown on the map and timeline screen",
                    "Note that application-wide filtering does not apply to the search so that all locations can be searched independently"
            }),

    // Guide explaining how to use the different features of the timeline
    TIMELINE("How to use the Timeline", 5,
            new int[] {
                    R.drawable.timeline_guide_1,
                    R.drawable.timeline_guide_2,
                    R.drawable.timeline_guide_3,
                    R.drawable.timeline_guide_4,
                    R.drawable.timeline_guide_5,
                    R.drawable.timeline_guide_6,
                    R.drawable.timeline_guide_7,
                    R.drawable.timeline_guide_8,
                    R.drawable.timeline_guide_9},
            new String[] {
                    "Navigate to the timeline page via the navigation menu",
                    "Scroll along the page horizontally to see where the remaining locations place on the timeline",
                    "To see additional architectural information images for a location, tap anywhere on the timeline segment for that location",
                    "The snippet of architectural information can be scrolled down if it does not all initially fit on the screen",
                    "Tap the ‘Find out More’ button to be taken to a location page for the selected location",
                    "To apply or remove application-wide filters, tap the pink switch icon in the top right of the timeline",
                    "Locations are initially ordered by Oldest to Newest - to change the order to Newest to Oldest and vice versa, begin by tapping the dropdown menu labeled by the current order",
                    "Select which order you wish to use",
                    "Locations will now be shown in the selected order"
            });

    private final String title; // Title of the guide
    private final int[] guideImageIDs; // All guide image drawable resource IDs
    private final String[] guideText; // All text for guide (note that len(guideImageIDs) should equal len(guideText))
    private final int menuImageIndex; // The index of the image in guideImageIDs to be displayed as part of the menu item

    HelpGuide(String title, int menuImageIndex, int[] guideImageIDs, String[] guideText) {
        this.title = title;
        this.menuImageIndex = menuImageIndex;
        this.guideImageIDs = guideImageIDs;
        this.guideText = guideText;
    }

    // Returns the number of pages in the guide
    public int getGuideLength() {
        return guideImageIDs.length;
    }

    // Getters for title and menu image index
    public String getTitle() {
        return title;
    }

    public int getMenuImageIndex() {
        return menuImageIndex;
    }

    // Getter for an individual image ID at the provided position in the array
    public int getSingleImageID(int position) {
        return guideImageIDs[position];
    }

    // Getter for an individual text string at the provided position in the array
    public String getSingleText(int position) {
        return guideText[position];
    }

}
