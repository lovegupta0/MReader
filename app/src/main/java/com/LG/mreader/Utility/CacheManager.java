package com.LG.mreader.Utility;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;

import java.io.File;
import java.security.MessageDigest;

public class CacheManager {

    private final LruCache<String, Bitmap> memCache;
    private final Context ctx;
    private final int MAX_CACHE_BYTES = 200 * 1024 * 1024;
    private static CacheManager instance;
    private ContextManager contextManager;
    private CacheManager() {
        contextManager=ContextManager.getInstance();
        this.ctx= contextManager.getWebFragmentContext();
        this.memCache = new LruCache<String, Bitmap>(MAX_CACHE_BYTES) {
            @Override protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();
            }
        };
    }

    public static CacheManager getInstance(){
        if (instance==null){
            synchronized (CacheManager.class){
                if(instance==null){
                    instance=new CacheManager();
                }
            }
        }
        return instance;
    }

    public  File getDiskCacheFile(String key) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] digest = md.digest(key.getBytes("UTF-8"));
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) sb.append(String.format("%02x", b));
        File dir = new File(ctx.getCacheDir(), "image_cache");
        if (!dir.exists()) dir.mkdirs();
        return new File(dir, sb.toString() + ".img");
    }

    public void putBitmap(String key, Bitmap bmp) {
        if (bmp != null) memCache.put(key, bmp);
    }

    public Bitmap getBitmap(String key) {
        return memCache.get(key);
    }
    public boolean isCached(String key) {
        return memCache.get(key) != null;
    }
}
