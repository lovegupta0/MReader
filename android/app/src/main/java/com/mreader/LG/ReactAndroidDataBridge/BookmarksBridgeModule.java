package com.mreader.LG.ReactAndroidDataBridge;

import android.content.Intent;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.mreader.LG.DataModel.BookmarkDataModel;
import com.mreader.LG.Service.BookmarkService;
import com.mreader.LG.Utility.JsonConverter;
import com.mreader.MainActivity;

public class BookmarksBridgeModule extends ReactContextBaseJavaModule {
    private final ReactApplicationContext reactContext;
    private final BookmarkService bookmarksService;


    public BookmarksBridgeModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
        bookmarksService=new BookmarkService();
    }

    @NonNull
    @Override
    public String getName() {
        return "BookmarksBridge";
    }

    @ReactMethod
    public void OnClickBookmark(String url) {
        System.out.println("Bookmark item clicked: " + url);
        Intent intent=new Intent(getReactApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("openWeb",true);
        intent.putExtra("url",url);
        getReactApplicationContext().startActivity(intent);
    }

    @ReactMethod
    public void onClickeDelete(String data){
        BookmarkDataModel model= JsonConverter.jsonToObj(data,BookmarkDataModel.class);
        bookmarksService.deleteBookmark(model);
    }
    @ReactMethod
    public void updateBookmark(String data){
        BookmarkDataModel model= JsonConverter.jsonToObj(data,BookmarkDataModel.class);
        bookmarksService.updateBookmark(model);

    }


}
