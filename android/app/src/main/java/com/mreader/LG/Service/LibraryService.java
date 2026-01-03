package com.mreader.LG.Service;

import com.mreader.LG.AppRepository.AppRepository;
import com.mreader.LG.DataModel.LibraryDataModel;

import java.util.List;

public class LibraryService {
    private static LibraryService instance;
    private AppRepository repo;

    private LibraryService() {
        repo=AppRepository.getInstance();
    }
    public static LibraryService getInstance() {
        if (instance == null) {
            synchronized (LibraryService.class) {
                if (instance == null) {
                    instance = new LibraryService();
                }
            }
        }
        return instance;
    }
    public void updateLibrary(LibraryDataModel data) {
        repo.updateLibrary(data);
    }
    public void insertLibrary(LibraryDataModel data) {
       if(!repo.getLibraryByChapter(data.getChapterUrl()).isEmpty()){
           return;
       }
        repo.insertLibrary(data);
    }
    public List<LibraryDataModel> getLibrary(){
        return repo.getLibraryData();
    }
    public void deleteLibrary(LibraryDataModel data) {
        repo.deleteLibrary(data);
    }

    public List<LibraryDataModel> getSortedLibrary() {
        return repo.getAllSortedByLastUpdatedDate();
    }
    public boolean isExist(String pageUrl){
        if(!repo.getLibraryByPageUrl(pageUrl).isEmpty() || !repo.getLibraryByChapter(pageUrl).isEmpty()){
            return true;
        }
        return false;
    }

    public List<LibraryDataModel> getLibraryByUrl(String url){
        List<LibraryDataModel> l1=repo.getLibraryByPageUrl(url);
        if(l1.isEmpty()){
            l1=repo.getLibraryByChapter(url);
        }
        return l1;
    }

    public void insertLibrary(List<LibraryDataModel> lst){
        if(lst==null) return;
        for(LibraryDataModel data:lst){
            insertLibrary(data);
        }
    }

}
