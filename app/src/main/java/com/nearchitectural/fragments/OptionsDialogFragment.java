package com.nearchitectural.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.nearchitectural.activities.SearchableActivity;

import java.util.ArrayList;

public class OptionsDialogFragment extends DialogFragment {
    private ArrayList<Integer> selectedItems;
    private boolean cheapEntry;
    private boolean freeEntry;

    public OptionsDialogFragment(boolean cheapEntry, boolean freeEntry) {
        this.cheapEntry = cheapEntry;
        this.freeEntry = freeEntry;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        selectedItems = new ArrayList<>();  // Where we track the checked items
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        CharSequence[] items = {"Cheap Entry", "Free Entry"};
        // Set the dialog title
        boolean[] currentStateOfItems = new boolean[items.length];
        currentStateOfItems[0] = this.cheapEntry;
        currentStateOfItems[1] = this.freeEntry;
        builder.setTitle("Select filters")
                // Specify the list array, the items to be selected by default (null for none),
                // and the listener through which to receive callbacks when items are selected
                .setMultiChoiceItems(items, currentStateOfItems,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which,
                                                boolean isChecked) {
                                if (isChecked) {
                                    // If the user checked the item, add it to the selected items
                                    selectedItems.add(which);
                                    // If cheap entry was selected - check it
                                    if (which == 0) {
                                        cheapEntry = true;
                                        ((SearchableActivity) getActivity()).setCheapEntry(true);
                                        ((SearchableActivity) getActivity()).filterAndRearrange();
                                    }
                                    // If free entry was selected - check it
                                    if (which == 1) {
                                        freeEntry = true;
                                        ((SearchableActivity) getActivity()).setFreeEntry(true);
                                        ((SearchableActivity) getActivity()).filterAndRearrange();
                                    }

                                } else if (selectedItems.contains(which)) {
                                    // Else, if the item is already in the array, remove it
                                    selectedItems.remove(Integer.valueOf(which));
                                    // Uncheck cheap entry
                                    if (which == 0) {
                                        cheapEntry = false;
                                        ((SearchableActivity) getActivity()).setCheapEntry(false);
                                        ((SearchableActivity) getActivity()).filterAndRearrange();
                                    }
                                    // Uncheck free entry
                                    if (which == 1) {
                                        freeEntry = false;
                                        ((SearchableActivity) getActivity()).setFreeEntry(false);
                                        ((SearchableActivity) getActivity()).filterAndRearrange();
                                    }
                                }else {
                                    if (which == 0) {
                                        cheapEntry = false;
                                        ((SearchableActivity) getActivity()).setCheapEntry(false);
                                        ((SearchableActivity) getActivity()).filterAndRearrange();
                                    }
                                    // Uncheck free entry
                                    if (which == 1) {
                                        freeEntry = false;
                                        ((SearchableActivity) getActivity()).setFreeEntry(false);
                                        ((SearchableActivity) getActivity()).filterAndRearrange();
                                    }
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
        public void onDialogPositiveClick(Bundle bundle);

        public void onDialogNegativeClick(Bundle bundle);
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
