package com.LG.mreader.Adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.LG.mreader.Middleware.ImageViewHolder;
import com.LG.mreader.DataModel.Page;
import com.LG.mreader.R;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class LongStripAdapter extends RecyclerView.Adapter<ImageViewHolder> {
    private final Context ctx;
    private ArrayList<Page> pages;
    private final LayoutInflater inflater;

    public LongStripAdapter(Context ctx, ArrayList<Page> pages) {
        this.ctx = ctx;
        this.pages = pages == null ? new ArrayList<>() : pages;
        this.inflater = LayoutInflater.from(ctx);

        // IMPORTANT: Disable item change animations to prevent gaps during loading
        setHasStableIds(true);
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.item_long_strip_image, parent, false);
        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        Page p = pages.get(position);
        Uri uri = p.getOptimizedUri()!= null ? p.getOptimizedUri(): p.getSourceUri();
        holder.bind(uri);

        // Prefetch next two pages
        int prefetch = 2;
        for (int i = 1; i <= prefetch; i++) {
            int idx = position + i;
            if (idx < pages.size()) {
                Uri nextUri = pages.get(idx).getOptimizedUri() != null ? pages.get(idx).getOptimizedUri() : pages.get(idx).getSourceUri();
                Glide.with(ctx).load(nextUri).preload();
            }
        }
    }

    @Override
    public int getItemCount() {
        return pages.size();
    }

    // Add stable IDs to prevent unnecessary rebinding
    @Override
    public long getItemId(int position) {
        return pages.get(position).getId().hashCode();
    }

    @Override
    public void onViewRecycled(@NonNull ImageViewHolder holder) {
        super.onViewRecycled(holder);
        holder.clear();
    }

    public void setPages(ArrayList<Page> pages) {
        this.pages = pages == null ? new ArrayList<>() : pages;
        notifyDataSetChanged();
    }
}