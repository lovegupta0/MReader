package com.mreader.LG.ReactNative.PageActivity;

import android.os.Bundle;
import android.util.Log;

import com.facebook.react.ReactActivity;
import com.facebook.react.ReactActivityDelegate;
import com.mreader.LG.DataModel.LOVDataModel;
import com.mreader.LG.DataModel.SettingDataModel;
import com.mreader.LG.Service.LOVService;
import com.mreader.LG.Service.SettingService;
import com.mreader.LG.Utility.JsonConverter;

/**
 * A dedicated Activity that loads the React Native Settings screen.
 * It passes SettingDataModel and LOVDataModel as initial props to the RN bridge.
 */
public class SettingActivity extends ReactActivity {

    private SettingService settingService;
    private LOVService lovService;
    private String TAG="SettingActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(null);
    }

    @Override
    protected String getMainComponentName() {
        return "LGMreader";
    }

    @Override
    protected ReactActivityDelegate createReactActivityDelegate()  {
        Log.d(TAG,"Setting started");

        return new ReactActivityDelegate(this, getMainComponentName()) {
            @Override
            protected Bundle getLaunchOptions() {
                Bundle initialProps = new Bundle();
                initialProps.putString("initialRouteName", "Settings");

                settingService = new SettingService(SettingActivity.this);
                lovService = new LOVService(SettingActivity.this);

                SettingDataModel settings = settingService.getSetting();
                LOVDataModel lov = lovService.getLOV();

                String settingsJson = JsonConverter.objToJsonSafe(settings);
                String lovJson = JsonConverter.objToJsonSafe(lov);

                initialProps.putString("settings", settingsJson);
                initialProps.putString("lov", lovJson);
                return initialProps;
            }
        };
    }
}
