package com.mreader;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;

import com.facebook.react.PackageList;
import com.facebook.react.ReactApplication;
import com.facebook.react.ReactHost;
import com.facebook.react.ReactNativeHost;
import com.facebook.react.defaults.DefaultNewArchitectureEntryPoint;
import com.facebook.react.defaults.DefaultReactHost;
import com.facebook.react.defaults.DefaultReactNativeHost;
import com.facebook.react.ReactPackage;
import com.facebook.soloader.SoLoader;
import com.facebook.react.soloader.OpenSourceMergedSoMapping;
import com.mreader.LG.ReactAndroidDataBridge.DataBridgePackage;


import java.io.IOException;
import java.util.List;

public class MainApplication extends Application implements ReactApplication {
    private final ReactNativeHost reactNativeHost =
            new DefaultReactNativeHost(this) {
                @Override
                public boolean getUseDeveloperSupport() {
                    return BuildConfig.DEBUG;
                }

                @Override
                protected List<ReactPackage> getPackages() {
                    List<ReactPackage> packageList= new PackageList(this).getPackages();
                    packageList.add(new DataBridgePackage());
                    return packageList;
                }

                @Override
                protected String getJSMainModuleName() {
                    return "index";
                }
            };

    private ReactHost reactHost;

    @Override
    @NonNull
    public ReactNativeHost getReactNativeHost() {
        return reactNativeHost;
    }

    @NonNull
    public ReactHost getReactHost() {
        if (reactHost == null) {
            // Uses DefaultReactHost.getDefaultReactHost(Context, ReactNativeHost, JSRuntimeFactory?)
            reactHost = DefaultReactHost.getDefaultReactHost(
                    getApplicationContext(),
                    reactNativeHost,
                    /* jsRuntimeFactory */ null   // null → uses Hermes by default per Kotlin impl
            );
        }
        return reactHost;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("MainApplication", "DEBUG MODE: " + BuildConfig.DEBUG);
        try {
            SoLoader.init(this, OpenSourceMergedSoMapping.INSTANCE);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (BuildConfig.IS_NEW_ARCHITECTURE_ENABLED) {
            DefaultNewArchitectureEntryPoint.load();
        }
    }
}

