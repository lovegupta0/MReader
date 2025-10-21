package com.LG.mreader.DataModel;


import java.io.Serializable;
import java.util.List;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Chapter implements Parcelable {
    private String title;
    private String id;
    private String nextPageUrl;
    private String prevPageUrl;
    private String homeUrl;
    private List<Page> pages;
    private String currentUrl;

    public Chapter() {}
    public Chapter(String title, String nextPageUrl, String prevPageUrl, String homeUrl, List<Page> pages, String currentUrl){
        this.title = title;
        this.id = UUID.randomUUID().toString();
        this.nextPageUrl = nextPageUrl;
        this.prevPageUrl = prevPageUrl;
        this.homeUrl = homeUrl;
        this.pages = pages;
        this.currentUrl=currentUrl;
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
}


