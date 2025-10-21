package com.LG.mreader.GPT;



import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

public class BitmapUtils {
    public static int calculateInSampleSize(int origWidth, int origHeight, int reqWidth, int reqHeight) {
        int inSampleSize = 1;
        if (origHeight > reqHeight || origWidth > reqWidth) {
            final int halfHeight = origHeight / 2;
            final int halfWidth = origWidth / 2;
            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    public static int getScreenWidth(Context ctx) {
        DisplayMetrics dm = new DisplayMetrics();
        ((WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }

    public static int getScreenHeight(Context ctx) {
        DisplayMetrics dm = new DisplayMetrics();
        ((WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(dm);
        return dm.heightPixels;
    }
}

