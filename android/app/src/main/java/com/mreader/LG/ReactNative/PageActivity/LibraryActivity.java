package com.mreader.LG.ReactNative.PageActivity;

import android.os.Bundle;

import com.facebook.react.ReactActivity;
import com.facebook.react.ReactActivityDelegate;
import com.mreader.LG.Service.LibraryService;
import com.mreader.LG.Utility.JsonConverter;

public class LibraryActivity extends ReactActivity {
    private LibraryService libraryService;
    @Override
    protected String getMainComponentName() {
        // must match AppRegistry.registerComponent('App', ...)
        return "LGMreader";
    }

    @Override
    protected ReactActivityDelegate createReactActivityDelegate() {

        libraryService=LibraryService.getInstance();

        Bundle launchOptions = new Bundle();
        launchOptions.putString("initialRouteName", "Library");


        launchOptions.putString(
                "data",
                JsonConverter.listToJsonSafe(libraryService.getSortedLibrary())
        );

        return new ReactActivityDelegate(this, getMainComponentName()) {
            @Override
            protected Bundle getLaunchOptions() {
                return launchOptions;
            }
        };
    }
}
