package com.LG.mreader.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.LG.mreader.DataModel.ViewImageDataModel;
import com.LG.mreader.Middleware.ImageLoader;
import com.LG.mreader.Middleware.ImgUtil;
import com.LG.mreader.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {

    private List<ViewImageDataModel> imageUrls;
    private Context context;

    public ImageAdapter(Context context) {
        this.context = context;
        this.imageUrls =new ArrayList<>();
    }

    public void setImageUrls(List<ViewImageDataModel> imageUrls) {
        this.imageUrls = imageUrls;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.image, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ImageLoader img=new ImageLoader(holder.imageView);
        String imageUrl = imageUrls.get(position).getSrc();

        Picasso.get().load(imageUrl).placeholder(R.drawable.reload_ic).into(holder.imageView, new Callback() {
            @Override
            public void onSuccess() {
                adjustImageViewDimensions(holder.imageView);
            }

            @Override
            public void onError(Exception e) {

            }
        });
    }
    private void adjustImageViewDimensions(ImageView photoView) {
        // Calculate the desired height based on the original image aspect ratio
        int originalWidth = photoView.getDrawable().getIntrinsicWidth();
        int originalHeight = photoView.getDrawable().getIntrinsicHeight();
        int desiredHeight = (int) ((float) photoView.getWidth() / originalWidth * originalHeight);

        // Set the adjusted height to the PhotoView
        ViewGroup.LayoutParams layoutParams = photoView.getLayoutParams();
        layoutParams.height = desiredHeight;
        photoView.setLayoutParams(layoutParams);
    }

    @Override
    public int getItemCount() {
        return imageUrls.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }

}