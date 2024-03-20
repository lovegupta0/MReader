package com.LG.mreader.DataModel;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "LGlibrary")
public class LibraryDataModel {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private String pageUrl;
    private String baseUrl;
    private int chapter;
    @ColumnInfo(name="last_updated_date")
    private String LastUpdateddate;

    public LibraryDataModel() {
    }

    public LibraryDataModel(int id, String title, String pageUrl, String baseUrl, int chapter, String lastUpdateddate) {
        this.id = id;
        this.title = title;
        this.pageUrl = pageUrl;
        this.baseUrl = baseUrl;
        this.chapter = chapter;
        LastUpdateddate = lastUpdateddate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPageUrl() {
        return pageUrl;
    }

    public void setPageUrl(String pageUrl) {
        this.pageUrl = pageUrl;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public int getChapter() {
        return chapter;
    }

    public void setChapter(int chapter) {
        this.chapter = chapter;
    }

    public String getLastUpdateddate() {
        return LastUpdateddate;
    }

    public void setLastUpdateddate(String lastUpdateddate) {
        LastUpdateddate = lastUpdateddate;
    }
}
