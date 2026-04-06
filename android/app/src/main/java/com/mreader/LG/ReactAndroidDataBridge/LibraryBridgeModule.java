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
import com.mreader.LG.Service.LibraryService;
import com.mreader.LG.Utility.ContextManager;
import com.mreader.LG.Utility.JsonConverter;
import com.mreader.LG.ViewModel.WebViewModel;
import com.mreader.MainActivity;

public class LibraryBridgeModule extends ReactContextBaseJavaModule {
    LibraryService service;


    public LibraryBridgeModule(ReactApplicationContext context) {
        super(context);
        service=LibraryService.getInstance();

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


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @ReactMethod void onLibraryItemDelete(String data){
        try {
            LibraryDataModel item =JsonConverter.jsonToObj(data, LibraryDataModel.class);
            service.deleteLibrary(item);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}