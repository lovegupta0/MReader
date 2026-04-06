package com.mreader.LG.Service;

import android.content.Context;

import com.mreader.LG.AppRepository.AppRepository;
import com.mreader.LG.DataModel.LOVDataModel;


public class LOVService {
    private AppRepository repo;
    public LOVService(){
        this(null);
    }
    public LOVService(Context context){
        repo = context != null ? AppRepository.getInstance(context) : AppRepository.getInstance();
    }
    public void updateLOV(LOVDataModel data) {
        repo.updateLOV(data);
    }
    public void insertLOV(LOVDataModel data) {
        repo.insertLOV(data);
    }
    public LOVDataModel getLOV(){
        return repo.getLOV();
    }
    public void deleteLOV() {
        repo.deleteLOV();
    }


}
