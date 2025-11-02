package com.LG.mreader.DataModel;


import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.LG.mreader.PoolService.PagePool;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class Chapter implements Parcelable {
    private static String TAG="Chapter";
    private String title;
    private String id;
    private String nextPageUrl;
    private String prevPageUrl;
    private String homeUrl;
    private List<Page> pages;
    private String currentUrl;
    private String chapterImageUrl;


    public Chapter() {
        this.id = UUID.randomUUID().toString();
    }
    public Chapter(String title, String nextPageUrl, String prevPageUrl, String homeUrl, List<Page> pages, String currentUrl){
        this();
        this.title = title;
        this.nextPageUrl = nextPageUrl;
        this.prevPageUrl = prevPageUrl;
        this.homeUrl = homeUrl;
        this.pages = pages;
        this.currentUrl=currentUrl;
        Log.d(TAG,"Next Chapter URL: "+nextPageUrl);
        Log.d(TAG,"Prev Chapter URL: "+prevPageUrl);
        Log.d(TAG,"Home URL: "+homeUrl);
        Log.d(TAG,"Current URL: "+currentUrl);
        Log.d(TAG,"Title: "+title);


    }
    public Chapter(String data,String currentUrl){
        this();
        String[] obj = data.split("~#");
        this.homeUrl = obj[0];
         this.title = obj[2];
        String imgSrc = obj[5];
        this.prevPageUrl = obj[4];
        this.nextPageUrl = obj[3];
        PagePool pagePool=PagePool.getInstance();
        pagePool.clear();
        String[] img = imgSrc.split(",");
        List<String> imgList = Arrays.stream(img).filter(e -> e.contains("chapter")).collect(Collectors.toList());
        this.pages = imgList.stream().map(pagePool::getOrCreatePage).collect(Collectors.toList());
        obj=null;
        img=null;
        imgList=null;
        pagePool=null;
        this.currentUrl=currentUrl;
        Log.d(TAG,"Next Chapter URL: "+nextPageUrl);
        Log.d(TAG,"Prev Chapter URL: "+prevPageUrl);
        Log.d(TAG,"Home URL: "+homeUrl);
        Log.d(TAG,"Current URL: "+currentUrl);
        Log.d(TAG,"Title: "+title);


    }

    protected Chapter(Parcel in) {
        title = in.readString();
        id = in.readString();
        nextPageUrl = in.readString();
        prevPageUrl = in.readString();
        homeUrl = in.readString();
        pages = new ArrayList<>();
        currentUrl=in.readString();
        in.readList(pages, Page.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(id);
        dest.writeString(nextPageUrl);
        dest.writeString(prevPageUrl);
        dest.writeString(homeUrl);
        dest.writeList(pages);
        dest.writeString(currentUrl);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Chapter> CREATOR = new Creator<Chapter>() {
        @Override
        public Chapter createFromParcel(Parcel in) {
            return new Chapter(in);
        }

        @Override
        public Chapter[] newArray(int size) {
            return new Chapter[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNextPageUrl() {
        return nextPageUrl;
    }

    public void setNextPageUrl(String nextPageUrl) {
        this.nextPageUrl = nextPageUrl;
    }

    public String getPrevPageUrl() {
        return prevPageUrl;
    }

    public void setPrevPageUrl(String prevPageUrl) {
        this.prevPageUrl = prevPageUrl;
    }

    public String getHomeUrl() {
        return homeUrl;
    }

    public void setHomeUrl(String homeUrl) {
        this.homeUrl = homeUrl;
    }

    public List<Page> getPages() {
        return pages;
    }

    public void setPages(List<Page> pages) {
        this.pages = pages;
    }

    public String getCurrentUrl() {
        return currentUrl;
    }

    public void setCurrentUrl(String currentUrl) {
        this.currentUrl = currentUrl;
    }

    public boolean isNextChapter(){
        return !nextPageUrl.contains("#") || nextPageUrl.length()>0;
    }
    public boolean isPrevChapter(){
        return !prevPageUrl.contains("#") || prevPageUrl.length()>0;
    }
}


