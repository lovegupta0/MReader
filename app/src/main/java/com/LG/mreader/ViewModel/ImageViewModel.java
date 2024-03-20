package com.LG.mreader.ViewModel;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.LG.mreader.AppDatabase.AppDatabase;
import com.LG.mreader.AppDatabase.ViewImageDao;
import com.LG.mreader.AppRepository.AppRepository;
import com.LG.mreader.DataModel.BookmarkDataModel;
import com.LG.mreader.DataModel.ImageDataModel;
import com.LG.mreader.DataModel.ViewImageDataModel;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;

public class ImageViewModel extends AndroidViewModel {

    private MutableLiveData<Boolean> nextBatch =new MutableLiveData<>(false);
    private MutableLiveData<Boolean> showImageView=new MutableLiveData<>(false);
    private String homePage="";
    private String title="";
    private String pageSrc="";
    private String nextPage="";
    private String prevPage="";
    private List<String> imgList=new ArrayList<>();
    private LiveData<List<ViewImageDataModel>> getData;
    private LiveData<List<ImageDataModel>> ImgData;
    private LiveData<List<BookmarkDataModel>> bookmarks;
    private AppRepository repo;

    public ImageViewModel(@NonNull Application application) {
        super(application);

            repo = new AppRepository(application);
            getData = repo.getViewImage();
            ImgData=  repo.getImage();
            bookmarks=repo.getBookmarks();
    }

    public LiveData<List<ViewImageDataModel>> getImgSrc(){
        if(getData.getValue()==null){
            getData=repo.getViewImage();
        }
        return getData;
    }

    public  void setNextBatch(Boolean req){
        nextBatch.setValue(req);
    }
    public LiveData<Boolean> getNextBatch(){
        return nextBatch;
    }

    public void setShowImageView(Boolean req){
        showImageView.setValue(req);
    }

    public LiveData<Boolean> getShowImageView(){
        return showImageView;
    }

    public String getHomePage() {
        return homePage;
    }

    public void setHomePage(String homePage) {
        this.homePage = homePage;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPageSrc() {
        return pageSrc;
    }

    public void setPageSrc(String pageSrc) {
        this.pageSrc = pageSrc;
    }

    public String getNextPage() {
        return nextPage;
    }

    public void setNextPage(String nextPage) {
        this.nextPage = nextPage;
    }

    public String getPrevPage() {
        return prevPage;
    }

    public void setPrevPage(String prevPage) {
        this.prevPage = prevPage;
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

    public LiveData<List<BookmarkDataModel>> getBookmarks() {
        return bookmarks;
    }
}
