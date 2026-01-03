package com.mreader.LG.AppDatabase;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.mreader.LG.DataModel.BookmarkDataModel;

import java.util.List;

@Dao
public interface BookmarkDao {
    @Insert
    public void insertBookmark(BookmarkDataModel data);

    @Delete
    public void deleteBookmark(BookmarkDataModel data);

    @Query("select * from lgbookmark")
    public List<BookmarkDataModel> getBookmark();

    @Query("select * from lgbookmark where address=:url")
    public List<BookmarkDataModel> getBookmarkByUrl(String url);

    @Update
    public void updateBookmark(BookmarkDataModel data);
}
