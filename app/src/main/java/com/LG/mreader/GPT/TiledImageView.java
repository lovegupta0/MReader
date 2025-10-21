package com.LG.mreader.GPT;



import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.BitmapRegionDecoder;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import androidx.annotation.Nullable;


import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Basic tiled view using BitmapRegionDecoder and a small tile LRU cache.
 * Supports pinch-zoom and basic pan. Not a full map-engine; extend for production.
 */
public class TiledImageView extends View {
    private BitmapRegionDecoder decoder;
    private int imageW, imageH;
    private float scale = 1f, minScale = 1f, maxScale = 4f;
    private float focusX, focusY; // image coords of view center
    private ScaleGestureDetector scaleDetector;
    private ExecutorService executor = Executors.newFixedThreadPool(2);
    private Handler main = new Handler(Looper.getMainLooper());
    private TileCache tileCache;
    private int tileSize = 512;

    // basic pan handling
    private float lastTouchX, lastTouchY;
    private boolean isPanning = false;

    public TiledImageView(Context ctx) { super(ctx); init(ctx); }
    public TiledImageView(Context ctx, @Nullable AttributeSet attrs) { super(ctx, attrs); init(ctx); }

    private void init(Context ctx) {
        scaleDetector = new ScaleGestureDetector(ctx, new ScaleListener());
        tileCache = new TileCache(8 * 1024); // 8MB default
    }

    public void setImageUri(final Uri uri) {
        executor.submit(() -> {
            try (InputStream is = getContext().getContentResolver().openInputStream(uri)) {
                decoder = BitmapRegionDecoder.newInstance(is, false);
                imageW = decoder.getWidth();
                imageH = decoder.getHeight();
                focusX = imageW / 2f;
                focusY = imageH / 2f;
                main.post(this::invalidate);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void setBitmapFallback(Bitmap bmp) {
        // optional fallback (not tiled). For simplicity, not used in main flow.
        if (bmp != null) {
            decoder = null;
            imageW = bmp.getWidth();
            imageH = bmp.getHeight();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (decoder == null) return;

        int vw = getWidth(), vh = getHeight();
        if (vw == 0 || vh == 0) return;

        float halfW = (vw / 2f) / scale;
        float halfH = (vh / 2f) / scale;
        int left = (int) Math.max(0, focusX - halfW);
        int top = (int) Math.max(0, focusY - halfH);
        int right = (int) Math.min(imageW, focusX + halfW);
        int bottom = (int) Math.min(imageH, focusY + halfH);

        int tileLeft = (left / tileSize) * tileSize;
        int tileTop = (top / tileSize) * tileSize;

        for (int y = tileTop; y <= bottom; y += tileSize) {
            for (int x = tileLeft; x <= right; x += tileSize) {
                int tx = x;
                int ty = y;
                int tw = Math.min(tileSize, imageW - tx);
                int th = Math.min(tileSize, imageH - ty);
                String key = tx + "_" + ty + "_" + Math.round(scale * 100);
                Bitmap tile = tileCache.get(key);
                if (tile != null) {
                    float dx = ((tx - (focusX - halfW)) * scale);
                    float dy = ((ty - (focusY - halfH)) * scale);
                    Rect src = new Rect(0, 0, tile.getWidth(), tile.getHeight());
                    Rect dst = new Rect(Math.round(dx), Math.round(dy), Math.round(dx + tile.getWidth() * scale), Math.round(dy + tile.getHeight() * scale));
                    canvas.drawBitmap(tile, src, dst, null);
                } else {
                    requestTile(tx, ty, tw, th, scale, key);
                }
            }
        }
    }

    private void requestTile(int x, int y, int w, int h, float scaleAtRequest, String key) {
        if (tileCache.get(key) != null) return;
        executor.submit(() -> {
            try {
                BitmapFactory.Options opts = new BitmapFactory.Options();
                // compute sample size heuristically
                int sample = Math.max(1, (int) Math.floor((w / (float) getWidth()) / scaleAtRequest));
                opts.inSampleSize = sample;
                Bitmap tile = decoder.decodeRegion(new Rect(x, y, x + w, y + h), opts);
                if (tile != null) tileCache.put(key, tile);
                main.post(this::invalidate);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        scaleDetector.onTouchEvent(ev);
        if (!scaleDetector.isInProgress()) {
            switch (ev.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    lastTouchX = ev.getX();
                    lastTouchY = ev.getY();
                    isPanning = true;
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (isPanning) {
                        float dx = ev.getX() - lastTouchX;
                        float dy = ev.getY() - lastTouchY;
                        // convert movement in view pixels to image coords
                        focusX -= dx / scale;
                        focusY -= dy / scale;
                        clampFocus();
                        lastTouchX = ev.getX();
                        lastTouchY = ev.getY();
                        invalidate();
                    }
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    isPanning = false;
                    break;
            }
        }
        return true;
    }

    private void clampFocus() {
        float halfW = (getWidth() / 2f) / scale;
        float halfH = (getHeight() / 2f) / scale;
        if (focusX - halfW < 0) focusX = halfW;
        if (focusY - halfH < 0) focusY = halfH;
        if (focusX + halfW > imageW) focusX = imageW - halfW;
        if (focusY + halfH > imageH) focusY = imageH - halfH;
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override public boolean onScale(ScaleGestureDetector detector) {
            float factor = detector.getScaleFactor();
            scale *= factor;
            if (scale < minScale) scale = minScale;
            if (scale > maxScale) scale = maxScale;
            clampFocus();
            invalidate();
            return true;
        }
    }

    public void clearCache() { tileCache.clear(); }
}
