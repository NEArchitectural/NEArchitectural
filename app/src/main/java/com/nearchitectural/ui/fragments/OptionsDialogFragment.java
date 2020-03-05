package com.nearchitectural.ui.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.nearchitectural.ui.activities.SearchableActivity;
import com.nearchitectural.utilities.TagID;
import com.nearchitectural.utilities.TagMapper;

import java.util.ArrayList;
import java.util.List;

/* Author:  Kristiyan Doykov, Joel Bell-Wilding
 * Since:   15/01/20
 * Version: 1.1
 * Purpose: Handle the activation/deactivation of additional dialogue box
 *          tags to apply locations when using the search function
 */
public class OptionsDialogFragment extends DialogFragment {

    private TagMapper tagMapper; // Tag utility used to store/manipulate tag states

    // Prepares a new duplicate TagMapper containing only dialogue tags
    public OptionsDialogFragment(TagMapper tagMapper) {

        // Creates a copy of original TagMapper to be modified
        this.tagMapper = new TagMapper(tagMapper);

        // Removing the two tags that are handled separately
        this.tagMapper.removeTagFromMapper(TagID.WHEELCHAIR_ACCESSIBLE);
        this.tagMapper.removeTagFromMapper(TagID.CHILD_FRIENDLY);
    }

    // Manages the creation and event handling involved with the dialogue
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Stores tag display names as a CharSequence array to pass into onClick handler
        final CharSequence[] items = new CharSequence[tagMapper.getTagDisplayNameMap().size()];
        List<String> tagDisplayNames  = new ArrayList<>(tagMapper.getTagDisplayNameMap().keySet());
        for (int i = 0; i < tagMapper.getTagDisplayNameMap().size(); i++) {
            items[i] = tagDisplayNames.get(i);
        }

        // Stores tag boolean values (i.e. if they are active) as a boolean array to pass into onClick handler
        final boolean[] currentStateOfItems = new boolean[tagMapper.getTagValuesMap().size()];
        List<Boolean> tagValues = new ArrayList<>(tagMapper.getTagValuesMap().values());
        for (int i = 0; i < tagMapper.getTagValuesMap().size(); i++) {
            currentStateOfItems[i] = tagValues.get(i);
        }

        // Set the dialogue title
        builder.setTitle("Select filters")
                // Specify the list array, the items to be selected by default (null for none),
                // and the listener through which to receive callbacks when items are selected
                .setMultiChoiceItems(items, currentStateOfItems,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            // Handles filtering of locations when a checkbox is ticked or unticked
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {

                                if (isChecked) { // If a tag is activated it, add to selected items and apply
                                    ((SearchableActivity) getActivity())
                                            .setTag(tagMapper.getTagDisplayNameMap()
                                                    .get(items[which]), true);
                                    ((SearchableActivity) getActivity()).filterAndRearrange();
                                } else { // If a tag is deactivated, remove from selected items and apply
                                    ((SearchableActivity) getActivity())
                                            .setTag(tagMapper.getTagDisplayNameMap()
                                                    .get(items[which]), false);
                                    ((SearchableActivity) getActivity()).filterAndRearrange();
                                }
                            }
                        })
                // Set the action buttons
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK
                    }
                });

        return builder.create();
    }

    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface OptionsDialogListener {
        void onDialogPositiveClick(Bundle bundle);

        void onDialogNegativeClick(Bundle bundle);
    }

    // Use this instance of the interface to deliver action events
    private OptionsDialogListener listener;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = (OptionsDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString()
                    + " must implement OptionsDialogListener");
        }
    }
}
