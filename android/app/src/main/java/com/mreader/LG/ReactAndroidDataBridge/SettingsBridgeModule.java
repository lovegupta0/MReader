package com.mreader.LG.ReactAndroidDataBridge;



import androidx.annotation.NonNull;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;


import com.mreader.LG.Common.SettingStorage;
import com.mreader.LG.DataModel.SettingDataModel;
import com.mreader.LG.Utility.JsonConverter;

/**
 * Bridge for React Native ↔ Android communication
 * Handles saving updated settings from React side.
 */
public class SettingsBridgeModule extends ReactContextBaseJavaModule {

    private final ReactApplicationContext reactContext;
    private SettingStorage settingStorage;

    public SettingsBridgeModule(@NonNull ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
        settingStorage=SettingStorage.getInstance();
    }

    @NonNull
    @Override
    public String getName() {
        return "SettingsBridge";
    }

    /**
     * Called from React Native to save updated settings.
     */
    @ReactMethod
    public void saveSettings(String jsonSettings, Promise promise) {
        try {

            // Convert JSON to model
            SettingDataModel settingModel = JsonConverter.jsonToObj(jsonSettings, SettingDataModel.class);

            // Save to Room database
            settingStorage.update(settingModel);


            promise.resolve("Settings saved successfully");
        } catch (Exception e) {
            e.printStackTrace();
            promise.reject("SAVE_FAILED", e.getMessage());
        }
    }
}
