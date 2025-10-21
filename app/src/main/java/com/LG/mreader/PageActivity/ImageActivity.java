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
import android.util.Log;

import com.LG.mreader.AppRepository.AppRepository;
import com.LG.mreader.DataModel.Chapter;


import com.LG.mreader.DataModel.Page;
import com.LG.mreader.Middleware.ImageDataContainer;
import com.LG.mreader.R;
import com.LG.mreader.ViewModel.ImageViewModel;

import java.util.ArrayList;
import java.util.List;

public class ImageActivity extends AppCompatActivity {

    private RecyclerView imgRecView;
    private ImageViewModel imageViewModel;
    private LongStripReaderFragment longStripFragment;
    private static String TAG="ImageActivity";
    private boolean isPaged = false;
    private ImageDataContainer container;
    private Chapter chapter;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"intent triggered");
        setContentView(R.layout.activity_integrated_reader);
        imageViewModel = new ViewModelProvider(this).get(ImageViewModel.class);
        container=ImageDataContainer.getInstance();
        Log.d(TAG,"intent started");

        this.chapter =container.getModel();
        this.longStripFragment = new LongStripReaderFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.reader_container, longStripFragment)
                .commit();


        passChapterToFragments();

    }

    @Override
    protected void onDestroy() {
        imageViewModel.setShowImageView(false);
        super.onDestroy();
    }
    private void passChapterToFragments() {
        if (chapter == null || chapter.getPages() == null) return;
        ArrayList<Page> list=new ArrayList<>(chapter.getPages());
        longStripFragment.setPages(list);
    }
}