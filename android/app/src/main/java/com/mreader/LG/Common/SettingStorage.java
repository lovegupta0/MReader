package com.mreader.LG.Common;

import com.mreader.LG.DataModel.SettingDataModel;
import com.mreader.LG.Service.SettingService;
import static com.mreader.LG.DataModel.SettingDataModel.*;

import android.util.Log;

public class SettingStorage {
    private SettingDataModel dataModel;
    private static SettingStorage instance;
    private SettingService service;
    private String TAG="SettingStorage";
    private SettingStorage(){
        service=new SettingService();
        dataModel=service.getSetting();
    }
    public static SettingStorage getInstance(){

        if(instance==null){
            synchronized (SettingStorage.class){
                if(instance==null){
                    instance=new SettingStorage();
                }
            }

        }
        if(!instance.verify()) instance.load();
        return instance;
    }
    public SettingDataModel getSetting(){
        Log.d(TAG,String.valueOf(dataModel));
        return dataModel;
    }
    public General getGeneral(){
        return dataModel.getGeneralSection();
    }
    public Privacy getPrivacy(){
        return dataModel.getPrivacySection();
    }
    public Performance getPerformance(){
        return dataModel.getPerformanceSection();
    }
    public Download getDownload(){
        return dataModel.getDownloadSection();
    }
    public Advanced getAdvanced(){
        return dataModel.getAdvancedSection();
    }
    private void load(){
        dataModel=service.getSetting();
    }
    public boolean verify(){
        if(dataModel==null) return false;
        if(dataModel.getGeneralSection()==null) return false;
        if(dataModel.getPrivacySection()==null) return false;
        if(dataModel.getPerformanceSection()==null) return false;
        if(dataModel.getDownloadSection()==null) return false;
        if(dataModel.getAdvancedSection()==null) return false;
        return true;
    }
    public void update(SettingDataModel dataModel){
        this.dataModel=dataModel;
        service.updateSetting(dataModel);
    }
    public boolean getReadMode(){
        return dataModel.getReadMode();
    }
    public void setReadMode(boolean readMode) {
        dataModel.setReadMode(readMode);
        service.updateReadMode(readMode);
    }


}
