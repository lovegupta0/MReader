package com.LG.mreader.Utility;

import android.content.Context;

public class ContextManager {
    private static ContextManager instance;
    private Context webFragmentContext;
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
}
