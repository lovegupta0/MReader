package com.LG.mreader.DataModel;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "LGviewimage")
public class ViewImageDataModel {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String chapterUrl;
    private String src;

    public ViewImageDataModel() {
    }

    public ViewImageDataModel(int id, String chapterUrl, String src) {
        this.id = id;
        this.chapterUrl = chapterUrl;
        this.src = src;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getChapterUrl() {
        return chapterUrl;
    }

    public void setChapterUrl(String chapterUrl) {
        this.chapterUrl = chapterUrl;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }
}
