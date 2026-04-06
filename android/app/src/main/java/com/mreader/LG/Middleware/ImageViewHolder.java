package com.mreader.LG.Middleware;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.mreader.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

public class ImageViewHolder extends RecyclerView.ViewHolder {
    private final ImageView imageView;
    private String currentUri;

    public ImageViewHolder(View itemView) {
        super(itemView);
        imageView = itemView.findViewById(R.id.long_strip_image);

        imageView.setAdjustViewBounds(true);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

        // Set layout params to match parent width
        ViewGroup.LayoutParams params = imageView.getLayoutParams();
        if (params == null) {
            params = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
        }
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        imageView.setLayoutParams(params);
    }

    public void bind(android.net.Uri uri) {
        currentUri = uri == null ? null : uri.toString();
        ViewGroup.LayoutParams params = imageView.getLayoutParams();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        imageView.setLayoutParams(params);

        // Load at ORIGINAL size with maximum quality
        RequestOptions opts = new RequestOptions()
                .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                .dontTransform()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(false);

        Glide.with(imageView.getContext())
                .asBitmap()
                .load(uri)
                .apply(opts)
                .listener(new RequestListener<android.graphics.Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                Target<android.graphics.Bitmap> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(android.graphics.Bitmap resource, Object model,
                                                   Target<android.graphics.Bitmap> target,
                                                   DataSource dataSource, boolean isFirstResource) {
                        updateHeightForBitmap(resource);
                        return false;
                    }
                })
                .into(imageView);
    }

    private void updateHeightForBitmap(android.graphics.Bitmap resource) {
        imageView.post(() -> {
            int availableWidth = itemView.getWidth();
            if (availableWidth <= 0) {
                availableWidth = imageView.getWidth();
            }
            if (availableWidth <= 0) {
                ViewTreeObserver observer = itemView.getViewTreeObserver();
                observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        ViewTreeObserver currentObserver = itemView.getViewTreeObserver();
                        if (currentObserver.isAlive()) {
                            currentObserver.removeOnGlobalLayoutListener(this);
                        }
                        updateHeightForBitmap(resource);
                    }
                });
                return;
            }

            int imageWidth = resource.getWidth();
            int imageHeight = resource.getHeight();
            if (imageWidth <= 0 || imageHeight <= 0) {
                return;
            }

            float aspectRatio = (float) imageHeight / (float) imageWidth;
            int targetHeight = Math.max(1, Math.round(availableWidth * aspectRatio));

            ViewGroup.LayoutParams params = imageView.getLayoutParams();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = targetHeight;
            imageView.setLayoutParams(params);
            imageView.requestLayout();
        });
    }

    public void clear() {
        Glide.with(imageView.getContext()).clear(imageView);
        imageView.setImageDrawable(null);
        currentUri = null;
    }
}
