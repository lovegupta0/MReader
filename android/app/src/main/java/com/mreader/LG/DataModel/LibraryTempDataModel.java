package com.mreader.LG.DataModel;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "LGlibraryTemp")
public class LibraryTempDataModel {
    private String coverUrl;
    @PrimaryKey
    @NonNull
    private String pageUrl;
    private String lastUpdateddate;
    private String latestchapter;
    private String latestChapterUpdated;

    public LibraryTempDataModel() {
    }

    public LibraryTempDataModel(String coverUrl, String pageUrl, String lastUpdateddate, String latestchapter, String latestChapterUpdated) {
        this.coverUrl = coverUrl;
        this.pageUrl = pageUrl;
        this.lastUpdateddate = lastUpdateddate;
        this.latestchapter = latestchapter;
        this.latestChapterUpdated = latestChapterUpdated;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public String getPageUrl() {
        return pageUrl;
    }

    public void setPageUrl(String pageUrl) {
        this.pageUrl = pageUrl;
    }

    public String getLastUpdateddate() {
        return lastUpdateddate;
    }

    public void setLastUpdateddate(String lastUpdateddate) {
        this.lastUpdateddate = lastUpdateddate;
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

    @Override
    public String toString() {
        return "LibraryTempDataModel{" +
                "coverUrl='" + coverUrl + '\'' +
                ", pageUrl='" + pageUrl + '\'' +
                ", lastUpdateddate='" + lastUpdateddate + '\'' +
                ", latestchapter='" + latestchapter + '\'' +
                ", latestChapterUpdated='" + latestChapterUpdated + '\'' +
                '}';
    }
}
