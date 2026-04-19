package com.mreader.LG.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class WebViewModel  extends ViewModel {
    private MutableLiveData<String> urlAddress=new MutableLiveData<>("");
    private MutableLiveData<Boolean> webRequest=new MutableLiveData<>(false);
    private MutableLiveData<Boolean> reload=new MutableLiveData<>(false);
    private MutableLiveData<Boolean> loadProgressBar=new MutableLiveData<>(false);
    boolean isNavigate = false;

    public void setUrlAddress(String url){
        urlAddress.setValue(url);
    }
    public LiveData<String> getUrlAddress(){
        return urlAddress;
    }

    public void setWebRequest(Boolean req){
        webRequest.setValue(req);
    }

    public LiveData<Boolean> getWebRequest(){
        return webRequest;
    }

    private void toggleReload(){
        reload.setValue(!reload.getValue());
        setValueDefault();

    }

    public LiveData<Boolean> getReload(){
        return reload;
    }

    public void reloadThePage(){
        if(urlAddress.getValue().isEmpty()) return;
        toggleReload();
    }
    private void setValueDefault(){
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        reload.setValue(false);
    }
    public LiveData<Boolean> getLoadProgressBar(){
        return loadProgressBar;
    }
    public void loadProgressBar(){
        loadProgressBar.setValue(true);
    }
    public void hideProgressBar(){
        loadProgressBar.setValue(false);
    }
    public boolean isNavigate(){
        return isNavigate;
    }
    public void toggleNavigate(){
        this.isNavigate= !this.isNavigate;
    }

}
