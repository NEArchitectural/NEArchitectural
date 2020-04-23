package com.nearchitectural.utilities.models;

/* Author:  Joel Bell-Wilding - Original author
 * Since:   07/02/20
 * Version: 1.2
 * Purpose: Enumerator storing available Tag IDs for use with TagMapper utility class
 */
public enum TagID {

    WHEELCHAIR_ACCESSIBLE("Wheelchair Accessible", "wheelChairAccessible", "ic_accessible"),
    CHILD_FRIENDLY("Child Friendly", "childFriendly", "ic_child_friendly"),
    CHEAP_ENTRY("Cheap Entry", "cheapEntry", "ic_cheap"),
    FREE_ENTRY("Free Entry", "freeEntry", "ic_free"),
    BOOK_IN_ADVANCE("Book in Advance", "bookInAdvance", "ic_book_advance"),
    // LIKED_BY_YOU Tag handled internally based on user's liked locations (stored on device)
    LIKED_BY_YOU("Liked by You", null, "ic_liked_tag");

    public final String displayName; // Name of tag as displayed in application
    public final String databaseReference; // Reference value for database retrieval
    public final String iconName; // Name of icon in resources (Icon does not show if left blank/not found)

    TagID(String displayName, String databaseReference, String iconName) {
        this.displayName = displayName;
        this.databaseReference = databaseReference;
        this.iconName = iconName;
    }
}