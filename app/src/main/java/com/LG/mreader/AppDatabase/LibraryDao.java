package com.LG.mreader.AppDatabase;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.LG.mreader.DataModel.LibraryDataModel;


import java.util.List;

@Dao
public interface LibraryDao {
    @Insert
    public void insertLibrary(LibraryDataModel data);

    @Delete
    public void deleteLibrary(LibraryDataModel data);
    @Query("select * from lglibrary")
    public LiveData<List<LibraryDataModel>> getLibrary();

}
