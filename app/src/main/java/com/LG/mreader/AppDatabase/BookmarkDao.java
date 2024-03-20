package com.LG.mreader.AppDatabase;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;


import com.LG.mreader.DataModel.BookmarkDataModel;

import java.util.List;

@Dao
public interface BookmarkDao {
    @Insert
    public void insertBookmark(BookmarkDataModel data);

    @Delete
    public void deleteBookmark(BookmarkDataModel data);

    @Query("select * from lgbookmark")
    public LiveData<List<BookmarkDataModel>> getBookmark();
}
