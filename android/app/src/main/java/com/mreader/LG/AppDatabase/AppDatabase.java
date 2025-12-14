package com.mreader.LG.AppDatabase;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.mreader.LG.Common.*;
import com.mreader.LG.DataModel.BookmarkDataModel;
import com.mreader.LG.DataModel.History;
import com.mreader.LG.DataModel.ImageDataModel;
import com.mreader.LG.DataModel.LOVDataModel;
import com.mreader.LG.DataModel.LibraryDataModel;
import com.mreader.LG.DataModel.SettingDataModel;
import com.mreader.LG.DataModel.ViewImageDataModel;

@Database(
        entities = {
                BookmarkDataModel.class,
                LibraryDataModel.class,
                ImageDataModel.class,
                ViewImageDataModel.class,
                History.class,
                LOVDataModel.class,
                SettingDataModel.class
        },
        version = 1,
        exportSchema = false
)
@TypeConverters({
        Converters.class,
        JsonTypeConverter.class
})
public abstract class AppDatabase extends RoomDatabase {
    public abstract LibraryDao libraryDao();
    public abstract BookmarkDao bookmarkDao();
    public abstract ImageDao imageDao();
    public abstract ViewImageDao viewImageDao();
    public abstract HistoryDao historyDao();
    public abstract LOVDao lovDao();
    public abstract SettingDao settingDao();



    private static volatile AppDatabase INSTANCE;
    public static AppDatabase getInstance(Context context){
        if(INSTANCE==null){
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "LGDB"
                    ).build();
                }
            }
        }
        return INSTANCE;
    }
}
