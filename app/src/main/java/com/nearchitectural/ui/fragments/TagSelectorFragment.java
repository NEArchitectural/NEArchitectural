package com.nearchitectural.ui.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.nearchitectural.R;
import com.nearchitectural.utilities.TagID;
import com.nearchitectural.utilities.TagMapper;

import java.util.ArrayList;
import java.util.List;

/* Author:  Kristiyan Doykov, Joel Bell-Wilding
 * Since:   15/01/20
 * Version: 1.2
 * Purpose: Handle the activation/deactivation of tags within a provider
 *          TagMapper object through a UI Dialog
 */
public class TagSelectorFragment extends DialogFragment {

    private DialogInterface.OnDismissListener dismissListener; // Custom dismiss listener
    private TagMapper tagMapper; // Tag utility used to store/manipulate tag states

    public TagSelectorFragment(TagMapper tagMapper) {
        this.tagMapper = tagMapper;
    }

    // Manages the creation and event handling involved with the dialogue
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new MaterialAlertDialogBuilder(getContext(), R.style.DialogTheme);

        // Stores tag display names as a CharSequence array to set as the multi choice items
        final CharSequence[] items = new CharSequence[tagMapper.getTagDisplayNameMap().size()];
        List<String> tagDisplayNames = new ArrayList<>(tagMapper.getTagDisplayNameMap().keySet());
        for (int i = 0; i < tagMapper.getTagDisplayNameMap().size(); i++) {
            items[i] = tagDisplayNames.get(i);
        }

        // Stores tag boolean values as a boolean array to set as the multi choice item states
        final boolean[] currentStateOfItems = new boolean[tagMapper.getTagValuesMap().size()];
        List<Boolean> tagValues = new ArrayList<>(tagMapper.getTagValuesMap().values());
        for (int i = 0; i < tagMapper.getTagValuesMap().size(); i++) {
            currentStateOfItems[i] = tagValues.get(i);
        }

        // Set the dialogue title
        builder.setTitle(R.string.tag_selector_title)
                /* Set the items to each of the tag display names and the current state of items
                 * to their corresponding values in the tag mapper */
                .setMultiChoiceItems(items, currentStateOfItems,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            // Handles filtering of locations when a checkbox is ticked or unticked
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {

                                TagID selectedTag = tagMapper.getTagDisplayNameMap()
                                        .get(items[which]);

                                if (isChecked) {
                                    tagMapper.addTagToMapper(selectedTag, true);
                                } else if (tagMapper.getTagValuesMap().get(selectedTag)) {
                                    tagMapper.addTagToMapper(selectedTag, false);
                                }
                            }
                        })
                // Set the action buttons
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK
                    }
                });

        return builder.create();
    }

    // Setter for onDismiss Listener
    public void setDismissListener(DialogInterface.OnDismissListener dismissListener) {
        this.dismissListener = dismissListener;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        // If a custom dismiss listener has been assigned, invoke it's on dismiss method
        if (dismissListener != null) {
            dismissListener.onDismiss(dialog);
        }
        super.onDismiss(dialog);
    }
}
