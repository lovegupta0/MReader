package com.LG.mreader.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.LG.mreader.DataModel.ImageModel;
import com.LG.mreader.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {

    private ImageModel imageModel;
    private final Context context;

    public ImageAdapter(Context context) {
        this.context = context;
    }

    public void setImageUrls(ImageModel imageModel) {
        this.imageModel = imageModel;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.image, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get the specific image URL for the current item's position from the stable list.
        String imageUrl = imageModel.getImgList().get(position);
        Log.d("ImageAdapter", "Loading url: " + imageUrl);

        // Use Picasso to load the image, letting it handle sizing automatically.
        Picasso.get()
                .load(imageUrl)
                .placeholder(R.drawable.reload_ic) // Show a placeholder while loading.
                .fit() // Resize the image to fit the ImageView bounds.
                .centerInside() // Scale the image to maintain aspect ratio without cropping.
                .into(holder.imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        // Image loaded successfully. No manual resizing needed.
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.e("ImageAdapter", "Picasso error: " + e.getMessage(), e);
                    }
                });
    }

    @Override
    public int getItemCount() {
        // Return 0 if the model or list is null, otherwise return the stable list size.
        if (imageModel == null || imageModel.getImgList() == null) {
            return 0;
        }
        return imageModel.getImgList().size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}
