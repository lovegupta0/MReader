package com.LG.mreader.AppDatabase;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.LG.mreader.DataModel.ImageDataModel;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface ImageDao {

    @Insert
    public void insertImage(ImageDataModel data);

    @Update
    public void updateImage(ImageDataModel data);

    @Query("select * from lgimage")
    public LiveData<List<ImageDataModel>> getImage();
}
