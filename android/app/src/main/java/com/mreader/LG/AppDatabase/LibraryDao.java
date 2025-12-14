package com.mreader.LG.AppDatabase;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.mreader.LG.DataModel.LibraryDataModel;

import java.util.List;

@Dao
public interface LibraryDao {
    @Insert
    public void insertLibrary(LibraryDataModel data);

    @Delete
    public void deleteLibrary(LibraryDataModel data);
    @Query("select * from lglibrary")
    public LiveData<List<LibraryDataModel>> getLibrary();

    @Update
    public void updateLibrary(LibraryDataModel data);
    @Query("SELECT * FROM LGlibrary ORDER BY last_updated_date DESC")
    List<LibraryDataModel> getAllSortedByLastUpdatedDate();

}
