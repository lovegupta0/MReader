package com.LG.mreader.Utility;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.util.Log;

import com.LG.mreader.Common.ImageDownloader;
import com.LG.mreader.PoolService.ThreadPoolManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public class ImagePreprocessor {
    private static final String TAG = "ImagePreprocessor";

    private final CacheManager cacheManager;
    private final ImageDownloader imageDownloader;
    private final ThreadPoolManager threadPoolManager;
    private final Context ctx;

    public interface Callback {
        void onSuccess(Uri optimizedUri);
        void onError(Exception e);
    }

    public ImagePreprocessor() {
        this.cacheManager = CacheManager.getInstance();
        this.imageDownloader = new ImageDownloader();
        this.threadPoolManager = ThreadPoolManager.getInstance();
        this.ctx = ContextManager.getInstance().getWebFragmentContext();
    }

    // === ASYNC VARIANT ===
    public Future<Uri> processAsync(final Uri sourceUri, final String outFilename, final Callback cb) {
        return threadPoolManager.submitTask((Callable<Uri>) () -> processBlocking(sourceUri, outFilename, cb));
    }

    // === BLOCKING VARIANT ===
    public Uri processBlocking(final Uri sourceUri, final String outFilename, final Callback cb) {
        Bitmap bmp = null;
        Bitmap cropped = null;
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
            Log.d(TAG, "Decoded bitmap: " + bmp.getWidth() + "x" + bmp.getHeight());

            // crop
            Rect crop = detectContentBounds(bmp);
            if (crop.width() <= 0 || crop.height() <= 0)
                crop = new Rect(0, 0, bmp.getWidth(), bmp.getHeight());

            cropped = Bitmap.createBitmap(bmp, crop.left, crop.top, crop.width(), crop.height());

            // write optimized WEBP
            File out = new File(ctx.getCacheDir(), outFilename);
            try (FileOutputStream fos = new FileOutputStream(out)) {
                cropped.compress(Bitmap.CompressFormat.WEBP_LOSSY, 85, fos); // slightly better quality
            }

            // cache bitmap (optional)
            String memKey = sourceUri.toString();
            if (cropped != null && cacheManager != null)
                cacheManager.putBitmap(memKey, cropped.copy(cropped.getConfig(), false));

            Uri resultUri = Uri.fromFile(out);
            if (cb != null) cb.onSuccess(resultUri);

            return resultUri;

        } catch (Exception e) {
            Log.e(TAG, "Preprocess failed", e);
            if (cb != null) cb.onError(e);
            return null;
        } finally {
            if (bmp != null && !bmp.isRecycled()) bmp.recycle();
            if (cropped != null && !cropped.isRecycled()) cropped.recycle();
        }
    }

    // === Helper crop methods ===
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
