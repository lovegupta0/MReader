package com.LG.mreader.PageActivity;


import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.LG.mreader.DataModel.Page;
import com.LG.mreader.ViewModel.TiledImageView;
import com.LG.mreader.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

public class PageFragment extends Fragment {
    private static final String ARG_PAGE = "arg_page";
    private Page page;
    private TiledImageView tiledImageView;

    public static PageFragment newInstance(Page page) {
        PageFragment f = new PageFragment();
        Bundle b = new Bundle();
        b.putParcelable(ARG_PAGE,page);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            page = getArguments().getParcelable(ARG_PAGE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_page, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        tiledImageView = view.findViewById(R.id.tiledImageView);
        loadPage();
    }

    private void loadPage() {
        if (page == null) return;
        Uri toLoad = page.getOptimizedUri()!= null ? page.getOptimizedUri() : page.getSourceUri();
        if (toLoad == null) return;

        // Prefer BitmapRegionDecoder for large local files -> call setImageUri if local file
        String scheme = toLoad.getScheme();
        if ("file".equalsIgnoreCase(scheme) || "content".equalsIgnoreCase(scheme)) {
            tiledImageView.setImageUri(toLoad);
        } else {
            Glide.with(this).asBitmap().load(toLoad).into(new CustomTarget<Bitmap>() {
                @Override
                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                    tiledImageView.setBitmapFallback(resource);
                    tiledImageView.invalidate();
                }

                @Override
                public void onLoadCleared(@Nullable Drawable placeholder) {

                }
            });


        }
    }
}
