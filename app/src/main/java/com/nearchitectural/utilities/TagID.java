package com.nearchitectural.utilities;

/* Author:  Joel Bell-Wilding
 * Since:   07/02/20
 * Version: 1.0
 * Purpose: Enumerator storing available Tag IDs for use with TagMapper utility class
 */
public enum TagID {

    WHEELCHAIR_ACCESSIBLE("Wheelchair Accessible", "wheelChairAccessible", "ic_accessible"),
    CHILD_FRIENDLY("Child Friendly", "childFriendly", "ic_child_friendly"),
    CHEAP_ENTRY("Cheap Entry", "cheapEntry", "ic_cheap"),
    FREE_ENTRY("Free Entry", "freeEntry", "ic_free"),
    BOOK_IN_ADVANCE("Book in Advance", "bookInAdvance", "ic_book_advance");

    public final String displayName; // Name of tag as displayed in application
    public final String databaseReference; // Reference value for database retrieval
    public final String iconName; // Name of icon in resources (Icon does not show if left blank/not found)

    TagID(String displayName, String databaseReference, String iconName) {
        this.displayName = displayName;
        this.databaseReference = databaseReference;
        this.iconName = iconName;
    }
}