package com.mreader.LG.ViewModel;

import static com.mreader.LG.Utility.LibraryCheckForUpdate.payload;

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
import com.mreader.LG.DataModel.LibraryTempDataModel;
import com.mreader.LG.Middleware.ImageDataContainer;
import com.mreader.LG.PoolService.CentralThreadPool;
import com.mreader.LG.Service.LibraryService;
import com.mreader.LG.Service.LibraryTempService;
import com.mreader.LG.Utility.HeadlessBrowser;
import com.mreader.LG.Utility.ThreadsPoolManager;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

public class FloatButtonViewModel extends AndroidViewModel {

    private MutableLiveData<Boolean> showFloatButton = new MutableLiveData<>(true);
    private final ImageDataContainer imageDataContainer;
    private final LibraryService service;
    private final String TAG="FloatButtonViewModel";
    private ThreadsPoolManager threadsPoolManager;
    private final HeadlessBrowser headlessBrowser;
    private LibraryTempService libraryTempService;

    public FloatButtonViewModel(@NonNull Application application) {
        super(application);
        imageDataContainer=ImageDataContainer.getInstance();
        service=LibraryService.getInstance();
        threadsPoolManager=CentralThreadPool.getInstance();
        headlessBrowser=HeadlessBrowser.getInstance();
        libraryTempService=LibraryTempService.getInstance();
    }

    public void setShowFloatButton(boolean show) {
        showFloatButton.setValue(show);

    }

    public MutableLiveData<Boolean> getShowFloatButton() {
        return showFloatButton;
    }
    public void floatButtonAction(){
        try {


        LibraryDataModel data=new LibraryDataModel();
        Chapter currentChapter=imageDataContainer.getCurrentChapter();
        if(currentChapter==null || service.isExist(currentChapter.getPageSource())){
            setShowFloatButton(false);
            return;
        }

        List<String> lst=new ArrayList<>();
        lst.addAll(fetchData(currentChapter));
        Log.d(TAG,"List: "+lst.toString());
        if(lst.isEmpty()) return;
        data.setPageUrl(currentChapter.getPageSource());
        data.setBaseUrl(currentChapter.getHomeUrl());
        data.setTitle(currentChapter.getTitle());
        data.setChapterUrl(currentChapter.getCurrentUrl());
        data.setLastUpdateddate(Converters.fromLocalDateTime(LocalDateTime.now()));
        data.setCoverUrl(lst.get(0));
        data.setLatestchapter(lst.get(1));
        data.setLatestChapterUpdated(lst.get(2));
        service.insertLibrary(data);
        Toast.makeText(getApplication(), "Library added", Toast.LENGTH_SHORT).show();
        setShowFloatButton(false);
        } catch (Exception e) {
            Log.d(TAG,e.getMessage());
        }
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
        List<String> lst=new ArrayList();
        try {
            lst.addAll(future.get());
            if(lst.size()<3) lst.clear();
        } catch (Exception e) {
           Log.d(TAG,e.getMessage());
        }

        if(lst.isEmpty()) lst.addAll(fetchDataV2(currentChapter));
        return lst;
    }

    private List<String> fetchDataV2(Chapter currentChapter){
        List<String> lst=new ArrayList<>();
        if(libraryTempService.isExist(currentChapter.getPageSource())){
            LibraryTempDataModel temp=libraryTempService.getLibraryByPageUrl(currentChapter.getPageSource());
            lst.add(temp.getCoverUrl());
            lst.add(temp.getLatestchapter());
            lst.add(temp.getLatestChapterUpdated());
        }
        return lst;
    }

    private List<String> transformData(String data){
        List<String> lstData=new ArrayList<>();
        data=data.replace("\"","");
        String[] lst=data.split(",");
        int pos=data.indexOf("\\n");

        lstData.add(lst[0]);

        if (lst.length > 1) {
            lstData.add(lst[1].replace("\\n","").trim());
        }
        if(lst.length>2){
            lstData.add(data.substring(pos+3).replace("\\n","").trim());
        }
       return lstData;
    }

}
