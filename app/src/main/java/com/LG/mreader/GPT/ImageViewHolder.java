package com.LG.mreader.GPT;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.LG.mreader.R;
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

        // CRITICAL: Remove adjustViewBounds - it causes gaps!
        imageView.setAdjustViewBounds(false);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);

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

        // Get screen width for proper sizing
        int screenWidth = BitmapUtils.getScreenWidth(imageView.getContext());

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
                        // Calculate proper height based on image aspect ratio
                        imageView.post(() -> {
                            int imageWidth = resource.getWidth();
                            int imageHeight = resource.getHeight();

                            // Calculate height to maintain aspect ratio at screen width
                            float aspectRatio = (float) imageHeight / imageWidth;
                            int targetHeight = (int) (screenWidth * aspectRatio);

                            // Set exact height to prevent gaps
                            ViewGroup.LayoutParams params = imageView.getLayoutParams();
                            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                            params.height = targetHeight;
                            imageView.setLayoutParams(params);
                            imageView.requestLayout();
                        });
                        return false;
                    }
                })
                .into(imageView);
    }

    public void clear() {
        Glide.with(imageView.getContext()).clear(imageView);
        imageView.setImageDrawable(null);
        currentUri = null;
    }
}