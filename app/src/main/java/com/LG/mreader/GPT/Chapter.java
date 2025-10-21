package com.LG.mreader.GPT;

import java.io.Serializable;
import java.util.List;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;
import java.util.List;

public class Chapter implements Parcelable {
    public String title;
    public String id;
    public String nextPageUrl;
    public String prevPageUrl;
    public String homeUrl;
    public List<Page> pages;

    public Chapter() {}

    protected Chapter(Parcel in) {
        title = in.readString();
        id = in.readString();
        nextPageUrl = in.readString();
        prevPageUrl = in.readString();
        homeUrl = in.readString();
        pages = new ArrayList<>();
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
}

