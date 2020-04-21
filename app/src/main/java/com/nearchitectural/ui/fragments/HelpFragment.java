package com.nearchitectural.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.nearchitectural.R;
import com.nearchitectural.ui.activities.MapsActivity;
import com.nearchitectural.ui.adapters.HelpGuideMenuAdapter;
import com.nearchitectural.ui.adapters.HelpGuidePagerAdapter;
import com.nearchitectural.ui.interfaces.BackHandleFragment;
import com.nearchitectural.utilities.models.HelpGuide;

import me.relex.circleindicator.CircleIndicator;

/* Author:  Kristiyan Doykov
 * Since:   10/12/19
 * Version: 1.0
 * purpose: To provide a selection of help guides and display these guides in a
 *          step-by-step format
 */
public class HelpFragment extends Fragment implements BackHandleFragment {

    public static final String TAG = "HelpFragment";

    // LAYOUT ELEMENTS
    private RecyclerView helpGuideMenu; // The guide selection menu
    private RelativeLayout guideContainer; // Container holding the step-by-step guide view pager
    private ViewPager guideViewPager; // The view pager displaying each step of the guide
    private CircleIndicator indicator; // Circle indicator showing the progress of the view pager
    private Button returnButton; // Return button shown at the end of a guide

    private boolean introGuideDisplayed = false; // Determines if a guide is currently being shown
    private boolean guideIsDisplayed = false; // Determines if the fragment should perform the first time launch procedure

    // Bundle key to indicate to the help fragment to perform the first time launch guide routine
    private final String LAUNCH_GUIDE_KEY = "PERFORM_LAUNCH_GUIDE";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_help, container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set the navigation bar title and navigation menu item
        MapsActivity parentActivity = (MapsActivity) this.getActivity();
        assert parentActivity != null;
        parentActivity.getNavigationView().getMenu().findItem(R.id.nav_help).setChecked(true);
        parentActivity.setActionBarTitle(getString(R.string.navigation_help));

        // Get all layout elements
        helpGuideMenu = view.findViewById(R.id.help_guide_menu);
        guideContainer = view.findViewById(R.id.help_guide_container);
        guideViewPager = view.findViewById(R.id.help_guide_view_pager);
        indicator = view.findViewById(R.id.circle_indicator);
        returnButton = view.findViewById(R.id.return_button);

        // If return is clicked, hide the guide layout and return to menu
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideGuideLayout();
                guideIsDisplayed = false;
            }
        });

        // Set up the recyclerview menu with the custom adapter
        LinearLayoutManager recyclerManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        helpGuideMenu.setLayoutManager(recyclerManager);
        HelpGuideMenuAdapter adapter = new HelpGuideMenuAdapter(this);
        helpGuideMenu.setAdapter(adapter);

        // Check if fragment opened through first time launch procedure
        if (getArguments() != null) {
            introGuideDisplayed = getArguments().getBoolean(LAUNCH_GUIDE_KEY);
            // If first time launch, display the introductory guide
            displayGuide(HelpGuide.INTRO_AND_MAP);
        }

    }

    // Displays the provided guide in a series of step-by-step instructions
    public void displayGuide(final HelpGuide guide) {
        showGuideLayout(); // Show the guide

        // Set the ViewPager adapter up for the selected guide
        HelpGuidePagerAdapter helpGuideAdapter = new HelpGuidePagerAdapter(guide, getContext());
        guideViewPager.setAdapter(helpGuideAdapter);
        indicator.setViewPager(guideViewPager);
        guideIsDisplayed = true;

        // Check if user has returned to guide and guide is on final page
        if (guideViewPager.getCurrentItem() == guide.getGuideLength()-1) {
            // If so, show the return button
            returnButton.setVisibility(View.VISIBLE);
            indicator.setVisibility(View.GONE);
        }

        // Listener to keep track of which page the user is on
        guideViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // Do nothing
            }

            @Override
            public void onPageSelected(int position) {
                // Check if user is on final page of guide
                if (guideViewPager.getCurrentItem() == guide.getGuideLength()-1) {
                    // Display the return to menu button and hide indicator
                    returnButton.setVisibility(View.VISIBLE);
                    indicator.setVisibility(View.GONE);
                    // If first time launch, handle button press different
                    if (introGuideDisplayed) {
                        setButtonForFirsTimeLaunch();
                    }
                } else {
                    // If not on final page, hide return and show indicator
                    returnButton.setVisibility(View.GONE);
                    indicator.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                // Do nothing
            }
        });
    }

    // Re-purposes the 'return' button to direct user to home/map page upon first time launch
    private void setButtonForFirsTimeLaunch() {
        returnButton.setText(R.string.first_time_launch_button_text); // Change button text
        // On clicked, open home/map page instead of returning to menu
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, MapFragment.newInstance(true)).commit();
            }
        });
    }

    // Hides the guide layout and displays the menu
    private void hideGuideLayout() {
        helpGuideMenu.setVisibility(View.VISIBLE);
        guideContainer.setVisibility(View.GONE);
        returnButton.setVisibility(View.GONE);
    }

    // Hides the menu and shows the guide layout
    private void showGuideLayout() {
        helpGuideMenu.setVisibility(View.GONE);
        guideContainer.setVisibility(View.VISIBLE);
        indicator.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onBackPressed() {
        // If back pressed while guide displayed, return to menu
        if (guideIsDisplayed) {
            hideGuideLayout();
            guideIsDisplayed = false;
            return true;
        }
        return false; // Otherwise perform usual back press behaviour
    }
}
