package com.mreader.LG.AppDatabase;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.mreader.LG.DataModel.SettingDataModel;

@Dao
public interface SettingDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insert(SettingDataModel setting);

    @Update
    public void update(SettingDataModel setting);

    @Query("SELECT * FROM LGSetting WHERE id = :id")
    public SettingDataModel get(int id);

    @Query("SELECT * FROM LGSetting LIMIT 1")
    public SettingDataModel getSettingDataModel();
}
