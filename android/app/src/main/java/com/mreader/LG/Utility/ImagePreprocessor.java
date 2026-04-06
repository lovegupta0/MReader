package com.mreader.LG.Utility;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import com.mreader.LG.Common.ImageDownloader;
import com.mreader.LG.PoolService.ImageThreadPool;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public class ImagePreprocessor {
    private static final String TAG = "ImagePreprocessor";

    private final CacheManager cacheManager;
    private final ImageDownloader imageDownloader;
    private final ThreadsPoolManager imageThreadPool;
    private final Context ctx;

    public interface Callback {
        void onSuccess(Uri optimizedUri);
        void onError(Exception e);
    }

    public ImagePreprocessor() {
        this.cacheManager = CacheManager.getInstance();
        this.imageDownloader = new ImageDownloader();
        this.imageThreadPool = ImageThreadPool.getInstance();
        this.ctx = ContextManager.getInstance().getWebFragmentContext();
    }

    // === ASYNC VARIANT ===
    public Future<Uri> processAsync(final Uri sourceUri, final String outFilename, final Callback cb) {
        return imageThreadPool.submitTask((Callable<Uri>) () -> processBlocking(sourceUri, outFilename, cb));
    }

    // === BLOCKING VARIANT ===
    public Uri processBlocking(final Uri sourceUri, final String outFilename, final Callback cb) {
        Bitmap bmp = null;
        try {
            File sourceFile;
            if ("http".equalsIgnoreCase(sourceUri.getScheme()) || "https".equalsIgnoreCase(sourceUri.getScheme())) {
                sourceFile = imageDownloader.download(sourceUri.toString());
            } else {
                try (InputStream is = ctx.getContentResolver().openInputStream(sourceUri)) {
                    if (is == null) throw new RuntimeException("Bad local URI: " + sourceUri);
                    sourceFile = imageDownloader.download(sourceUri.toString());
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

            // Decode with sampling
            BitmapFactory.Options bounds = new BitmapFactory.Options();
            bounds.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(sourceFile.getAbsolutePath(), bounds);

            int maxDim = 6000;
            int inSample = 1;
            if (bounds.outWidth > maxDim || bounds.outHeight > maxDim) {
                inSample = Math.max(1, Math.max(bounds.outWidth / maxDim, bounds.outHeight / maxDim));
            }

            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
            opts.inDither = true;
            opts.inSampleSize = inSample;

            bmp = BitmapFactory.decodeFile(sourceFile.getAbsolutePath(), opts);
            if (bmp == null) throw new RuntimeException("Decode failed: " + sourceFile.getAbsolutePath());
            Log.d(TAG, "Decoded resolution bitmap: " + bmp.getWidth() + "x" + bmp.getHeight() +
                    " (" + (bmp.getByteCount() / 1024 / 1024) + " MB)");

            // write optimized WEBP
            File out = new File(ctx.getCacheDir(), outFilename);
            try (FileOutputStream fos = new FileOutputStream(out)) {
                bmp.compress(Bitmap.CompressFormat.WEBP_LOSSY, 95, fos); // slightly better quality
            }

            // cache bitmap (optional)
            String memKey = sourceUri.toString();
            if (bmp != null && cacheManager != null)
                cacheManager.putBitmap(memKey, bmp.copy(bmp.getConfig(), false));

            Uri resultUri = Uri.fromFile(out);
            if (cb != null) cb.onSuccess(resultUri);

            return resultUri;

        } catch (Exception e) {
            Log.e(TAG, "Preprocess failed", e);
            if (cb != null) cb.onError(e);
            return null;
        } finally {
            if (bmp != null && !bmp.isRecycled()) bmp.recycle();
        }
    }
}
