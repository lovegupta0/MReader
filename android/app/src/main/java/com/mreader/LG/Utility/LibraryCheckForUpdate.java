package com.mreader.LG.Utility;

import android.util.Log;

import com.mreader.LG.Common.Converters;
import com.mreader.LG.Common.PageDataExtracter;
import com.mreader.LG.Common.WebRequest;
import com.mreader.LG.DataModel.LibraryDataModel;
import com.mreader.LG.PoolService.CentralThreadPool;
import com.mreader.LG.Service.LibraryService;

import java.time.LocalDateTime;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

public class LibraryCheckForUpdate {
    private static final String TAG = "LibraryCheckForUpdate";

    private final LibraryService libraryService;
    private final HeadlessBrowser headlessBrowser;
    private final Queue<String> pendingUrls = new ArrayDeque<>();
    private boolean isRunning;
    private boolean blockedForSession;
    private ThreadsPoolManager threadsPoolManager;

    public static final String payload =
            "(function() {"
                    + "var result;"
                    + "var cover;"
                    + "var coverDiv = document.querySelector('.cover');"
                    + "if (coverDiv) {"
                    + "var img = coverDiv.querySelector('img');"
                    + "if (img) {"
                    + "cover = img.getAttribute('data-src') || img.src;"
                    + "}"
                    + "}"
                    + "var data;"
                    + "var chapter = document.querySelector('.chapter-number');"
                    + "if (chapter) {"
                    + "data = chapter.innerText.trim();"
                    + "}"
                    + "result=cover;"
                    + "var pos = data.indexOf(' ');"
                    + "if (pos !== -1) {"
                    + "result=result+','+data.substring(0, pos);"
                    + "result=result+','+data.substring(pos + 1);"
                    + "}"
                    + "return result;"
                    + "})();";

    public LibraryCheckForUpdate() {
        libraryService = LibraryService.getInstance();
        headlessBrowser = HeadlessBrowser.getInstance();
        threadsPoolManager=CentralThreadPool.getInstance();
    }

    public void checkForUpdate() {
        synchronized (this) {
            if (blockedForSession) {
                Log.w(TAG, "Skipping update check because Cloudflare already blocked this session");
                return;
            }
            if (isRunning) {
                Log.d(TAG, "Update check already running; skipping duplicate request");
                return;
            }

            pendingUrls.clear();
            libraryService.getLibrary().forEach(libraryItem -> {
                String pageUrl = libraryItem.getPageUrl();
                if (pageUrl != null && !pageUrl.isEmpty() && !isTodayUpdated(libraryItem.getLastUpdateddate()) ) {
                    pendingUrls.offer(pageUrl);
                }
            });
            isRunning = true;
        }

        processNext();
    }

    private boolean isTodayUpdated(String time) {

        LocalDateTime localDateTime = Converters.toLocalDateTime(time);
        LocalDateTime now = LocalDateTime.now();
        return localDateTime.toLocalDate().equals(now.toLocalDate());
    }

    private void processNext() {
        String pageUrl;
        synchronized (this) {
            pageUrl = pendingUrls.poll();
            if (pageUrl == null) {
                isRunning = false;
                Log.d(TAG, "Library update queue finished");
                return;
            }
        }

        try {
            headlessBrowser.fetchData(pageUrl, payload, new HeadlessBrowser.callback() {
                @Override
                public void onSuccess(String extractedData) {
                    if (extractedData == null || extractedData.isEmpty()) {
                        Log.w(TAG, "Empty update response for " + pageUrl);
                        processNext();
                    } else if ("[\"BLOCKED\"]".equals(extractedData)) {
                        Log.w(TAG, "Cloudflare blocked update check for " + pageUrl);
                        abortQueueForSession();
                    } else {
                        Log.d(TAG, "Fetched update data for " + pageUrl + ": " + extractedData);
                        threadsPoolManager.submitTask(()->updateLibrary(extractedData,pageUrl));
                        processNext();
                    }
                }

                @Override
                public void onError(Exception e) {
                    Log.e(TAG, "Update check failed for " + pageUrl, e);
                    processNext();
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Unable to start update check for " + pageUrl, e);
            processNext();
        }
    }

    public synchronized boolean isRunning() {
        return isRunning;
    }

    private void abortQueueForSession() {
        synchronized (this) {
            blockedForSession = true;
            pendingUrls.clear();
            isRunning = false;
        }
        Log.w(TAG, "Stopped library update queue after Cloudflare block");
    }

    public void updateOnStart(){
        threadsPoolManager.submitTask(()->{
            libraryService.getLibrary().forEach(libraryItem -> {
                String pageUrl = libraryItem.getPageUrl();
                String homeUrl=libraryItem.getBaseUrl();
                if(isTodayUpdated(libraryItem.getLastUpdateddate())) return;
                try {
                   List<String> lst= PageDataExtracter.ExtractDataForChapter(WebRequest.fetchPageHTML(pageUrl,homeUrl),homeUrl);
                    libraryItem.setCoverUrl(lst.get(0));
                    libraryItem.setLatestchapter(lst.get(1));
                    libraryItem.setLatestChapterUpdated(lst.get(2));
                    libraryItem.setLastUpdateddate(Converters.fromLocalDateTime(LocalDateTime.now()));
                    libraryService.updateLibrary(libraryItem);
                } catch (Exception e) {
                    Log.d(TAG,e.getMessage());
                }
            });


        });

    }

    private void updateLibrary(String data,String pageUrl){
        data=data.replace("\"","");
        String[] lst=data.split(",");
        List<LibraryDataModel> libraryList=libraryService.getLibraryByUrl(pageUrl);
        if(libraryList.size()>0){
            int pos=data.indexOf("\\n");
            LibraryDataModel library=libraryList.get(0);
            library.setCoverUrl(lst[0]);
            library.setLastUpdateddate(Converters.fromLocalDateTime(LocalDateTime.now()));
            if (lst.length > 1) {
                library.setLatestchapter(lst[1].replace("\\n","").trim());
            }
            if(lst.length>2){
                library.setLatestChapterUpdated(data.substring(pos+3).replace("\\n","").trim());
            }
            libraryService.updateLibrary(library);
        }
    }
}
