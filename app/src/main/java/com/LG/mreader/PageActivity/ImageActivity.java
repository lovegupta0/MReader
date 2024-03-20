package com.LG.mreader.PageActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.LG.mreader.Adapter.ImageAdapter;
import com.LG.mreader.AppRepository.AppRepository;
import com.LG.mreader.DataModel.ViewImageDataModel;
import com.LG.mreader.MainActivity;
import com.LG.mreader.R;
import com.LG.mreader.ViewModel.ImageViewModel;

import java.util.ArrayList;
import java.util.List;

public class ImageActivity extends AppCompatActivity {
    private AppRepository repo;
    private RecyclerView imgRecView;
    private ImageViewModel imageViewModel;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        repo=new AppRepository(this);
        imageViewModel=new ViewModelProvider(this).get(ImageViewModel.class);
        imgRecView=findViewById(R.id.recView);
        imgRecView.setLayoutManager(new LinearLayoutManager(this));
        ImageAdapter imgAdpt=new ImageAdapter(this);
        imgRecView.setAdapter(imgAdpt);

        imageViewModel.getImgSrc().observe(this, new Observer<List<ViewImageDataModel>>() {
            @Override
            public void onChanged(List<ViewImageDataModel> viewImageDataModels) {
                imgAdpt.setImageUrls(viewImageDataModels);
                Log.d("hello","Size comming from DB: "+Integer.toString(viewImageDataModels.size()));

            }
        });
        /*
        imgRecView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                    LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                    // Check if the last visible item is the last item in the dataset
                    int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
                    int totalItemCount = layoutManager.getItemCount();

                    if (lastVisibleItemPosition == totalItemCount - 1) {
                        // Scroll to the top smoothly
                        recyclerView.stopScroll();
                    }

            }
        });*/
        RecyclerView.ItemDecoration itemDecoration;

        while (imgRecView.getItemDecorationCount() > 0
                &&(itemDecoration = imgRecView.getItemDecorationAt(0)) != null) {
            imgRecView.removeItemDecoration(itemDecoration);
        }

    }

    @Override
    protected void onDestroy() {
        repo.clearViewImage();
        imageViewModel.setShowImageView(false);
        super.onDestroy();
    }
}