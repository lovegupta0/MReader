package com.mreader.LG.ReactNative.PageActivity;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;

import com.facebook.react.ReactActivity;
import com.facebook.react.ReactActivityDelegate;
import com.mreader.LG.Common.SettingStorage;
import com.mreader.LG.DataModel.LOVDataModel;
import com.mreader.LG.DataModel.SettingDataModel;
import com.mreader.LG.Service.LOVService;
import com.mreader.LG.Utility.JsonConverter;

/**
 * A dedicated Activity that loads the React Native Settings screen.
 * It passes SettingDataModel and LOVDataModel as initial props to the RN bridge.
 */
public class SettingActivity extends ReactActivity {


    private SettingStorage settingStorage;
    private LOVService lovService;
    private String TAG="SettingActivity";


    @Override
    protected String getMainComponentName() {
        // Must match your React Native entry point name
        return "LGMreader";
    }

    @Override
    protected ReactActivityDelegate createReactActivityDelegate()  {
        Log.d(TAG,"Setting started");
        // ✅ Prepare initial props for React
        Bundle initialProps = new Bundle();
        initialProps.putString("initialRouteName", "Settings");
        settingStorage=SettingStorage.getInstance();
        lovService=new LOVService();
        SettingDataModel settings =settingStorage.getSetting();
        LOVDataModel lov = lovService.getLOV();

        // Convert them to JSON using your utility class
        String settingsJson = JsonConverter.objToJsonSafe(settings);
        String lovJson = JsonConverter.objToJsonSafe(lov);

        // ✅ Set the JSON data as props

        initialProps.putString("settings", settingsJson);
        initialProps.putString("lov", lovJson);

        return new ReactActivityDelegate(this, getMainComponentName()) {
            @Override
            protected Bundle getLaunchOptions() {
                return initialProps;
            }
        };


    }


}
