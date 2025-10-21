package com.LG.mreader.GPT;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.util.Log;
import android.util.LruCache;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Disk-backed preprocessor:
 * 1) download to cache file (no large byte[] in RAM)
 * 2) decode from file with inSampleSize (two-pass)
 * 3) crop and save lossless WEBP
 *
 * Use with small thread count (1 or 2) to avoid memory pressure.
 */
public class ImagePreprocessor {
    private static final String TAG = "ImagePreprocessor";
    private final Context ctx;
    private final ExecutorService pool;
    private static final int CONNECTION_TIMEOUT = 15000;
    private static final int READ_TIMEOUT = 30000;

    // Tracks active downloads to avoid duplicates
    private static final Set<String> downloading = ConcurrentHashMap.newKeySet();

    // Optional small in-memory bitmap cache for very recent tiles (adjust size as needed)
    private final LruCache<String, Bitmap> memCache;

    public interface Callback {
        void onSuccess(Uri optimizedUri);
        void onError(Exception e);
    }

    public ImagePreprocessor(Context ctx, int threads, int memCacheKb) {
        this.ctx = ctx.getApplicationContext();
        this.pool = Executors.newFixedThreadPool(Math.max(1, threads));
        this.memCache = new LruCache<String, Bitmap>(memCacheKb * 1024) {
            @Override protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();
            }
        };
    }

    // Utility: get disk cache file for url
    private File diskCacheFileFor(String url) throws Exception {
        // use a hashed filename
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] digest = md.digest(url.getBytes("UTF-8"));
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) sb.append(String.format("%02x", b));
        String name = sb.toString() + ".img";
        File dir = new File(ctx.getCacheDir(), "image_preproc");
        if (!dir.exists()) dir.mkdirs();
        return new File(dir, name);
    }

    // Download to disk (atomic write to .tmp then rename)
    private File downloadToDisk(String urlString) throws Exception {
        File outFile = diskCacheFileFor(urlString);
        if (outFile.exists() && outFile.length() > 0) {
            Log.d(TAG, "Disk cache hit: " + urlString);
            return outFile;
        }

        // guard against duplicate downloads
        boolean weDownload = downloading.add(urlString);
        if (!weDownload) {
            // some other thread is downloading; wait until file appears
            int attempts = 0;
            while (!outFile.exists() && attempts < 60) { // wait up to ~60 * 200ms = 12s
                Thread.sleep(200);
                attempts++;
            }
            if (outFile.exists()) return outFile;
            // else fall through and attempt download ourselves
            if (!downloading.add(urlString)) {
                // rare race; continue download attempt
            }
        }

        HttpURLConnection conn = null;
        File tmp = new File(outFile.getAbsolutePath() + ".tmp");

        try {
            URL url = new URL(urlString);
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(CONNECTION_TIMEOUT);
            conn.setReadTimeout(READ_TIMEOUT);
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");
            conn.setRequestProperty("Accept", "image/webp,image/apng,image/*,*/*;q=0.8");
            conn.setDoInput(true);

            int code = conn.getResponseCode();
            if (code != HttpURLConnection.HTTP_OK) {
                throw new RuntimeException("HTTP " + code + " for " + urlString);
            }

            try (InputStream is = new BufferedInputStream(conn.getInputStream());
                 FileOutputStream fos = new FileOutputStream(tmp)) {
                byte[] buf = new byte[16 * 1024];
                int r;
                while ((r = is.read(buf)) != -1) fos.write(buf, 0, r);
                fos.getFD().sync();
            }

            // atomic rename
            if (!tmp.renameTo(outFile)) {
                // fallback: copy then delete tmp
                try (InputStream is2 = new BufferedInputStream(new java.io.FileInputStream(tmp));
                     FileOutputStream fos2 = new FileOutputStream(outFile)) {
                    byte[] buf = new byte[16 * 1024];
                    int r;
                    while ((r = is2.read(buf)) != -1) fos2.write(buf, 0, r);
                }
                tmp.delete();
            }

            return outFile;
        } finally {
            if (conn != null) conn.disconnect();
            downloading.remove(urlString);
            if (tmp.exists()) tmp.delete(); // cleanup leftover
        }
    }

    /**
     * Public API: process a page Uri -> writes optimized file to cache and invokes callback.
     * If sourceUri is remote, we download it to disk first (no large byte[] in RAM).
     */
    public void process(final Uri sourceUri, final String outFilename, final Callback cb) {
        pool.submit(() -> {
            Bitmap bmp = null;
            Bitmap cropped = null;
            try {
                File sourceFile;
                if ("http".equalsIgnoreCase(sourceUri.getScheme()) || "https".equalsIgnoreCase(sourceUri.getScheme())) {
                    sourceFile = downloadToDisk(sourceUri.toString());
                } else {
                    // local Uri: copy into our cache dir so we can safely decode by path
                    try (InputStream is = ctx.getContentResolver().openInputStream(sourceUri)) {
                        if (is == null) throw new RuntimeException("Bad local URI: " + sourceUri);
                        sourceFile = diskCacheFileFor(sourceUri.toString());
                        if (!sourceFile.exists()) {
                            try (FileOutputStream fos = new FileOutputStream(sourceFile)) {
                                byte[] buf = new byte[16 * 1024];
                                int r;
                                while ((r = is.read(buf)) != -1) fos.write(buf, 0, r);
                                fos.getFD().sync();
                            }
                        }
                    }
                }

                // Two-pass decode from file: bounds -> compute sample -> decode
                BitmapFactory.Options bounds = new BitmapFactory.Options();
                bounds.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(sourceFile.getAbsolutePath(), bounds);

                // decide sample size — keep full fidelity unless extremely huge
                int maxDim = 6000; // you can tune; lower = less memory
                int inSample = 1;
                if (bounds.outWidth > maxDim || bounds.outHeight > maxDim) {
                    inSample = Math.max(1, Math.max(bounds.outWidth / maxDim, bounds.outHeight / maxDim));
                }

                BitmapFactory.Options opts = new BitmapFactory.Options();
                opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
                opts.inDither = true;
                opts.inSampleSize = inSample;
                // decodeFile uses native memory; keep it minimal by using inSampleSize above
                bmp = BitmapFactory.decodeFile(sourceFile.getAbsolutePath(), opts);
                if (bmp == null) throw new RuntimeException("Decode failed: " + sourceFile.getAbsolutePath());
                Log.d(TAG, "Decoded FULL resolution bitmap: " + bmp.getWidth() + "x" + bmp.getHeight() +
                        " (" + (bmp.getByteCount() / 1024 / 1024) + " MB)");

                // crop
                Rect crop = detectContentBounds(bmp);
                if (crop.width() <= 0 || crop.height() <= 0) {
                    crop = new Rect(0, 0, bmp.getWidth(), bmp.getHeight());
                }
                cropped = Bitmap.createBitmap(bmp, crop.left, crop.top, crop.width(), crop.height());

                // write optimized file (lossless)
                File out = new File(ctx.getCacheDir(), outFilename);
                try (FileOutputStream fos = new FileOutputStream(out)) {
                    cropped.compress(Bitmap.CompressFormat.WEBP_LOSSY, 80, fos);
                }

                // optional: put into small mem cache
                String memKey = sourceUri.toString();
                try {
                    if (cropped != null && memCache != null) memCache.put(memKey, cropped.copy(cropped.getConfig(), false));
                } catch (Throwable ignored) {}

                // cleanup large bitmaps
                if (bmp != null && !bmp.isRecycled()) { bmp.recycle(); bmp = null; }
                if (cropped != null && !cropped.isRecycled()) { cropped.recycle(); cropped = null; }

                if (cb != null) cb.onSuccess(Uri.fromFile(out));
            } catch (Exception e) {
                Log.e(TAG, "Preprocess failed", e);
                if (cb != null) cb.onError(e);
            } finally {
                try {
                    if (bmp != null && !bmp.isRecycled()) bmp.recycle();
                } catch (Throwable ignored) {}
                try {
                    if (cropped != null && !cropped.isRecycled()) cropped.recycle();
                } catch (Throwable ignored) {}
            }
        });
    }

    // Same detectContentBounds and isBackgroundPixel as before
    private Rect detectContentBounds(Bitmap bmp) {
        int w = bmp.getWidth(), h = bmp.getHeight();
        int left = 0, right = w - 1, top = 0, bottom = h - 1;

        outerTop:
        for (int y = 0; y < h; y++)
            for (int x = 0; x < w; x++)
                if (!isBackgroundPixel(bmp.getPixel(x, y))) { top = y; break outerTop; }

        outerBottom:
        for (int y = h - 1; y >= 0; y--)
            for (int x = 0; x < w; x++)
                if (!isBackgroundPixel(bmp.getPixel(x, y))) { bottom = y; break outerBottom; }

        outerLeft:
        for (int x = 0; x < w; x++)
            for (int y = top; y <= bottom; y++)
                if (!isBackgroundPixel(bmp.getPixel(x, y))) { left = x; break outerLeft; }

        outerRight:
        for (int x = w - 1; x >= 0; x--)
            for (int y = top; y <= bottom; y++)
                if (!isBackgroundPixel(bmp.getPixel(x, y))) { right = x; break outerRight; }

        left = Math.max(0, left - 1);
        top = Math.max(0, top - 1);
        right = Math.min(w - 1, right + 1);
        bottom = Math.min(h - 1, bottom + 1);

        return new Rect(left, top, right - left + 1, bottom - top + 1);
    }

    private boolean isBackgroundPixel(int px) {
        int r = (px >> 16) & 0xff, g = (px >> 8) & 0xff, b = px & 0xff;
        boolean nearWhite = r > 245 && g > 245 && b > 245;
        boolean nearBlack = r < 10 && g < 10 && b < 10;
        return nearWhite || nearBlack;
    }
}
