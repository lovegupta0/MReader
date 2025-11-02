package com.LG.mreader.PageActivity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.LG.mreader.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the  factory method to
 * create an instance of this fragment.
 */


public class MenuFragement extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_menu, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Setup all menu items with their icons and labels
        setupMenuItem(view.findViewById(R.id.menu_tabs), R.drawable.ic_tabs, "Tabs");
        setupMenuItem(view.findViewById(R.id.menu_bookmarks), R.drawable.ic_bookmarks, "Bookmarks");
        setupMenuItem(view.findViewById(R.id.menu_history), R.drawable.ic_history, "History");
        setupMenuItem(view.findViewById(R.id.menu_read_mode), R.drawable.ic_read_mode, "Read");
        setupMenuItem(view.findViewById(R.id.menu_reload), R.drawable.reload_ic, "Reload");

        setupMenuItem(view.findViewById(R.id.menu_bookmark_add), R.drawable.ic_bookmark_adds, "Add Bookmark");
        setupMenuItem(view.findViewById(R.id.menu_pc), R.drawable.ic_pc, "Desktop");
        setupMenuItem(view.findViewById(R.id.menu_incognito), R.drawable.ic_incongnio, "Incognito");
        setupMenuItem(view.findViewById(R.id.menu_report), R.drawable.ic_report, "Report");
        setupMenuItem(view.findViewById(R.id.menu_block), R.drawable.ic_block, "Block");

        setupMenuItem(view.findViewById(R.id.menu_download), R.drawable.ic_download, "Download");
        setupMenuItem(view.findViewById(R.id.menu_feedback), R.drawable.ic_feedback, "Feedback");
        setupMenuItem(view.findViewById(R.id.menu_share), R.drawable.ic_share, "Share");
        setupMenuItem(view.findViewById(R.id.menu_settings), R.drawable.ic_setting, "Settings");
        setupMenuItem(view.findViewById(R.id.menu_quit), R.drawable.ic_quit, "Quit");
    }

    /**
     * Setup a menu item with icon, label, and click listener
     * @param menuItem The root view of the included item_menu_button layout
     * @param iconRes The drawable resource ID for the icon
     * @param label The text label for the menu item
     */
    private void setupMenuItem(View menuItem, int iconRes, String label) {
        if (menuItem == null) return;

        // Set icon
        ImageView icon = menuItem.findViewById(R.id.icon);
        if (icon != null) {
            icon.setImageResource(iconRes);
        }

        // Set label
        TextView labelView = menuItem.findViewById(R.id.label);
        if (labelView != null) {
            labelView.setText(label);
        }

        // Set click listener
        menuItem.setOnClickListener(v -> handleMenuClick(v.getId()));
    }

    /**
     * Alternative method using string resources instead of hardcoded strings
     * Use this if you have strings defined in strings.xml
     */
    private void setupMenuItemWithStringRes(View menuItem, int iconRes, int labelRes) {
        if (menuItem == null) return;

        ImageView icon = menuItem.findViewById(R.id.icon);
        if (icon != null) {
            icon.setImageResource(iconRes);
        }

        TextView labelView = menuItem.findViewById(R.id.label);
        if (labelView != null) {
            labelView.setText(labelRes);
        }

        menuItem.setOnClickListener(v -> handleMenuClick(v.getId()));
    }

    /**
     * Handle menu item clicks
     * @param menuId The ID of the clicked menu item
     */
    private void handleMenuClick(int menuId) {
        if (menuId == R.id.menu_tabs) {
            // Handle tabs click
            showToast("Tabs clicked");
            // TODO: Open tabs activity/fragment

        } else if (menuId == R.id.menu_bookmarks) {
            showToast("Bookmarks clicked");
            // TODO: Open bookmarks activity/fragment

        } else if (menuId == R.id.menu_history) {
            showToast("History clicked");
            // TODO: Open history activity/fragment

        } else if (menuId == R.id.menu_read_mode) {
            showToast("Read Mode clicked");
            // TODO: Toggle read mode

        } else if (menuId == R.id.menu_reload) {
            showToast("Reload clicked");
            // TODO: Reload current page

        } else if (menuId == R.id.menu_bookmark_add) {
            showToast("Add Bookmark clicked");
            // TODO: Add current page to bookmarks

        } else if (menuId == R.id.menu_pc) {
            showToast("Desktop Mode clicked");
            // TODO: Toggle desktop/mobile mode

        } else if (menuId == R.id.menu_incognito) {
            showToast("Incognito clicked");
            // TODO: Open incognito tab

        } else if (menuId == R.id.menu_report) {
            showToast("Report clicked");
            // TODO: Open report dialog

        } else if (menuId == R.id.menu_block) {
            showToast("Block clicked");
            // TODO: Block current site

        } else if (menuId == R.id.menu_download) {
            showToast("Download clicked");
            // TODO: Open downloads

        } else if (menuId == R.id.menu_feedback) {
            showToast("Feedback clicked");
            // TODO: Open feedback form

        } else if (menuId == R.id.menu_share) {
            showToast("Share clicked");
            // TODO: Share current page

        } else if (menuId == R.id.menu_settings) {
            showToast("Settings clicked");
            // TODO: Open settings

        } else if (menuId == R.id.menu_quit) {
            showToast("Quit clicked");
            // TODO: Exit app or close menu
            if (getActivity() != null) {
                getActivity().finish();
            }
        }

        // Dismiss the menu after handling click
        dismissMenu();
    }

    /**
     * Dismiss the menu (if it's a bottom sheet or dialog)
     */
    private void dismissMenu() {
        // If this fragment is in a BottomSheetDialogFragment, dismiss it
        if (getParentFragment() != null) {
            // Handle if nested in another fragment
        } else if (getActivity() != null) {
            // Pop back stack or finish activity
            getActivity().getSupportFragmentManager().popBackStack();
        }
    }

    /**
     * Helper method to show toast messages
     */
    private void showToast(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }
}