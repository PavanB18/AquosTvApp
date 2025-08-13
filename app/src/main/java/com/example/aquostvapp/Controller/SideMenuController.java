package com.example.aquostvapp.Controller;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.aquostvapp.R;
import com.example.aquostvapp.Screens.DailyTopRanked;
import com.example.aquostvapp.Screens.Favorite;
import com.example.aquostvapp.Screens.Home;
import com.example.aquostvapp.Screens.MonthlyTopRankedShow;
import com.example.aquostvapp.Screens.Settings;
import com.example.aquostvapp.Screens.WatchList;
import com.example.aquostvapp.Screens.WeeklyTopRanked;

public class SideMenuController {

    private final LinearLayout sideMenu, menuToggle, menuHome, menuDailyTopRanked, menuWeeklyTopRanked,
            menuMonthlyTopRankedShow, menuSettings, menuFavorite, menuWatchList;
    private final TextView toggleLabel, labelHome, labelDailyTopRanked, labelWeeklyTopRanked,
            labelMonthlyTopRankedShow, labelSettings, labelFavorite, labelWatchList;
    private boolean isMenuExpanded = true;
    private final FragmentManager fragmentManager;
    private final Context context;

    public SideMenuController(View rootView, FragmentManager fragmentManager, Context context) {
        this.fragmentManager = fragmentManager;
        this.context = context;

        sideMenu = rootView.findViewById(R.id.sideMenu);
        menuToggle = rootView.findViewById(R.id.menuToggle);
        menuHome = rootView.findViewById(R.id.menuHome);
        menuDailyTopRanked = rootView.findViewById(R.id.menuDailyTopRanked);
        menuWeeklyTopRanked = rootView.findViewById(R.id.menuWeeklyTopRanked);
        menuMonthlyTopRankedShow = rootView.findViewById(R.id.menuMonthlyTopRankedShow);
        menuSettings = rootView.findViewById(R.id.menuSettings);
        menuFavorite = rootView.findViewById(R.id.menuFavorite);
        menuWatchList = rootView.findViewById(R.id.menuWatchList);

        toggleLabel = rootView.findViewById(R.id.toggleLabel);
        labelHome = rootView.findViewById(R.id.labelHome);
        labelDailyTopRanked = rootView.findViewById(R.id.labelDailyTopRanked);
        labelWeeklyTopRanked = rootView.findViewById(R.id.labelWeeklyTopRanked);
        labelMonthlyTopRankedShow = rootView.findViewById(R.id.labelMonthlyTopRankedShow);
        labelSettings = rootView.findViewById(R.id.labelSettings);
        labelFavorite = rootView.findViewById(R.id.labelFavorite);
        labelWatchList = rootView.findViewById(R.id.labelWatchList);

        setupMenuClicks();
        setupExpandCollapseLogic();
        setupFocusListeners(); // New: Set up focus listeners for D-pad
    }

    // New method for D-pad navigation
    private void setupFocusListeners() {
        // Set all menu items to be focusable
        menuToggle.setFocusable(true);
        menuHome.setFocusable(true);
        menuDailyTopRanked.setFocusable(true);
        menuWeeklyTopRanked.setFocusable(true);
        menuMonthlyTopRankedShow.setFocusable(true);
        menuSettings.setFocusable(true);
        menuFavorite.setFocusable(true);
        menuWatchList.setFocusable(true);

        // Add a focus listener to the side menu toggle to handle expansion
        menuToggle.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                toggleSideMenu(true);
            }
        });

        // Add focus listener to the side menu container itself to handle collapse.
        // This is a robust way to ensure the menu collapses when focus moves away.
        sideMenu.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                toggleSideMenu(false);
            }
        });
    }

    private void setupMenuClicks() {
        menuHome.setOnClickListener(v -> loadFragment(new Home()));
        menuDailyTopRanked.setOnClickListener(v -> loadFragment(new DailyTopRanked()));
        menuWeeklyTopRanked.setOnClickListener(v -> loadFragment(new WeeklyTopRanked()));
        menuMonthlyTopRankedShow.setOnClickListener(v -> loadFragment(new MonthlyTopRankedShow()));
        menuSettings.setOnClickListener(v -> loadFragment(new Settings()));
        menuFavorite.setOnClickListener(v -> loadFragment(new Favorite()));
        menuWatchList.setOnClickListener(v -> loadFragment(new WatchList()));
    }

    private void setupExpandCollapseLogic() {
        // This is now redundant for D-pad but useful for touch events.
        menuToggle.setOnClickListener(v -> toggleSideMenu(!isMenuExpanded));
    }

    // New method signature to allow programmatic control of the menu state
    public void toggleSideMenu(boolean expand) {
        isMenuExpanded = expand;
        int newWidth = dpToPx(isMenuExpanded ? 200 : 64);
        ViewGroup.LayoutParams params = sideMenu.getLayoutParams();
        params.width = newWidth;
        sideMenu.setLayoutParams(params);

        toggleLabel.setVisibility(isMenuExpanded ? View.VISIBLE : View.GONE);
        labelHome.setVisibility(isMenuExpanded ? View.VISIBLE : View.GONE);
        labelDailyTopRanked.setVisibility(isMenuExpanded ? View.VISIBLE : View.GONE);
        labelWeeklyTopRanked.setVisibility(isMenuExpanded ? View.VISIBLE : View.GONE);
        labelMonthlyTopRankedShow.setVisibility(isMenuExpanded ? View.VISIBLE : View.GONE);
        labelSettings.setVisibility(isMenuExpanded ? View.VISIBLE : View.GONE);
        labelFavorite.setVisibility(isMenuExpanded ? View.VISIBLE : View.GONE);
        labelWatchList.setVisibility(isMenuExpanded ? View.VISIBLE : View.GONE);
    }

    public boolean isMenuExpanded() {
        return isMenuExpanded;
    }

    private int dpToPx(int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    public void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.mainContent, fragment);
        transaction.commit();
    }
}