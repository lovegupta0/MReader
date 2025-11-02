package com.LG.mreader.PageActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.LG.mreader.AppRepository.AppRepository;
import com.LG.mreader.DataModel.Chapter;
import com.LG.mreader.DataModel.Page;
import com.LG.mreader.Middleware.ImageDataContainer;
import com.LG.mreader.Middleware.WebviewRepoMiddleware;
import com.LG.mreader.R;
import com.LG.mreader.ViewModel.ImageViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImageActivity extends AppCompatActivity {

    private ImageViewModel imageViewModel;
    private LongStripReaderFragment longStripFragment;
    private static String TAG = "ImageActivity";
    private ImageDataContainer container;
    private Chapter currentChapter;
    private ExecutorService executorService;
    private Handler mainHandler;
    private volatile boolean isCheckingForNewChapter = false;
    private WebviewRepoMiddleware webviewRepoMiddleware;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "Intent triggered");
        setContentView(R.layout.activity_integrated_reader);

        imageViewModel = new ViewModelProvider(this).get(ImageViewModel.class);
        webviewRepoMiddleware=new WebviewRepoMiddleware(imageViewModel);
        container = ImageDataContainer.getInstance();
        executorService = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());

        Log.d(TAG, "Intent started");

        // Get initial chapter
        this.currentChapter = container.getModel();
        this.longStripFragment = new LongStripReaderFragment();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.reader_container, longStripFragment)
                .commit();

        // Set up the listener for loading next chapter
        longStripFragment.setOnLoadNextChapterListener(new LongStripReaderFragment.OnLoadNextChapterListener() {
            @Override
            public void onLoadNextChapter() {
                Log.d(TAG, "Load next chapter triggered");
                checkAndLoadNextChapter();
            }
        });

        // Pass initial chapter to fragment
        passChapterToFragments();
    }

    @Override
    protected void onDestroy() {
        imageViewModel.setShowImageView(false);
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
        super.onDestroy();
    }

    private void passChapterToFragments() {
        if (currentChapter == null || currentChapter.getPages() == null) return;
        ArrayList<Page> list = new ArrayList<>(currentChapter.getPages());
        longStripFragment.setPages(list);
    }

    /**
     * Check if there's a new chapter in the container and append it
     */
    private void checkAndLoadNextChapter() {
        if (isCheckingForNewChapter || !currentChapter.isNextChapter()) {
            Log.d(TAG, "Already checking for next chapter, skipping");
            return;
        }

        isCheckingForNewChapter = true;


        // Check in background thread to avoid blocking UI
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    webviewRepoMiddleware.loadNextChapter();
                    if (!container.isEmpty()) {
                        Chapter nextChapter = container.getModel();

                        if (nextChapter != null && nextChapter.getPages() != null) {
                            Log.d(TAG, "Found next chapter with " + nextChapter.getPages().size() + " pages");

                            // Update on main thread
                            mainHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    appendChapterToView(nextChapter);
                                }
                            });
                        } else {
                            Log.d(TAG, "Next chapter is null or has no pages");
                            mainHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    longStripFragment.resetLoadingState();
                                    Toast.makeText(ImageActivity.this, "No more chapters available", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    } else {
                        Log.d(TAG, "Container is empty, no next chapter available");
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                longStripFragment.resetLoadingState();
                                // Optionally show a message
                                Toast.makeText(ImageActivity.this, "Reached end of available chapters", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error loading next chapter: " + e.getMessage());
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            longStripFragment.resetLoadingState();
                            Toast.makeText(ImageActivity.this, "Error loading next chapter", Toast.LENGTH_SHORT).show();
                        }
                    });
                } finally {
                    isCheckingForNewChapter = false;
                }
            }
        });
    }

    /**
     * Append new chapter pages to the RecyclerView
     */
    private void appendChapterToView(Chapter newChapter) {
        if (newChapter == null || newChapter.getPages() == null || newChapter.getPages().isEmpty()) {
            Log.w(TAG, "Cannot append null or empty chapter");
            longStripFragment.resetLoadingState();
            return;
        }

        ArrayList<Page> newPages = new ArrayList<>(newChapter.getPages());
        longStripFragment.appendPages(newPages);

        // Update current chapter reference (optional - depends on your needs)
        currentChapter = newChapter;

        Log.d(TAG, "Successfully appended " + newPages.size() + " pages from next chapter");
        Toast.makeText(this, "Loaded next chapter", Toast.LENGTH_SHORT).show();
    }
}