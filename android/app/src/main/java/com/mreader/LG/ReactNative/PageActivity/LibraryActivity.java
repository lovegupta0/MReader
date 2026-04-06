package com.mreader.LG.ReactNative.PageActivity;

import android.os.Bundle;

import com.facebook.react.ReactActivity;
import com.facebook.react.ReactActivityDelegate;
import com.mreader.LG.Service.LibraryService;
import com.mreader.LG.Utility.JsonConverter;

public class LibraryActivity extends ReactActivity {
    private LibraryService libraryService;

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
                libraryService = LibraryService.getInstance(LibraryActivity.this);
                Bundle launchOptions = new Bundle();
                launchOptions.putString("initialRouteName", "Library");
                launchOptions.putString(
                        "data",
                        JsonConverter.listToJsonSafe(libraryService.getSortedLibrary())
                );
                return launchOptions;
            }
        };
    }
}
