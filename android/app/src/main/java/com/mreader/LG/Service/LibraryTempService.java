package com.mreader.LG.Service;

import android.content.Context;

import com.mreader.LG.AppRepository.AppRepository;
import com.mreader.LG.DataModel.LibraryTempDataModel;

import java.util.List;
import java.util.Optional;

public class LibraryTempService {
    private static LibraryTempService instance;
    private AppRepository repo;

    private LibraryTempService() {
        this(null);
    }
    private LibraryTempService(Context context) {
        repo = context != null ? AppRepository.getInstance(context) : AppRepository.getInstance();
    }
    public static LibraryTempService getInstance() {
        if (instance == null) {
            synchronized (LibraryTempService.class) {
                if (instance == null) {
                    instance = new LibraryTempService();
                }
            }
        }
        return instance;

    }
    public static LibraryTempService getInstance(Context context) {
        if (instance == null) {
            synchronized (LibraryTempService.class) {
                if (instance == null) {
                    instance = new LibraryTempService(context);
                }
            }
        }
        return instance;
    }
    public void insertLibrary(LibraryTempDataModel data) {
        repo.insertLibraryTemp(data);
    }
    public void updateLibrary(LibraryTempDataModel data) {
        repo.updateLibraryTemp(data);
    }
    public void deleteLibrary(LibraryTempDataModel data) {
        repo.deleteLibraryTemp(data);
    }
    public List<LibraryTempDataModel> getLibrary(){
        return repo.getLibraryTemp();
    }
    public List<LibraryTempDataModel> getLibraryByLastUpdatedDate(String givenDate){
        return repo.getLibraryTempByLastUpdatedDate(givenDate);
    }

    public LibraryTempDataModel getLibraryByPageUrl(String pageUrl) {
        return repo.getLibraryTempByPageUrl(pageUrl);
    }

    public boolean isExist(String pageUrl){
       return getLibraryByPageUrl(pageUrl)!=null;
    }

    public void deleteOlderOne(){
        repo.deleteOlderOne();
    }


}
