package com.mreader.LG.Service;

import android.content.Context;
import android.util.Log;

import com.mreader.LG.AppRepository.AppRepository;
import com.mreader.LG.DataModel.SettingDataModel;

public class SettingService {
    private String TAG="SettingService";
    private AppRepository repo;
    public SettingService(){
        this(null);
    }
    public SettingService(Context context){
        repo = context != null ? AppRepository.getInstance(context) : AppRepository.getInstance();
    }
    public void updateSetting(SettingDataModel data){
        repo.updateSetting(data);
    }
    public void insertSetting(SettingDataModel data){
        if(repo.getSetting()==null){
            Log.d(TAG,"inserting");
            repo.insertSetting(data);
        }
       else{
            Log.d(TAG,"updating");
           repo.updateSetting(data);
        }
    }
    public SettingDataModel getSetting(){
        return repo.getSetting();
    }
    public void deleteSetting() {
        repo.deleteSetting();
    }
    public void updateReadMode(boolean readMode){
        repo.updateReadMode(readMode);
    }
}
