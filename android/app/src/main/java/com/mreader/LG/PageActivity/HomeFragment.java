package com.mreader.LG.PageActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mreader.LG.Adapter.HomeBookmarkAdapter;
import com.mreader.LG.DataModel.BookmarkDataModel;
import com.mreader.LG.PoolService.CentralThreadPool;
import com.mreader.LG.ReactNative.PageActivity.BookmarkActivity;
import com.mreader.LG.Service.BookmarkService;
import com.mreader.LG.ViewModel.WebViewModel;
import com.mreader.databinding.FragmentHomeBinding;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private static final int SHORTCUT_LIMIT = 11;

    private FragmentHomeBinding home;
    private WebViewModel webViewModel;
    private BookmarkService bookmarkService;
    private HomeBookmarkAdapter adapter;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        home = FragmentHomeBinding.inflate(inflater, container, false);
        return home.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        webViewModel = new ViewModelProvider(requireActivity()).get(WebViewModel.class);
        bookmarkService = new BookmarkService(requireContext());
        setupBookmarks();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadBookmarks();
    }

    private void setupBookmarks() {
        adapter = new HomeBookmarkAdapter(new HomeBookmarkAdapter.Listener() {
            @Override
            public void onBookmarkClicked(BookmarkDataModel bookmark) {
                openBookmark(bookmark);
            }

            @Override
            public void onOverflowClicked() {
                startActivity(new Intent(requireContext(), BookmarkActivity.class));
            }
        });

        GridLayoutManager layoutManager = new GridLayoutManager(requireContext(), 3, RecyclerView.HORIZONTAL, false);
        home.bookmarksRecycler.setLayoutManager(layoutManager);
        home.bookmarksRecycler.setAdapter(adapter);
    }

    private void loadBookmarks() {
        CentralThreadPool.getInstance().submitTask(() -> {
            List<BookmarkDataModel> bookmarks = bookmarkService.getBookmarks();
            List<BookmarkDataModel> displayItems = prepareShortcutItems(bookmarks);

            mainHandler.post(() -> {
                if (!isAdded() || home == null) {
                    return;
                }
                adapter.submitList(displayItems);
                boolean isEmpty = displayItems.isEmpty();
                home.emptyState.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
                home.bookmarksRecycler.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
            });
            return null;
        });
    }

    private List<BookmarkDataModel> prepareShortcutItems(List<BookmarkDataModel> bookmarks) {
        ArrayList<BookmarkDataModel> items = new ArrayList<>();
        if (bookmarks == null || bookmarks.isEmpty()) {
            return items;
        }

        int limit = Math.min(bookmarks.size(), SHORTCUT_LIMIT);
        for (int i = 0; i < limit; i++) {
            items.add(bookmarks.get(i));
        }

        if (bookmarks.size() > SHORTCUT_LIMIT) {
            BookmarkDataModel overflow = new BookmarkDataModel();
            overflow.setId(-1);
            overflow.setAddress("more://bookmarks");
            overflow.setTitle("More");
            items.add(overflow);
        }

        return items;
    }

    private void openBookmark(BookmarkDataModel bookmark) {
        if (bookmark == null || TextUtils.isEmpty(bookmark.getAddress())) {
            return;
        }
        String address = bookmark.getAddress();
        if (!address.startsWith("http://") && !address.startsWith("https://")) {
            address = "https://" + address;
        }
        webViewModel.setUrlAddress(address);
        webViewModel.setWebRequest(true);
    }
}
