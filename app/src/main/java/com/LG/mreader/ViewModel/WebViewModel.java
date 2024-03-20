package com.LG.mreader.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class WebViewModel  extends ViewModel {
    private MutableLiveData<String> urlAddress=new MutableLiveData<>("https://readm.today/manga/magic-emperor/516/all-pages");
    private MutableLiveData<Boolean> webRequest=new MutableLiveData<>(false);

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
}
