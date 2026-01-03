package com.mreader.LG.ReactAndroidDataBridge;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

public class DataBridgeModule extends ReactContextBaseJavaModule implements LifecycleEventListener {

    private static final String NAME = "CommonDataBridge";
    private final ReactApplicationContext reactContext;

    public DataBridgeModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
        reactContext.addLifecycleEventListener(this);
    }

    @NonNull @Override
    public String getName() {
        return NAME;
    }

    /** ===== React -> Native =====
     * Receive dynamic data from JS at any time.
     */
    @ReactMethod
    public void sendToNative(String jsonPayload, Promise promise) {
        // TODO: parse/handle/store the payload as needed
        // e.g., forward to Room/Repository/etc.
        promise.resolve("Native received: " + jsonPayload);
    }

    /** ===== React -> Native (request data) ===== */
    @ReactMethod
    public void getNativeData(Promise promise) {
        WritableMap data = Arguments.createMap();
        data.putString("time", String.valueOf(System.currentTimeMillis()));
        data.putString("source", "native");
        promise.resolve(data);
    }

    /** ===== Native -> React (emit event) =====
     * You can call this from anywhere in Java side to push data to JS.
     */
    public void emitToJs(String eventName, WritableMap payload) {
        if (reactContext.hasActiveCatalystInstance()) {
            reactContext
                    .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                    .emit(eventName, payload);
        }
    }

    /** Example: as soon as the host (activity) resumes, push fresh data to JS */
    @Override
    public void onHostResume() {
        WritableMap payload = Arguments.createMap();
        payload.putString("type", "screenReady");
        payload.putString("msg", "Screen resumed – sending initial native data");
        emitToJs("DataBridgeEvent", payload);
    }

    @Override public void onHostPause() {}
    @Override public void onHostDestroy() {}
}
