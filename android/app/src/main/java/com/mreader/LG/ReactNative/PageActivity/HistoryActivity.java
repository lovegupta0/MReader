package com.mreader.LG.ReactNative.PageActivity;

import android.os.Bundle;
import android.util.Log;

import com.facebook.react.ReactActivity;
import com.facebook.react.ReactActivityDelegate;
import com.mreader.LG.Middleware.HistoryService;
import com.mreader.LG.Utility.JsonConverter;

public class HistoryActivity extends ReactActivity {
    HistoryService historyMiddleware;

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
                historyMiddleware = HistoryService.getInstance(HistoryActivity.this);
                Bundle launchOptions = new Bundle();
                launchOptions.putString("initialRouteName", "History");
                Log.d("HistoryActivity", JsonConverter.listToJsonSafe(historyMiddleware.getAllHistoryDesc()));
                launchOptions.putString(
                        "data",
                        JsonConverter.listToJsonSafe(historyMiddleware.getAllHistoryDesc())
                );
                return launchOptions;
            }
        };
    }
}
