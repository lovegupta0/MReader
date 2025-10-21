package com.LG.mreader.GPT;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

public class Page implements Parcelable {
    public String id;
    public Uri sourceUri;     // original source (file:// or content:// or http://)
    public Uri optimizedUri=null;  // after preprocessing (local cached WEBP)
    public int width;
    public int height;

    public Page() {}

    public Page(String id, Uri sourceUri) {
        this.id = id;
        this.sourceUri = sourceUri;
    }

    // Constructor for Parcel
    protected Page(Parcel in) {
        id = in.readString();
        sourceUri = in.readParcelable(Uri.class.getClassLoader());
        optimizedUri = in.readParcelable(Uri.class.getClassLoader());
        width = in.readInt();
        height = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeParcelable(sourceUri, flags);
        dest.writeParcelable(optimizedUri, flags);
        dest.writeInt(width);
        dest.writeInt(height);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Page> CREATOR = new Creator<Page>() {
        @Override
        public Page createFromParcel(Parcel in) {
            return new Page(in);
        }

        @Override
        public Page[] newArray(int size) {
            return new Page[size];
        }
    };
}
