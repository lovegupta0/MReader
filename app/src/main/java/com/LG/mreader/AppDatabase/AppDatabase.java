package com.LG.mreader.AppDatabase;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;

import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;

import com.LG.mreader.DataModel.BookmarkDataModel;
import com.LG.mreader.DataModel.ImageDataModel;
import com.LG.mreader.DataModel.LibraryDataModel;
import com.LG.mreader.DataModel.ViewImageDataModel;

@Database(entities = {BookmarkDataModel.class, LibraryDataModel.class, ImageDataModel.class, ViewImageDataModel.class},version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract LibraryDao libraryDao();
    public abstract BookmarkDao bookmarkDao();
    public abstract ImageDao imageDao();
    public abstract ViewImageDao viewImageDao();
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
