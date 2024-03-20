package com.LG.mreader.DataModel;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "LGbookmark")
public class BookmarkDataModel {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String address;
    private String title;

    public BookmarkDataModel() {
    }

    public BookmarkDataModel(int id, String address, String title) {
        this.id = id;
        this.address = address;
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
