package com.nearchitectural.utilities;

/**author: Joel Bell-Wilding
 * since: 07/02/20
 * version: 1.0
 * purpose: Enumerator storing available Tag IDs for use with TagMapper utility class
 */
public enum TagID {

    WHEELCHAIR_ACCESSIBLE("Wheelchair Accessible", "wheelChairAccessible"),
    CHILD_FRIENDLY("Child Friendly", "childFriendly"),
    CHEAP_ENTRY("Cheap Entry", "cheapEntry"),
    FREE_ENTRY("Free Entry", "freeEntry");

    public final String displayName;
    public final String databaseReference;

    TagID(String displayName, String databaseReference) {
        this.displayName = displayName;
        this.databaseReference = databaseReference;
    }
}