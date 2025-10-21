package com.LG.mreader.Common;

import android.util.Log;

import com.LG.mreader.Utility.CacheManager;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ImageDownloader {
    private CacheManager cacheManager;
    private static final String TAG = "ImageDownloader";
    private static final Set<String> downloading = ConcurrentHashMap.newKeySet();
    private static final int CONNECTION_TIMEOUT = 15000;
    private static final int READ_TIMEOUT = 30000;
    public ImageDownloader() {
        this.cacheManager = CacheManager.getInstance();
    }
    public File download(String urlString) throws Exception {
        File outFile = cacheManager.getDiskCacheFile(urlString);
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

}
