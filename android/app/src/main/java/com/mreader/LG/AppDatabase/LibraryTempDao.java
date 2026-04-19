package com.mreader.LG.AppDatabase;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Upsert;

import com.mreader.LG.DataModel.LibraryTempDataModel;

import java.util.List;
import java.util.Optional;

@Dao
public interface LibraryTempDao {
    @Insert
    public void insertLibrary(LibraryTempDataModel data);

    @Query("SELECT * FROM LGlibraryTemp")
    public List<LibraryTempDataModel> getLibrary();

   @Delete
    public void deleteLibrary(LibraryTempDataModel data);

    @Query("SELECT * FROM LGlibraryTemp WHERE pageUrl = :pageUrl LIMIT 1")
    public LibraryTempDataModel getLibraryByPageUrl(String pageUrl);

    @Upsert
    public void updateLibrary(LibraryTempDataModel data);
    @Query("SELECT * FROM LGlibraryTemp WHERE lastUpdateddate < :givenDate")
    public List<LibraryTempDataModel> getLibraryByLastUpdatedDate(String givenDate);

    @Query("DELETE FROM LGlibraryTemp WHERE lastUpdateddate < :givenDate")
    void deleteOldOne(String givenDate);






}
