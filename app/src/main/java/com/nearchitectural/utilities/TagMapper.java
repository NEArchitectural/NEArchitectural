package com.nearchitectural.utilities;

import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.LinkedHashMap;
import java.util.Map;

/* Author:  Joel Bell-Wilding
 * Since:   07/02/20
 * Version: 1.0
 * Purpose: Utility class which stores mapping of Tag IDs, their state (active/inactive)
 *          and display names for use when searching/applying via settings
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
        for (TagID tag : TagID.values()) {
            tagValuesMap.put(tag, false);
            tagDisplayNameMap.put(tag.displayName, tag);
        }
    }

    // Constructor for tag mapper with values read in from database (i.e. location specific set tags)
    public TagMapper(QueryDocumentSnapshot document) {

        tagValuesMap = new LinkedHashMap<>();
        tagDisplayNameMap = new LinkedHashMap<>();

        // Reads values from database for a given location and sets tags to appropriate boolean value
        for (TagID tag : TagID.values()) {
            tagValuesMap.put(tag,
                    document.getData().get(tag.databaseReference) != null
                            && (boolean) document.getData().get(tag.databaseReference));
            tagDisplayNameMap.put(tag.displayName, tag);
        }
    }

    // Getter for map of Tag IDs to their respective state
    public Map<TagID, Boolean> getTagValuesMap() {
        return new LinkedHashMap<>(tagValuesMap);
    }

    // Getter for map of Tag display names to their respective Tag IDs
    public Map<String, TagID> getTagDisplayNameMap() {
        return new LinkedHashMap<>(tagDisplayNameMap);
    }

    // Removes a tag from both internal maps
    public void removeTagFromMapper(TagID tag) {
        tagValuesMap.remove(tag);
        tagDisplayNameMap.remove(tag.displayName);
    }

    // Adds a tag to both internal maps
    public void addTagToMapper(TagID tag, boolean isActive) {
        tagValuesMap.put(tag, isActive);
        tagDisplayNameMap.put(tag.displayName, tag);
    }

}
