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
import com.mreader.LG.Middleware.ImageDataContainer;
import com.mreader.LG.PoolService.CentralThreadPool;
import com.mreader.LG.Service.LibraryService;
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

    public FloatButtonViewModel(@NonNull Application application) {
        super(application);
        imageDataContainer=ImageDataContainer.getInstance();
        service=LibraryService.getInstance();
        threadsPoolManager=CentralThreadPool.getInstance();
        headlessBrowser=HeadlessBrowser.getInstance();
    }

    public void setShowFloatButton(boolean show) {
        showFloatButton.setValue(show);

    }

    public MutableLiveData<Boolean> getShowFloatButton() {
        return showFloatButton;
    }
    public void floatButtonAction(){
        LibraryDataModel data=new LibraryDataModel();
        Chapter currentChapter=imageDataContainer.getCurrentChapter();
        if(currentChapter==null || service.isExist(currentChapter.getPageSource())){
            setShowFloatButton(false);
            return;
        }

        List<String> lst=new ArrayList<>();
        try {
            lst.addAll(fetchData(currentChapter));
        } catch (Exception e) {
           Log.d(TAG,e.getMessage());
        }
        if(lst.isEmpty()){

        }
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
        Toast.makeText(getApplication(), "Library added", Toast.LENGTH_SHORT).show();
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

    private List<String> fetchDataV2(Chapter currentChapter){
        String pageUrl=currentChapter.getPageSource();
        List<String> data=new ArrayList<>();
        try {
            headlessBrowser.fetchData(pageUrl, payload, new HeadlessBrowser.callback() {
                @Override
                public void onSuccess(String extractedData) {
                    if (extractedData == null || extractedData.isEmpty()) {
                        Log.w(TAG, "Empty update response for " + pageUrl);

                    } else if ("[\"BLOCKED\"]".equals(extractedData)) {
                        Log.w(TAG, "Cloudflare blocked update check for " + pageUrl);

                    } else {
                        Log.d(TAG, "Fetched update data for " + pageUrl + ": " + extractedData);
                        data.addAll(transformData(extractedData));

                    }
                }

                @Override
                public void onError(Exception e) {
                    Log.e(TAG, "Update check failed for " + pageUrl, e);

                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Unable to start update check for " + pageUrl, e);

        }
        return data;
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
