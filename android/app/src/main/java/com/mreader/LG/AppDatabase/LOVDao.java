package com.mreader.LG.AppDatabase;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.mreader.LG.DataModel.LOVDataModel;

import java.util.List;

@Dao
public interface LOVDao {
    @Query("SELECT  * FROM LGLOV LIMIT 1")
    public LOVDataModel getAllLOV();

    @Insert
    public void insertLOV(LOVDataModel lov);

    @Query("DELETE FROM LGLOV")
    public void deleteLOV();

    @Update
    public void updateLOV(LOVDataModel lov);

}
