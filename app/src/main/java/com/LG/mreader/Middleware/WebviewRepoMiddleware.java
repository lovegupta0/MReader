package com.LG.mreader.Middleware;

import android.util.Log;

import com.LG.mreader.Common.PageDataExtracter;
import com.LG.mreader.DataModel.Chapter;
import com.LG.mreader.DataModel.Page;
import com.LG.mreader.PoolService.PagePool;
import com.LG.mreader.ViewModel.ImageViewModel;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WebviewRepoMiddleware {
    private final ImageViewModel imageViewModel;
    private final ImageDataContainer imageDataContainer;
    private static String TAG = "WebviewRepoMiddleware";


    public WebviewRepoMiddleware(ImageViewModel imageViewModel) {
        this.imageViewModel = imageViewModel;
        this.imageDataContainer = ImageDataContainer.getInstance();
    }

    public void addviewImageList(String str, String url) {
        try {
            String[] obj = str.split("~#");
            if (obj.length < 6) {
                Log.e(TAG, "Invalid data format: " + str);
                return;
            }
            String hompage = obj[0];
            String title = obj[2];
            String imgSrc = obj[5];
            String prevPage = obj[4];
            String nextPage = obj[3];
            PagePool pagePool=PagePool.getInstance();
            String[] img = imgSrc.split(",");
            List<String> imgList = Arrays.stream(img).filter(e -> e.contains("chapter")).collect(Collectors.toList());
            List<Page> pageList = imgList.stream()
                    .map(pagePool::getOrCreatePage).collect(Collectors.toList());
            Log.d(TAG, "Total: " + imgList.size());
            //Log.d("hello", "Total: " + imgList);
            if (!imgList.isEmpty() && imgList.size()>0) {

                Chapter chapter = new Chapter(title,nextPage,prevPage,hompage,pageList,url);
                imageDataContainer.addImageModel(chapter);
                imageViewModel.setShowImageView(true);
            }
        } catch (Exception e) {
            Log.e(TAG, Objects.toString(e.getMessage(), "Unknown error"));
        }
    }
    /**
     * Get the next page URL from the current chapter
     * This can be called when user scrolls near the end
     */
    public String getNextPageUrl(Chapter currentChapter) {
        if (currentChapter != null) {
            return currentChapter.getNextPageUrl();
        }
        return null;
    }

    /**
     * Check if container has more chapters ready
     */
    public boolean hasMoreChapters() {
        return !imageDataContainer.isEmpty();
    }

    /**
     * Manually trigger loading of next chapter
     * Call this from your WebView when you want to preload the next chapter
     */
    public void loadNextChapter() {
        String nextPageUrl=getNextPageUrl(imageDataContainer.getCurrentChapter());
        String baseUrl=imageDataContainer.getCurrentChapter().getHomeUrl();
        if (nextPageUrl != null && !nextPageUrl.isEmpty()) {
            Log.d(TAG, "Requesting next chapter from URL: " + nextPageUrl);
            Chapter chapter=new Chapter(PageDataExtracter.fetchNextChapter(nextPageUrl,baseUrl),nextPageUrl);
            imageDataContainer.addImageModel(chapter);
        }
    }
}
