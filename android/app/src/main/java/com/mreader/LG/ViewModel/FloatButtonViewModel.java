package com.mreader.LG.ViewModel;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.mreader.LG.Common.Converters;
import com.mreader.LG.Common.PageDataExtracter;
import com.mreader.LG.Common.WebRequest;
import com.mreader.LG.DataModel.Chapter;
import com.mreader.LG.DataModel.LibraryDataModel;
import com.mreader.LG.Middleware.ImageDataContainer;
import com.mreader.LG.PoolService.CentralThreadPool;
import com.mreader.LG.Service.LibraryService;
import com.mreader.LG.Utility.ThreadsPoolManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Future;

public class FloatButtonViewModel extends AndroidViewModel {

    private MutableLiveData<Boolean> showFloatButton = new MutableLiveData<>(true);
    private final ImageDataContainer imageDataContainer;
    private final LibraryService service;
    private final String TAG="FloatButtonViewModel";
    private ThreadsPoolManager threadsPoolManager;

    public FloatButtonViewModel(@NonNull Application application) {
        super(application);
        imageDataContainer=ImageDataContainer.getInstance();
        service=LibraryService.getInstance();
        threadsPoolManager=CentralThreadPool.getInstance();
    }

    public void setShowFloatButton(boolean show) {
        showFloatButton.setValue(show);

    }

    public MutableLiveData<Boolean> getShowFloatButton() {
        return showFloatButton;
    }
    public void floatButtonAction(){
        Toast.makeText(getApplication(), "Add button clicked", Toast.LENGTH_SHORT).show();
        LibraryDataModel data=new LibraryDataModel();
        Chapter currentChapter=imageDataContainer.getCurrentChapter();
        List<String> lst=fetchData(currentChapter);
        Log.d(TAG,"List: "+lst.toString());
        data.setPageUrl(currentChapter.getPageSource());
        data.setBaseUrl(currentChapter.getHomeUrl());
        data.setTitle(currentChapter.getTitle());
        data.setChapterUrl(currentChapter.getCurrentUrl());
        data.setLastUpdateddate(Converters.fromLocalDateTime(LocalDateTime.now()));
        data.setCoverUrl(lst.get(0));
        data.setLatestchapter(lst.get(1));
        data.setLatestChapterUpdated(lst.get(2));
        service.insertLibrary(data);
        setShowFloatButton(false);
    }
    public void checkForVisibility(){
        Chapter currentChapter=imageDataContainer.getCurrentChapter();
        if(currentChapter!=null && service.isExist(currentChapter.getPageSource())){
            setShowFloatButton(false);
        }
        else setShowFloatButton(true);
    }

    private List<String> fetchData(Chapter currentChapter){
        Future<List<String>> future=threadsPoolManager.submitTask(()->PageDataExtracter.ExtractDataForChapter(WebRequest.fetchPageHTML(currentChapter.getPageSource(),currentChapter.getHomeUrl()),currentChapter.getHomeUrl()));
        try {
            return future.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
