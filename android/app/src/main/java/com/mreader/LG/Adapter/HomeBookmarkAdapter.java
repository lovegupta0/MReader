package com.mreader.LG.Adapter;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.load.engine.GlideException;
import com.mreader.LG.DataModel.BookmarkDataModel;
import com.mreader.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HomeBookmarkAdapter extends RecyclerView.Adapter<HomeBookmarkAdapter.ViewHolder> {
    private static final int TYPE_BOOKMARK = 0;
    private static final int TYPE_OVERFLOW = 1;

    private final List<BookmarkDataModel> items = new ArrayList<>();
    private final Listener listener;

    public interface Listener {
        void onBookmarkClicked(BookmarkDataModel bookmark);
        void onOverflowClicked();
    }

    public HomeBookmarkAdapter(Listener listener) {
        this.listener = listener;
    }

    public void submitList(List<BookmarkDataModel> bookmarks) {
        items.clear();
        if (bookmarks != null) {
            items.addAll(bookmarks);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        BookmarkDataModel item = items.get(position);
        return item.getId() == -1 ? TYPE_OVERFLOW : TYPE_BOOKMARK;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home_bookmark, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BookmarkDataModel item = items.get(position);
        if (getItemViewType(position) == TYPE_OVERFLOW) {
            holder.bindOverflow();
            holder.itemView.setOnClickListener(v -> listener.onOverflowClicked());
            return;
        }

        holder.bindBookmark(item);
        holder.itemView.setOnClickListener(v -> listener.onBookmarkClicked(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final FrameLayout iconContainer;
        private final ImageView iconImage;
        private final TextView iconText;
        private final TextView label;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            iconContainer = itemView.findViewById(R.id.iconContainer);
            iconImage = itemView.findViewById(R.id.bookmarkIconImage);
            iconText = itemView.findViewById(R.id.bookmarkIconText);
            label = itemView.findViewById(R.id.bookmarkLabel);
        }

        void bindBookmark(BookmarkDataModel bookmark) {
            label.setText(resolveLabel(bookmark));
            iconImage.setImageDrawable(null);
            iconImage.setVisibility(View.GONE);
            iconText.setVisibility(View.VISIBLE);
            iconText.setText(getInitial(bookmark));
            iconContainer.setBackground(makeCircleBackground(bookmark.getAddress()));

            String faviconUrl = buildFaviconUrl(bookmark.getAddress());
            if (faviconUrl == null) {
                return;
            }

            Glide.with(iconImage.getContext())
                    .load(faviconUrl)
                    .listener(new RequestListener<android.graphics.drawable.Drawable>() {
                        @Override
                        public boolean onLoadFailed(GlideException e, Object model, Target<android.graphics.drawable.Drawable> target, boolean isFirstResource) {
                            showTextFallback();
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(android.graphics.drawable.Drawable resource, Object model, Target<android.graphics.drawable.Drawable> target, com.bumptech.glide.load.DataSource dataSource, boolean isFirstResource) {
                            iconImage.post(() -> {
                                iconImage.setVisibility(View.VISIBLE);
                                iconText.setVisibility(View.GONE);
                                iconContainer.setBackground(makeNeutralCircleBackground());
                            });
                            return false;
                        }

                        private void showTextFallback() {
                            iconImage.post(() -> {
                                iconImage.setVisibility(View.GONE);
                                iconText.setVisibility(View.VISIBLE);
                                iconContainer.setBackground(makeCircleBackground(bookmark.getAddress()));
                            });
                        }
                    })
                    .into(iconImage);
        }

        void bindOverflow() {
            label.setText("More");
            iconText.setText("...");
            iconText.setVisibility(View.VISIBLE);
            iconImage.setVisibility(View.GONE);
            iconContainer.setBackground(makeNeutralCircleBackground());
        }

        private String resolveLabel(BookmarkDataModel bookmark) {
            if (!TextUtils.isEmpty(bookmark.getTitle())) {
                return bookmark.getTitle();
            }
            String host = extractHost(bookmark.getAddress());
            return TextUtils.isEmpty(host) ? "Site" : host;
        }

        private String getInitial(BookmarkDataModel bookmark) {
            String labelText = resolveLabel(bookmark);
            if (TextUtils.isEmpty(labelText)) {
                return "?";
            }
            return labelText.substring(0, 1).toUpperCase(Locale.US);
        }

        private String buildFaviconUrl(String address) {
            String host = extractHost(address);
            if (TextUtils.isEmpty(host)) {
                return null;
            }
            return "https://www.google.com/s2/favicons?sz=128&domain_url=" + host;
        }

        private String extractHost(String address) {
            if (TextUtils.isEmpty(address)) {
                return null;
            }

            String normalized = address;
            if (!normalized.startsWith("http://") && !normalized.startsWith("https://")) {
                normalized = "https://" + normalized;
            }
            Uri uri = Uri.parse(normalized);
            return uri.getHost();
        }

        private GradientDrawable makeCircleBackground(String seed) {
            GradientDrawable drawable = new GradientDrawable();
            drawable.setShape(GradientDrawable.OVAL);
            drawable.setColor(resolveColor(seed));
            return drawable;
        }

        private GradientDrawable makeNeutralCircleBackground() {
            GradientDrawable drawable = new GradientDrawable();
            drawable.setShape(GradientDrawable.OVAL);
            drawable.setColor(Color.parseColor("#E5E7EB"));
            return drawable;
        }

        private int resolveColor(String seed) {
            String[] palette = {
                    "#F97316",
                    "#2563EB",
                    "#059669",
                    "#D97706",
                    "#7C3AED",
                    "#DB2777",
                    "#0F766E"
            };
            int index = Math.abs((seed == null ? 0 : seed.hashCode())) % palette.length;
            return Color.parseColor(palette[index]);
        }
    }
}
