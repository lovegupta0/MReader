package com.LG.mreader.DataModel;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.time.LocalDateTime;

@Entity(tableName = "lhistory")
public class History {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String url;
    @ColumnInfo(defaultValue = "CURRENT_TIMESTAMP")
    private LocalDateTime createdOn;

    public History(String url) {
        this.url = url;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(LocalDateTime createdOn) {
        this.createdOn = createdOn;
    }
}
