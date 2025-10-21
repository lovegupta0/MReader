package com.LG.mreader.Middleware;

import android.content.Context;

import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.module.AppGlideModule;

@GlideModule
public final class MyAppGlideModule extends AppGlideModule {
    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        // Prefer RGB_565 where possible to reduce memory use (careful with alpha)
        builder.setDefaultRequestOptions(
                new com.bumptech.glide.request.RequestOptions()
                        .format(DecodeFormat.PREFER_ARGB_8888)
        );
        // Further tuning of memory/disk cache can be added here.
    }

    @Override
    public boolean isManifestParsingEnabled() {
        return false;
    }
}
