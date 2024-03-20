package com.LG.mreader.AppRepository;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.LG.mreader.AppDatabase.AppDatabase;
import com.LG.mreader.AppDatabase.BookmarkDao;
import com.LG.mreader.AppDatabase.ImageDao;
import com.LG.mreader.AppDatabase.LibraryDao;
import com.LG.mreader.AppDatabase.ViewImageDao;
import com.LG.mreader.DataModel.BookmarkDataModel;
import com.LG.mreader.DataModel.ImageDataModel;
import com.LG.mreader.DataModel.LibraryDataModel;
import com.LG.mreader.DataModel.ViewImageDataModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import kotlinx.coroutines.Dispatchers;



import kotlinx.coroutines.CoroutineScope;

public class AppRepository {
    private final LibraryDao libraryDao;
    private final BookmarkDao bookmarkDao;
    private final ImageDao imageDao;
    private final ViewImageDao viewImageDao;
    private final ExecutorService executorService;

    public AppRepository(Context context){
        AppDatabase database=AppDatabase.getInstance(context);
        libraryDao=database.libraryDao();
        bookmarkDao= database.bookmarkDao();
        imageDao=database.imageDao();
        viewImageDao= database.viewImageDao();
        executorService = Executors.newSingleThreadExecutor();
    }
    public void insertLibrary(LibraryDataModel data){
        executorService.execute(()->libraryDao.insertLibrary(data));
    }

    public LiveData<List<LibraryDataModel>> getLibraryData(){
        return libraryDao.getLibrary();
    }
    public LiveData<List<ImageDataModel>> getImage(){
        return imageDao.getImage();
    }
    public LiveData<List<ViewImageDataModel>> getViewImage(){
            return viewImageDao.getViewImage();
    }
    public LiveData<List<BookmarkDataModel>> getBookmarks(){return  bookmarkDao.getBookmark();}
    public void insertImage(ImageDataModel img){
        executorService.execute(()->imageDao.insertImage(img));
    }
    public void insertViewImage(ViewImageDataModel data){
        executorService.execute(()->{

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
        executorService.execute(()-> viewImageDao.clearViewImageTable());
    }
    public void insertBookMark(BookmarkDataModel data){
        executorService.execute(()->bookmarkDao.insertBookmark(data));
    }
}
