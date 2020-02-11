package com.nearchitectural.utilities;

import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.LinkedHashMap;
import java.util.Map;

/**author: Joel Bell-Wilding
 * since: 07/02/20
 * version: 1.0
 * purpose: Utility class which stores mapping of Tag IDs, their state (active/inactive)
 * and display names for use when searching/applying via settings
 */
public class TagMapper {

    // Map of Tag IDs to their state (true = active, false = inactive)
    private Map<TagID, Boolean> tagValuesMap;
    // Map of Tag Display Names (presented on UI) to Tag IDs
    private Map<String, TagID> tagDisplayNameMap;

    // Constructor for creating a duplicate tag mapper
    public TagMapper(TagMapper tagMapper) {
        this.tagValuesMap = new LinkedHashMap<>(tagMapper.getTagValuesMap());
        this.tagDisplayNameMap = new LinkedHashMap<>(tagMapper.getTagDisplayNameMap());
    }

    // Constructor for tag mapper with default false values (i.e. no tags set)
    public TagMapper() {

        tagValuesMap = new LinkedHashMap<>();
        tagDisplayNameMap = new LinkedHashMap<>();

        // Sets all tags to be inactive by default
        tagValuesMap.put(TagID.WHEELCHAIR_ACCESSIBLE, false);
        tagDisplayNameMap.put("Wheelchair Accessible", TagID.WHEELCHAIR_ACCESSIBLE);

        tagValuesMap.put(TagID.CHILD_FRIENDLY, false);
        tagDisplayNameMap.put("Child Friendly", TagID.CHILD_FRIENDLY);

        tagValuesMap.put(TagID.CHEAP_ENTRY, false);
        tagDisplayNameMap.put("Cheap Entry", TagID.CHEAP_ENTRY);

        tagValuesMap.put(TagID.FREE_ENTRY, false);
        tagDisplayNameMap.put("Free Entry", TagID.FREE_ENTRY);
    }

    // Constructor for tag mapper with values read in from database (i.e. location specific set tags)
    public TagMapper(QueryDocumentSnapshot document) {

        tagValuesMap = new LinkedHashMap<>();
        tagDisplayNameMap = new LinkedHashMap<>();

        // Reads values from database for a given location and sets tags to appropriate boolean value
        tagValuesMap.put(TagID.WHEELCHAIR_ACCESSIBLE,
                document.getData().get("wheelChairAccessible") != null
                        && (boolean) document.getData().get("wheelChairAccessible"));
        tagDisplayNameMap.put("Wheelchair Accessible", TagID.WHEELCHAIR_ACCESSIBLE);

        tagValuesMap.put(TagID.CHILD_FRIENDLY,
                document.getData().get("childFriendly") != null
                        && (boolean) document.getData().get("childFriendly"));
        tagDisplayNameMap.put("Child Friendly", TagID.CHILD_FRIENDLY);

        tagValuesMap.put(TagID.CHEAP_ENTRY,
                document.getData().get("cheapEntry") != null
                        && (boolean) document.getData().get("cheapEntry"));
        tagDisplayNameMap.put("Cheap Entry", TagID.CHEAP_ENTRY);

        tagValuesMap.put(TagID.FREE_ENTRY,
                document.getData().get("freeEntry") != null
                        && (boolean) document.getData().get("freeEntry"));
        tagDisplayNameMap.put("Free Entry", TagID.FREE_ENTRY);

    }

    // Getter for map of Tag IDs to their respective state
    public Map<TagID, Boolean> getTagValuesMap() {
        return tagValuesMap;
    }

    // Getter for map of Tag display names to their respective Tag IDs
    public Map<String, TagID> getTagDisplayNameMap() {
        return tagDisplayNameMap;
    }

    // Removes a tag from both internal maps
    public void removeTagFromMapper(TagID tagID, String displayName) {
        tagValuesMap.remove(tagID);
        tagDisplayNameMap.remove(displayName);
    }

    // Adds a tag to both internal maps
    public void addTagToMapper(TagID tagID, boolean isActive, String displayName) {
        tagValuesMap.put(tagID, isActive);
        tagDisplayNameMap.put(displayName, tagID);
    }

}
