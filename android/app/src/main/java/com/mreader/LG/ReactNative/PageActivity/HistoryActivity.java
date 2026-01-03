package com.mreader.LG.ReactNative.PageActivity;

import android.os.Bundle;

import com.facebook.react.ReactActivity;
import com.facebook.react.ReactActivityDelegate;
import com.mreader.LG.Middleware.HistoryService;
import com.mreader.LG.Utility.JsonConverter;

public class HistoryActivity extends ReactActivity {
    HistoryService historyMiddleware;


    @Override
    protected String getMainComponentName() {
        // must match AppRegistry.registerComponent('App', ...)
        return "LGMreader";
    }

    @Override
    protected ReactActivityDelegate createReactActivityDelegate() {
        historyMiddleware= HistoryService.getInstance();
        // Build initial props for JS root (App)
        Bundle launchOptions = new Bundle();
        launchOptions.putString("initialRouteName", "History");


        launchOptions.putString(
                "data",
                JsonConverter.listToJsonSafe( historyMiddleware.getAllHistory())
        );

        return new ReactActivityDelegate(this, getMainComponentName()) {
            @Override
            protected Bundle getLaunchOptions() {
                return launchOptions;
            }
        };
    }
}
