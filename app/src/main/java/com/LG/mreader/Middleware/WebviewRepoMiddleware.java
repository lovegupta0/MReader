package com.LG.mreader.Middleware;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.LG.mreader.DataModel.Chapter;
import com.LG.mreader.DataModel.Page;
import com.LG.mreader.PoolService.PagePool;
import com.LG.mreader.PageActivity.IntegratedReaderActivity;
import com.LG.mreader.ViewModel.ImageViewModel;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

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

                Chapter chapter = new Chapter(title,nextPage,prevPage,hompage,pageList,hompage);
                imageDataContainer.addImageModel(chapter);
                imageViewModel.setShowImageView(true);

                /*
                chapter.title = title;
                chapter.id = hompage;
                chapter.pages = pageList;
                chapter.nextPageUrl = nextPage;
                chapter.prevPageUrl = prevPage;
                chapter.homeUrl = hompage;
                ImagePreprocessor pre = new ImagePreprocessor(context, 1,256);
                AtomicInteger remaining = new AtomicInteger(chapter.pages.size());
                for (Page p : chapter.pages) {
                    if(p.optimizedUri!=null) continue;
                    String out = "cache_" + p.id + ".webp";
                    pre.process(p.sourceUri, out, new ImagePreprocessor.Callback() {
                        @Override
                        public void onSuccess(Uri optimizedUri) {
                            p.optimizedUri = optimizedUri;
                            if (remaining.decrementAndGet() == 0) {
                                // all pages done -> launch reader
                                Intent i = new Intent(context, IntegratedReaderActivity.class);
                                i.putExtra("chapter", chapter);
                                context.startActivity(i);
                            }
                        }

                        @Override
                        public void onError(Exception e) {
                            if (remaining.decrementAndGet() == 0) {
                                Intent i = new Intent(context, IntegratedReaderActivity.class);
                                i.putExtra("chapter", chapter);
                                context.startActivity(i);
                            }
                        }
                    });

                }*/
            }
        } catch (Exception e) {
            Log.e(TAG, Objects.toString(e.getMessage(), "Unknown error"));
        }
    }
}
