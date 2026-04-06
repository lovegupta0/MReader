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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(null);
    }

    @Override
    protected String getMainComponentName() {
        // must match AppRegistry.registerComponent('App', ...)
        return "LGMreader";
    }

    @Override
    protected ReactActivityDelegate createReactActivityDelegate() {
        return new ReactActivityDelegate(this, getMainComponentName()) {
            @Override
            protected Bundle getLaunchOptions() {
                bookmarkService = new BookmarkService(BookmarkActivity.this);
                Bundle launchOptions = new Bundle();
                launchOptions.putString("initialRouteName", "Bookmarks");
                Log.d(TAG, bookmarkService.getBookmarks().toString());
                launchOptions.putString(
                        "data",
                        JsonConverter.listToJsonSafe(bookmarkService.getBookmarks())
                );
                return launchOptions;
            }
        };
    }
}
