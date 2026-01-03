package com.mreader.LG.ReactAndroidDataBridge;

import android.content.Intent;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.mreader.LG.DataModel.History;
import com.mreader.LG.Middleware.HistoryService;
import com.mreader.LG.Utility.JsonConverter;
import com.mreader.MainActivity;

public class HistoryBridgeModule extends ReactContextBaseJavaModule {
    private final ReactApplicationContext reactContext;
    private HistoryService historyService;
    public HistoryBridgeModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
        historyService=HistoryService.getInstance();
    }
    @NonNull
    @Override
    public String getName() {
        return "HistoryBridge";
    }

    @ReactMethod
    public void onClickHistory(String url){
        System.out.println("History item clicked: " + url);

        Intent intent=new Intent(getReactApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("openWeb",true);
        intent.putExtra("url",url);
        getReactApplicationContext().startActivity(intent);
    }

    @ReactMethod public void deleteAllHistory(){
        historyService.deleteAllHistory();
    }
}
