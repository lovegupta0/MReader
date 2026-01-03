package com.mreader.LG.ReactAndroidDataBridge;

import static androidx.core.content.ContentProviderCompat.requireContext;
import static androidx.core.content.ContextCompat.startActivity;

import android.app.Activity;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mreader.LG.DataModel.LibraryDataModel;
import com.mreader.LG.ReactNative.PageActivity.LibraryActivity;
import com.mreader.LG.Utility.ContextManager;
import com.mreader.LG.Utility.JsonConverter;
import com.mreader.LG.ViewModel.WebViewModel;
import com.mreader.MainActivity;

public class LibraryBridgeModule extends ReactContextBaseJavaModule {



    public LibraryBridgeModule(ReactApplicationContext context) {
        super(context);


    }

    @NonNull
    @Override
    public String getName() {
        return "LibraryBridge";
    }

    /**
     * Called when user taps a library card
     */
    @ReactMethod
    public void onLibraryItemClick(String json) {
        try {

            LibraryDataModel item = JsonConverter.jsonToObj(json, LibraryDataModel.class);
            Intent intent=new Intent(getReactApplicationContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("openWeb",true);
            intent.putExtra("url",item.getPageUrl());
            getReactApplicationContext().startActivity(intent);

            System.out.println("Library item clicked: " + item.getTitle());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @ReactMethod void onLibraryItemDelete(String data){
        try {
            LibraryDataModel item =JsonConverter.jsonToObj(data, LibraryDataModel.class);


            // 👉 HERE is where you decide what happens
            // Example: open reader activity
            // ReaderActivity.open(getReactApplicationContext(), item);

            System.out.println("Library item clicked: " + item.getTitle());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}