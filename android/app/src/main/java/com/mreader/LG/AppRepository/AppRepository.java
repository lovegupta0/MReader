package com.mreader.LG.AppRepository;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.mreader.LG.AppDatabase.AppDatabase;
import com.mreader.LG.AppDatabase.BookmarkDao;
import com.mreader.LG.AppDatabase.HistoryDao;
import com.mreader.LG.AppDatabase.ImageDao;
import com.mreader.LG.AppDatabase.LOVDao;
import com.mreader.LG.AppDatabase.LibraryDao;
import com.mreader.LG.AppDatabase.SettingDao;
import com.mreader.LG.AppDatabase.ViewImageDao;
import com.mreader.LG.DataModel.BookmarkDataModel;
import com.mreader.LG.DataModel.History;
import com.mreader.LG.DataModel.ImageDataModel;
import com.mreader.LG.DataModel.LOVDataModel;
import com.mreader.LG.DataModel.LibraryDataModel;
import com.mreader.LG.DataModel.SettingDataModel;
import com.mreader.LG.DataModel.ViewImageDataModel;
import com.mreader.LG.PoolService.CentralThreadPool;
import com.mreader.LG.Utility.ThreadsPoolManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class AppRepository {
    private final LibraryDao libraryDao;
    private final BookmarkDao bookmarkDao;
    private final ImageDao imageDao;
    private final ViewImageDao viewImageDao;

    private final HistoryDao historyDao;
    private final ThreadsPoolManager threadsPoolManager;
    private final LOVDao lovDao;
    private final SettingDao settingDao;

    private static AppRepository instance;
    private AppRepository(Context context){
        AppDatabase database=AppDatabase.getInstance(context);
        libraryDao=database.libraryDao();
        bookmarkDao= database.bookmarkDao();
        imageDao=database.imageDao();
        viewImageDao= database.viewImageDao();
        historyDao=database.historyDao();
        threadsPoolManager= CentralThreadPool.getInstance();
        lovDao=database.lovDao();
        settingDao =database.settingDao();

    }
    public void insertLibrary(LibraryDataModel data){
        threadsPoolManager.submitTask(()->libraryDao.insertLibrary(data));
    }

    public List<LibraryDataModel> getLibraryData(){

        Future<List<LibraryDataModel>> future = threadsPoolManager.submitTask(() -> {
            return libraryDao.getLibrary();
        });
        try {
            return future.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public List<BookmarkDataModel> getBookmarks(){
        Future<List<BookmarkDataModel>> future = threadsPoolManager.submitTask(() -> {
            return bookmarkDao.getBookmark();
        });
        try {
            return future.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public void insertImage(ImageDataModel img){
        threadsPoolManager.submitTask(()->imageDao.insertImage(img));
    }
    public void insertViewImage(ViewImageDataModel data){
        threadsPoolManager.submitTask(()->{

                viewImageDao.insertViewImage(data);

        });
    }
    public void addViewImageList(List<String> lst,String chapUrl){
        for(String str:lst){
            ViewImageDataModel data=new ViewImageDataModel();
            data.setSrc(str);
            data.setChapterUrl(chapUrl);
            insertViewImage(data);
        }
    }

    public void clearViewImage(){
        threadsPoolManager.submitTask(()-> viewImageDao.clearViewImageTable());
    }
    public void insertBookMark(BookmarkDataModel data){
        threadsPoolManager.submitTask(()->bookmarkDao.insertBookmark(data));
    }

    public List<History> getAllHistory(){
        Future<List<History>> future=threadsPoolManager.submitTask(()->{
            return historyDao.getAllHistory();
        });
        try {
            return future.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
    public void deleteHistory(History data) {
        threadsPoolManager.submitTask(() -> {
            historyDao.deleteHistory(data);
        });
    }
    public void clearHistory(){
        threadsPoolManager.submitTask(()-> {
            historyDao.clearHistory();
        });
    }

    public void insertHistory(History data){
        threadsPoolManager.submitTask(()->{
            historyDao.insertHistory(data);
        });
    }

    public static AppRepository getInstance(Context context){
        if(instance==null){
            synchronized (AppRepository.class){
                if(instance==null){
                    instance=new AppRepository(context);
                }
            }
        }
        return instance;
    }
    public static AppRepository getInstance(){
        if(instance==null) throw new RuntimeException("Context not set");
        return instance;
    }

    public void insertSetting(SettingDataModel data) {
        threadsPoolManager.submitTask(() -> settingDao.insert(data));
    }
    public SettingDataModel getSetting() {
        Future<SettingDataModel> future = threadsPoolManager.submitTask(() -> {
            return settingDao.getSettingDataModel();
        });
        try {
            return future.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void updateSetting(SettingDataModel data) {
        threadsPoolManager.submitTask(() -> settingDao.update(data));
    }

    public void insertLOV(LOVDataModel dataModel) {
        threadsPoolManager.submitTask(() -> lovDao.insertLOV(dataModel));
    }

    public LOVDataModel getLOV() {
        Future<LOVDataModel> future = threadsPoolManager.submitTask(() -> {
            return lovDao.getAllLOV();
        });
        try {
            return future.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void updateLOV(LOVDataModel dataModel) {
        threadsPoolManager.submitTask(() -> lovDao.updateLOV(dataModel));
    }
    public void deleteLOV() {
        threadsPoolManager.submitTask(() -> lovDao.deleteLOV());
    }
    public void deleteSetting() {
        threadsPoolManager.submitTask(() -> settingDao.insert(new SettingDataModel()));
    }
    public void updateLibrary(LibraryDataModel data) {
        threadsPoolManager.submitTask(() -> libraryDao.updateLibrary(data));
    }
    public void deleteLibrary(LibraryDataModel data) {
        threadsPoolManager.submitTask(() -> libraryDao.deleteLibrary(data));
    }
    public List<LibraryDataModel> getAllSortedByLastUpdatedDate(){
        Future<List<LibraryDataModel>> future = threadsPoolManager.submitTask(() -> {
            return libraryDao.getAllSortedByLastUpdatedDate();
        });
        try {
            return future.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
    public List<LibraryDataModel> getLibraryByChapter(String chapter){
        Future<List<LibraryDataModel>> future = threadsPoolManager.submitTask(() -> {
            return libraryDao.getLibraryByChapterUrl(chapter);
        });
        try {
            return future.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<LibraryDataModel> getLibraryByPageUrl(String pageUrl){
        Future<List<LibraryDataModel>> future = threadsPoolManager.submitTask(() -> {
            return libraryDao.getLibraryByPageUrl(pageUrl);
        });
        try {
            return future.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public void deleteBookmark(BookmarkDataModel data) {
        threadsPoolManager.submitTask(() -> {
            bookmarkDao.deleteBookmark(data);
        });
    }
    public List<BookmarkDataModel> getBookmarkByUrl(String url){
        Future<List<BookmarkDataModel>> future = threadsPoolManager.submitTask(() -> {
            return bookmarkDao.getBookmarkByUrl(url);
        });
        try {
            return future.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void updateBookmark(BookmarkDataModel data) {
        threadsPoolManager.submitTask(() -> {
            bookmarkDao.updateBookmark(data);
        });
    }

    public void updateReadMode(boolean readMode){
        threadsPoolManager.submitTask(()->{
            settingDao.updateReadMode(readMode);
        });
    }


}
