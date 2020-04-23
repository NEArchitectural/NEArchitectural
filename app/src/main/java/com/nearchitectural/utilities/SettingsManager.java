package com.nearchitectural.utilities;

import android.content.Context;
import android.content.SharedPreferences;

import com.nearchitectural.R;
import com.nearchitectural.utilities.models.TagID;

import java.util.HashSet;
import java.util.Set;

/* Author:  Joel Bell-Wilding, Mark Lumb
 * Since:   19/03/20
 * Version: 1.1
 * Purpose: Handle the retrieval and storage of user settings on the Android device
 */
public class SettingsManager {

    private Context context; // The context through which the Settings Manager is instantiated

    public SettingsManager(Context applicationContext) {
        this.context = applicationContext;
    }

    /* Initialises settings singleton from values stored on device,
     * or uses default values if no settings are saved. This method should
     * only need to be called once. */
    public void retrieveSettings() {

        // Shared Preferences representing the settings file stored on the device
        SharedPreferences settingsFile = context.getSharedPreferences(
                context.getString(R.string.settings_file_key), Context.MODE_PRIVATE);

        // Initialise settings file to store default settings if it does not already exist
        if (!settingsFile.contains(context.getString(R.string.settings_file_exists))) {

            SharedPreferences.Editor editor = settingsFile.edit();
            // Apply default values for each key in settings file
            editor.putInt(context.getString(R.string.settings_font_size), R.style.FontStyle_Medium);
            editor.putString(context.getString(R.string.settings_distance_unit), Settings.DistanceUnit.KILOMETRE.name());
            editor.putBoolean(context.getString(R.string.settings_location_permissions_granted), false);
            editor.putFloat(context.getString(R.string.settings_max_distance), (float) Double.MAX_VALUE);
            editor.putInt(context.getString(R.string.settings_max_distance_slider), 0);
            editor.putStringSet(context.getString(R.string.settings_active_tags), new HashSet<String>());
            editor.putStringSet(context.getString(R.string.settings_liked_locations), new HashSet<String>());
            editor.putBoolean(context.getString(R.string.settings_file_exists), false); // False upon first startup
            editor.commit();
        }

        // Read settings values into Settings singleton from device
        Settings userSettings = Settings.getInstance();
        userSettings.setFontSize(settingsFile.getInt(context.getString(R.string.settings_font_size), R.style.FontStyle_Medium));
        userSettings.setDistanceUnit(Settings.DistanceUnit.valueOf(settingsFile.getString(
                context.getString(R.string.settings_distance_unit), Settings.DistanceUnit.KILOMETRE.name())));
        userSettings.setLocationPermissionsGranted(settingsFile.getBoolean(context.getString(R.string.settings_location_permissions_granted), false));
        userSettings.setMaxDistance(settingsFile.getFloat(context.getString(R.string.settings_max_distance), (float) Double.MAX_VALUE));
        userSettings.setMaxDistanceSliderVal(settingsFile.getInt(context.getString(R.string.settings_max_distance_slider), 0));
        userSettings.setLikedLocations(new HashSet<>(settingsFile.getStringSet(context.getString(R.string.settings_liked_locations), new HashSet<String>())));
        userSettings.setSettingsFileExists(settingsFile.getBoolean(context.getString(R.string.settings_file_exists), false));

        // Set the active tags for the Settings TagMapper
        Set<String> activeTags = settingsFile.getStringSet(context.getString(R.string.settings_active_tags), new HashSet<String>());
        for (TagID tag : TagID.values()) {
            userSettings.setTagValue(tag, activeTags.contains(tag.toString()));
        }
    }

    // Saves the current state of all settings in the Settings singleton to the Android device
    public void saveSettings() {

        // Shared Preferences representing the settings file stored on the device
        SharedPreferences settingsFile = context.getSharedPreferences(
                context.getString(R.string.settings_file_key), Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = settingsFile.edit();
        Settings userSettings = Settings.getInstance();
        // Save the current value of each setting in the singleton to the device
        editor.putInt(context.getString(R.string.settings_font_size), userSettings.getFontSize());
        editor.putString(context.getString(R.string.settings_distance_unit), userSettings.getDistanceUnit().name());
        editor.putBoolean(context.getString(R.string.settings_location_permissions_granted), userSettings.locationPermissionsAreGranted());
        editor.putFloat(context.getString(R.string.settings_max_distance), (float) userSettings.getMaxDistance());
        editor.putInt(context.getString(R.string.settings_max_distance_slider), userSettings.getMaxDistanceSliderVal());
        editor.putStringSet(context.getString(R.string.settings_liked_locations), userSettings.getLikedLocations());
        //  Always set to true after first startup
        editor.putBoolean(context.getString(R.string.settings_file_exists), true);

        // Retrieve the active tags from the Settings TagMapper and save them to device
        HashSet<String> activeTags = new HashSet<>();
        for (TagID tag : TagID.values()) {
            if (userSettings.getTagValue(tag)) {
                activeTags.add(tag.toString());
            }
        }
        editor.putStringSet(context.getString(R.string.settings_active_tags), activeTags);

        editor.commit();
    }
}
