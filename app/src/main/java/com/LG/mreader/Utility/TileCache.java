package com.LG.mreader.Utility;



import android.graphics.Bitmap;
import android.util.LruCache;

public class TileCache {
    private final LruCache<String, Bitmap> cache;

    public TileCache(int maxKb) {
        final int max = maxKb * 1024;
        cache = new LruCache<String, Bitmap>(max) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                // size in bytes
                return value.getByteCount();
            }
        };
    }

    public Bitmap get(String key) { return cache.get(key); }
    public void put(String key, Bitmap bmp) { if (bmp != null) cache.put(key, bmp); }
    public void clear() { cache.evictAll(); }
}
