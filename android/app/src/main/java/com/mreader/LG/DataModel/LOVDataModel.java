package com.mreader.LG.DataModel;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "LGLOV")
public class LOVDataModel {
    @PrimaryKey
    @NonNull
    private String id="LG1000";
    private List<String> homepageLOV;
    private List<String> searchEngineLOV;
    private List<String> languageLOV;
    private List<String> trackingProtectionLOV;
    private List<String> cookiePolicyLOV;
    private List<String> dataSaverLOV;
    private List<String> preloadLOV;
    private List<String> imageLoadingLOV;
    private List<String> tabDiscardingLOV;
    private List<String>  userAgentLOV;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getHomepageLOV() {
        return homepageLOV;
    }

    public void setHomepageLOV(List<String> homepageLOV) {
        this.homepageLOV = homepageLOV;
    }

    public List<String> getSearchEngineLOV() {
        return searchEngineLOV;
    }

    public void setSearchEngineLOV(List<String> searchEngineLOV) {
        this.searchEngineLOV = searchEngineLOV;
    }

    public List<String> getLanguageLOV() {
        return languageLOV;
    }

    public void setLanguageLOV(List<String> languageLOV) {
        this.languageLOV = languageLOV;
    }

    public List<String> getTrackingProtectionLOV() {
        return trackingProtectionLOV;
    }

    public void setTrackingProtectionLOV(List<String> trackingProtectionLOV) {
        this.trackingProtectionLOV = trackingProtectionLOV;
    }

    public List<String> getCookiePolicyLOV() {
        return cookiePolicyLOV;
    }

    public void setCookiePolicyLOV(List<String> cookiePolicyLOV) {
        this.cookiePolicyLOV = cookiePolicyLOV;
    }

    public List<String> getDataSaverLOV() {
        return dataSaverLOV;
    }

    public void setDataSaverLOV(List<String> dataSaverLOV) {
        this.dataSaverLOV = dataSaverLOV;
    }

    public List<String> getPreloadLOV() {
        return preloadLOV;
    }

    public void setPreloadLOV(List<String> preloadLOV) {
        this.preloadLOV = preloadLOV;
    }

    public List<String> getImageLoadingLOV() {
        return imageLoadingLOV;
    }

    public void setImageLoadingLOV(List<String> imageLoadingLOV) {
        this.imageLoadingLOV = imageLoadingLOV;
    }

    public List<String> getTabDiscardingLOV() {
        return tabDiscardingLOV;
    }

    public void setTabDiscardingLOV(List<String> tabDiscardingLOV) {
        this.tabDiscardingLOV = tabDiscardingLOV;
    }

    public List<String> getUserAgentLOV() {
        return userAgentLOV;
    }

    public void setUserAgentLOV(List<String> userAgentLOV) {
        this.userAgentLOV = userAgentLOV;
    }

    public List<String> getLOV(String key){
        key=key.toLowerCase();
        switch (key) {
            case "homepage":
                return homepageLOV;
            case "searchengine":
                return searchEngineLOV;
            case "language":
                return languageLOV;
            case "trackingprotection":
                return trackingProtectionLOV;
            case "cookiepolicy":
                return cookiePolicyLOV;
            case "datasaver":
                return dataSaverLOV;
            case "preload":
                return preloadLOV;
            case "imageloadingL":
                return imageLoadingLOV;
            case "tabdiscarding":
                return tabDiscardingLOV;
            case "useragent":
                return userAgentLOV;
            default:
                return new ArrayList<>();
        }
    }
}
