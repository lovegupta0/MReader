package com.mreader.LG.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.mreader.LG.DataModel.BookmarkDataModel;
import com.mreader.LG.DataModel.Chapter;
import com.mreader.LG.DataModel.ImageDataModel;
import com.mreader.LG.Middleware.ImageDataContainer;

import java.util.ArrayList;
import java.util.List;

public class ImageViewModel extends AndroidViewModel {

    private MutableLiveData<Boolean> nextBatch =new MutableLiveData<>(false);
    private MutableLiveData<Boolean> showImageView=new MutableLiveData<>(false);
    private MenuViewModel menuViewModel;

    private List<String> imgList=new ArrayList<>();
    private LiveData<Chapter> getData;
    private LiveData<List<ImageDataModel>> ImgData;

    private ImageDataContainer imageDataContainer;


    public ImageViewModel(@NonNull Application application) {
        super(application);
        imageDataContainer=ImageDataContainer.getInstance();
        menuViewModel=MenuViewModel.getInstance();
    }

    public void setNextBatch(Boolean req){
        nextBatch.setValue(req);
    }

    public LiveData<Chapter> getImgSrc(){
        if(!imageDataContainer.isEmpty()){
            getData=new MutableLiveData<>(imageDataContainer.getModel());
        }
        return getData;
    }



    public void setShowImageView(Boolean req){
        if(menuViewModel.getReadMode().getValue()){
            showImageView.setValue(req);
        }

    }

    public LiveData<Boolean> getShowImageView(){
        return showImageView;
    }



    public List<String> getImgList() {
        return imgList;
    }

    public void setImgList(List<String> imgList) {
        if(this.imgList.size()>0){
            this.imgList.clear();
        }
        for(String s:imgList){
            this.imgList.add(s);
        }
    }

    public LiveData<List<ImageDataModel>> getImgData() {
        return ImgData;
    }

}
