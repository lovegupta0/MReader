package com.mreader.LG.Utility;

import android.content.Context;

public class ContextManager {
    private static ContextManager instance;
    private Context webFragmentContext;
    private Context applicationMainContext;
    private ContextManager(){

    }

    public static ContextManager getInstance() {
        if (instance == null) {
            synchronized (ContextManager.class){
                if (instance==null){
                    instance=new ContextManager();
                }
            }
        }
        return instance;

    }
    public void setWebFragmentContext(Context context){
        this.webFragmentContext=context;
    }
    public Context getWebFragmentContext(){
        return this.webFragmentContext;
    }

    public Context getApplicationMainContext() {
        return applicationMainContext;
    }

    public void setApplicationMainContext(Context applicationMainContext) {
        this.applicationMainContext = applicationMainContext;
    }
}
