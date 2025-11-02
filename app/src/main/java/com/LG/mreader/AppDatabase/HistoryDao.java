package com.LG.mreader.AppDatabase;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.LG.mreader.DataModel.History;

import java.util.List;

@Dao
public interface HistoryDao {
    @Query("SELECT * FROM lhistory")
    List<History> getAllHistory();

    @Insert
    void insertHistory(History history);

    @Delete
    void deleteHistory(History history);
    @Query("SELECT * FROM lhistory ORDER BY createdOn DESC LIMIT :limit")
    List<History> getRecentHistory(int limit);

    @Query("SELECT * FROM lhistory ORDER BY createdOn DESC")
    List<History> getHistory();

    @Query("DELETE FROM lhistory")
    void clearHistory();
}
