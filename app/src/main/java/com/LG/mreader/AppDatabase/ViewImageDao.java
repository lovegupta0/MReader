package com.LG.mreader.AppDatabase;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.LG.mreader.DataModel.ViewImageDataModel;

import java.util.List;

@Dao
public interface ViewImageDao {

    @Insert
    public void insertViewImage(ViewImageDataModel data);

    @Update
    public void updateViewImage(ViewImageDataModel data);

    @Query("select * from lgviewimage")
    public LiveData<List<ViewImageDataModel>> getViewImage();

    @Query("delete from lgviewimage")
    public void clearViewImageTable();
}
