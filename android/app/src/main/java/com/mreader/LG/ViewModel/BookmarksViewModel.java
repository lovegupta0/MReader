package com.mreader.LG.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class BookmarksViewModel extends AndroidViewModel {
    private MutableLiveData<Boolean> addBookmark;


    public BookmarksViewModel(@NonNull Application application) {
        super(application);
        addBookmark= new MutableLiveData<>(false);
    }
    public void setAddBookmark(boolean add){
        addBookmark.setValue(add);
    }
    public LiveData<Boolean> getAddBookmark(){
        return addBookmark;
    }

}
