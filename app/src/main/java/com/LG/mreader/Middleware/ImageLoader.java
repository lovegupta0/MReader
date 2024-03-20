package com.LG.mreader.Middleware;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class ImageLoader implements Target {
    private ImageView imageView;
    public ImageLoader(ImageView imageView){
        this.imageView=imageView;
    }

    @Override
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Log.d("hello","width: "+Integer.toString(width));
        Log.d("hello","height: "+Integer.toString(height));
        imageView.setImageBitmap(bitmap);
    }

    @Override
    public void onBitmapFailed(Exception e, Drawable errorDrawable) {
        Log.d("hello","failed to load img");
    }

    @Override
    public void onPrepareLoad(Drawable placeHolderDrawable) {
        Log.d("hello","loading image");
    }
}
