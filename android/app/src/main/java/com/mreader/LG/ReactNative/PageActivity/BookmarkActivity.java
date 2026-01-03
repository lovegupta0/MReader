package com.mreader.LG.ReactNative.PageActivity;

import android.os.Bundle;
import android.util.Log;

import com.facebook.react.ReactActivity;
import com.facebook.react.ReactActivityDelegate;

import com.mreader.LG.Service.BookmarkService;
import com.mreader.LG.Utility.JsonConverter;

public class BookmarkActivity extends ReactActivity {
    private BookmarkService bookmarkService;
    private String TAG="BookmarkActivity";

    @Override
    protected String getMainComponentName() {
        // must match AppRegistry.registerComponent('App', ...)
        return "LGMreader";
    }

    @Override
    protected ReactActivityDelegate createReactActivityDelegate() {
        bookmarkService=new BookmarkService();
        // Build initial props for JS root (App)
        Bundle launchOptions = new Bundle();
        launchOptions.putString("initialRouteName", "Bookmarks");
        Log.d(TAG, bookmarkService.getBookmarks().toString());

        launchOptions.putString(
                "data",
                JsonConverter.listToJsonSafe( bookmarkService.getBookmarks())
        );

        return new ReactActivityDelegate(this, getMainComponentName()) {
            @Override
            protected Bundle getLaunchOptions() {
                return launchOptions;
            }
        };
    }
}
