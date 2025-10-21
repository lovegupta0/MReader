package com.LG.mreader.DataModel;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.LG.mreader.PoolService.ThreadPoolManager;
import com.LG.mreader.Utility.ImagePreprocessor;

import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class Page implements Parcelable {
    private static final String TAG = "Page";

    private String id;
    private Uri sourceUri;     // original source (file:// or content:// or http://)
    private Uri optimizedUri=null;  // after preprocessing (local cached WEBP)
    private int width;
    private int height;

    private Future<Uri> thread;

    public Page(Uri sourceUri) {
        this.sourceUri = sourceUri;
        setId(sourceUri.toString());
        Log.d(TAG,"Processing: "+id);
        ImagePreprocessor imagePreprocessor=new ImagePreprocessor();
        String out ="cache_" + getId() + ".webp";
        thread=imagePreprocessor.processAsync(getSourceUri(), out, new ImagePreprocessor.Callback() {
            @Override
            public void onSuccess(Uri optimizedUri) {
                setOptimizedUri(optimizedUri);
            }

            @Override
            public void onError(Exception e) {
                if(e!=null){
                    Log.e(TAG,e.getMessage());
                }
            }
        });
    }
    public Page(String uri){
        this(Uri.parse(uri));
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

    public String getId() {
        return id;
    }

    public Uri getSourceUri() {
        return sourceUri;
    }

    public void setSourceUri(Uri sourceUri) {
        this.sourceUri = sourceUri;
    }

    public Uri getOptimizedUri() {
        if(optimizedUri==null && thread!=null && !thread.isDone()){
            try {
                optimizedUri=thread.get();
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return optimizedUri;
    }
    private void setId(String id){
        this.id=id.replace(":","").replace("//","/").replace("/","_");
    }
    private void setOptimizedUri(Uri optimizedUri) {
        this.optimizedUri = optimizedUri;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}