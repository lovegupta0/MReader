package com.mreader.LG.PageActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.mreader.LG.Adapter.LongStripAdapter;
import com.mreader.LG.DataModel.Page;
import com.mreader.LG.Middleware.ImageDataContainer;
import com.mreader.LG.ViewModel.FloatButtonViewModel;
import com.mreader.R;

import java.util.ArrayList;

public class LongStripReaderFragment extends Fragment {
    private static final String TAG = "LongStripReaderFragment";
    private static final long BOTTOM_RECHECK_DELAY_MS = 180L;
    private RecyclerView recyclerView;
    private ExtendedFloatingActionButton fabAdd;
    private LongStripAdapter adapter;
    private ArrayList<Page> pages = new ArrayList<>();
    private OnLoadNextChapterListener loadNextChapterListener;
    private FloatButtonViewModel floatButtonViewModel;
    private boolean isLoadingNextChapter = false;
    private final Handler scrollHandler = new Handler(Looper.getMainLooper());
    private final Runnable bottomCheckRunnable = new Runnable() {
        @Override
        public void run() {
            if (!isAdded() || recyclerView == null || adapter == null) {
                return;
            }

            int totalItemCount = adapter.getItemCount();
            if (totalItemCount == 0 || isLoadingNextChapter) {
                return;
            }

            boolean isAtBottom = !recyclerView.canScrollVertically(1);
            int scrollOffset = recyclerView.computeVerticalScrollOffset();
            int scrollExtent = recyclerView.computeVerticalScrollExtent();
            int scrollRange = recyclerView.computeVerticalScrollRange();
            boolean hasReachedScrollableEnd = scrollOffset + scrollExtent >= scrollRange - 8;

            if (isAtBottom || hasReachedScrollableEnd) {
                Log.d(TAG, "Reached end of strip, triggering next chapter load");
                isLoadingNextChapter = true;
                if (loadNextChapterListener != null) {
                    loadNextChapterListener.onLoadNextChapter();
                }
            }
        }
    };



    public interface OnLoadNextChapterListener {
        void onLoadNextChapter();
    }

    public LongStripReaderFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_long_strip_reader, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        recyclerView = view.findViewById(R.id.long_strip_recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        fabAdd = view.findViewById(R.id.fabAdd);
        adapter = new LongStripAdapter(requireContext(), pages);
        recyclerView.setAdapter(adapter);
        floatButtonViewModel= new ViewModelProvider(requireActivity()).get(FloatButtonViewModel.class);
        fabAdd.setOnClickListener(v -> {
            floatButtonViewModel.floatButtonAction();
        });
        floatButtonViewModel.checkForVisibility();
        floatButtonViewModel.getShowFloatButton().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean){
                    fabAdd.show();
                }
                else{
                    fabAdd.hide();
                }
            }
        });
        // Add scroll listener to detect when near the end
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && !isLoadingNextChapter) {
                    scrollHandler.removeCallbacks(bottomCheckRunnable);
                    scrollHandler.postDelayed(bottomCheckRunnable, BOTTOM_RECHECK_DELAY_MS);
                }
            }
        });

    }

    public void setPages(ArrayList<Page> pages) {
        this.pages = pages == null ? new ArrayList<>() : pages;
        if (adapter != null) {
            adapter.setPages(this.pages);
        }
    }

    /**
     * Append new pages to the existing list without replacing
     */
    public void appendPages(ArrayList<Page> newPages) {
        if (newPages == null || newPages.isEmpty()) {
            Log.w(TAG, "Attempted to append null or empty pages");
            isLoadingNextChapter = false;
            return;
        }

        int oldSize = this.pages.size();
        this.pages.addAll(newPages);

        if (adapter != null) {
            adapter.notifyItemRangeInserted(oldSize, newPages.size());
            Log.d(TAG, "Appended " + newPages.size() + " pages. Total: " + this.pages.size());
        }

        // Reset loading flag
        isLoadingNextChapter = false;
    }

    /**
     * Set listener for next chapter loading
     */
    public void setOnLoadNextChapterListener(OnLoadNextChapterListener listener) {
        this.loadNextChapterListener = listener;
    }

    /**
     * Reset loading state (call this if loading failed)
     */
    public void resetLoadingState() {
        isLoadingNextChapter = false;
        scrollHandler.removeCallbacks(bottomCheckRunnable);
    }

    /**
     * Get current page count
     */
    public int getPageCount() {
        return pages.size();
    }

    @Override
    public void onDestroy(){
        scrollHandler.removeCallbacks(bottomCheckRunnable);
        super.onDestroy();
        ImageDataContainer.getInstance().clear();

    }

}
