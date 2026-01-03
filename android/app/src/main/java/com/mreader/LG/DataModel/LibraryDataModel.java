package com.mreader.LG.DataModel;

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
    private String chapterUrl;
    private String latestchapter;
    private String latestChapterUpdated;
    private String coverUrl;

    @ColumnInfo(name="last_updated_date")
    private String LastUpdateddate;

    public LibraryDataModel() {
    }

    public LibraryDataModel(String title, String pageUrl, String baseUrl, String chapterUrl, String lastUpdateddate) {
        this.title = title;
        this.pageUrl = pageUrl;
        this.baseUrl = baseUrl;
        this.chapterUrl = chapterUrl;
        LastUpdateddate = lastUpdateddate;
    }

    public int getId() {
        return id;
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



    public String getLastUpdateddate() {
        return LastUpdateddate;
    }

    public void setLastUpdateddate(String lastUpdateddate) {
        LastUpdateddate = lastUpdateddate;
    }
    public String getChapterUrl() {
        return chapterUrl;
    }

    public void setChapterUrl(String chapterUrl) {
        this.chapterUrl = chapterUrl;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLatestchapter() {
        return latestchapter;
    }

    public void setLatestchapter(String latestchapter) {
        this.latestchapter = latestchapter;
    }

    public String getLatestChapterUpdated() {
        return latestChapterUpdated;
    }

    public void setLatestChapterUpdated(String latestChapterUpdated) {
        this.latestChapterUpdated = latestChapterUpdated;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }
}
